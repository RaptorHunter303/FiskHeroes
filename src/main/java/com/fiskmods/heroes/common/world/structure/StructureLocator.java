package com.fiskmods.heroes.common.world.structure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public class StructureLocator extends Thread
{
    public final WeakHashMap<World, Map<StructureType, List<ChunkCoordIntPair>>> cachedStructurePos = new WeakHashMap<>();

    private final CommandBase command;
    private final ICommandSender sender;
    private final StructureType structureType;

    private final boolean targetAll;
    private final int maxRange;
    private final int startX;
    private final int startZ;

    public StructureLocator(CommandBase commandBase, ICommandSender commandSender, StructureType type, boolean all, int range, int x, int z)
    {
        command = commandBase;
        sender = commandSender;
        structureType = type;
        targetAll = all;
        maxRange = range;
        startX = x;
        startZ = z;
    }

    public static void locate(CommandBase commandBase, ICommandSender commandSender, StructureType type, boolean all, int range, int x, int z, int millis)
    {
        Thread t = new StructureLocator(commandBase, commandSender, type, all, range, x, z);
        new Thread(() ->
        {
            t.start();

            try
            {
                t.join(millis);
            }
            catch (InterruptedException e)
            {
            }

            if (t.isAlive())
            {
                t.interrupt();
            }
        }).start();
    }

    @Override
    public void run()
    {
        World world = sender.getEntityWorld();
        int x = startX >> 4;
        int z = startZ >> 4;
        int range = 0;

        if (!containsStructure(world, x, z))
        {
            l: while (++range <= maxRange)
            {
                for (int i = x - range; i <= x + range; ++i)
                {
                    if (containsStructure(world, i, range) || containsStructure(world, i, -range))
                    {
                        break l;
                    }
                }

                for (int i = z - range + 1; i <= z + range - 1; ++i)
                {
                    if (containsStructure(world, range, i) || containsStructure(world, -range, i))
                    {
                        break l;
                    }
                }
            }
        }

        Vec3 vec = Vec3.createVectorHelper(x, 0, z);
        Comparator<ChunkCoordIntPair> c = Comparator.comparingDouble(t -> Vec3.createVectorHelper(t.chunkXPos, 0, t.chunkZPos).distanceTo(vec));

        for (StructureType type : StructureType.values())
        {
            getStructures(world, type).sort(c);
        }

        if (targetAll)
        {
            List<ChunkCoordIntPair> structures = new ArrayList<>();

            for (StructureType type : StructureType.values())
            {
                if (!getStructures(world, type).isEmpty())
                {
                    structures.add(getStructures(world, type).get(0));
                }
            }

            if (structures.isEmpty())
            {
                ChatComponentTranslation message = new ChatComponentTranslation("commands.fiskheroes.structure.locate.failure");
                message.getChatStyle().setColor(EnumChatFormatting.RED);

                sender.addChatMessage(message);
            }
            else
            {
                structures.sort(c);
                ChunkCoordIntPair chunk = structures.get(0);

                for (StructureType type : StructureType.values())
                {
                    if (!getStructures(world, type).isEmpty() && chunk.equals(getStructures(world, type).get(0)))
                    {
                        CommandBase.func_152373_a(sender, command, "commands.fiskheroes.structure.locate.success", type.key, chunk.getCenterXPos(), chunk.getCenterZPosition());
                        break;
                    }
                }
            }
        }
        else
        {
            List<ChunkCoordIntPair> structures = getStructures(world, structureType);

            if (structures.isEmpty())
            {
                ChatComponentTranslation message = new ChatComponentTranslation("commands.fiskheroes.structure.locate.failure");
                message.getChatStyle().setColor(EnumChatFormatting.RED);

                sender.addChatMessage(message);
            }
            else
            {
                ChunkCoordIntPair chunk = structures.get(0);
                CommandBase.func_152373_a(sender, command, "commands.fiskheroes.structure.locate.success", structureType.key, chunk.getCenterXPos(), chunk.getCenterZPosition());
            }
        }
    }

    public boolean containsStructure(World world, int chunkX, int chunkZ)
    {
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(chunkX, chunkZ);

        for (StructureType type : StructureType.values())
        {
            if (StructureGenerator.canStructureGenerateAtCoords(world, chunk.getCenterXPos(), chunk.getCenterZPosition(), type))
            {
                if (!getStructures(world, type).contains(chunk))
                {
                    getStructures(world, type).add(chunk);
                }

                if (structureType == type || targetAll)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public Map<StructureType, List<ChunkCoordIntPair>> getStructures(World world)
    {
        return cachedStructurePos.computeIfAbsent(world, t -> new HashMap<>());
    }

    public List<ChunkCoordIntPair> getStructures(World world, StructureType type)
    {
        return getStructures(world).computeIfAbsent(type, t -> new ArrayList<>());
    }
}
