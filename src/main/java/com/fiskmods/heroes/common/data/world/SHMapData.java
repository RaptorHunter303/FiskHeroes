package com.fiskmods.heroes.common.data.world;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.BlockStack;
import com.fiskmods.heroes.common.DimensionalCoords;
import com.fiskmods.heroes.common.config.RuleHandler;
import com.fiskmods.heroes.common.config.RuleSet;
import com.fiskmods.heroes.common.tileentity.TileEntityRuleHandler;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants.NBT;

public class SHMapData extends WorldSavedData
{
    public static final String KEY = FiskHeroes.MODID + "_data";

    public Map<Block, Map<BlockStack, Integer>> ores = new HashMap<>();
    private Map<DimensionalCoords, Integer> power = new HashMap<>();

    private ArrayDeque<DimensionalCoords> ruleHandlerQueue = new ArrayDeque<>();
    private Set<TileEntityRuleHandler> ruleHandlers = new HashSet<>();
    private Map<Long, RuleSet> ruleAreas = new HashMap<>();
    private boolean ruleAreasDirty;

    public float forceSlow;

    public SHMapData(String s)
    {
        super(s);
    }

    public static SHMapData get(World world)
    {
        MapStorage storage = world.mapStorage;
        SHMapData instance = (SHMapData) storage.loadData(SHMapData.class, KEY);

        if (instance == null)
        {
            instance = new SHMapData(KEY);
            instance.markDirty();
            storage.setData(KEY, instance);
        }

        return instance;
    }

    public void onUpdate(World world)
    {
        if (!power.isEmpty())
        {
            power.entrySet().removeIf(e ->
            {
                if (e.getKey().dimension == world.provider.dimensionId && e.setValue(e.getValue() - 1) <= 0)
                {
                    int x = e.getKey().posX;
                    int y = e.getKey().posY;
                    int z = e.getKey().posZ;

                    if (world.getChunkProvider().chunkExists(x >> 4, z >> 4))
                    {
                        Block block = world.getBlock(x, y, z);
                        world.notifyBlockOfNeighborChange(x, y, z, block);
                        world.markAndNotifyBlock(x, y, z, null, block, block, 3);

                        return true;
                    }
                }

                return false;
            });
        }

        if (!ruleHandlerQueue.isEmpty())
        {
            DimensionalCoords coords;

            while ((coords = ruleHandlerQueue.poll()) != null)
            {
                if (coords.dimension == world.provider.dimensionId)
                {
                    TileEntity tile = world.getTileEntity(coords.posX, coords.posY, coords.posZ);

                    if (tile instanceof TileEntityRuleHandler)
                    {
                        addRuleHandler((TileEntityRuleHandler) tile);
                    }
                }
            }
        }

        if (ruleAreasDirty)
        {
            ruleAreasDirty = false;
            ruleAreas.clear();

            for (TileEntityRuleHandler tile : new HashSet<>(ruleHandlers))
            {
                int r = tile.chunkRadius;

                for (long chunkX = -r; chunkX <= r; ++chunkX)
                {
                    for (long chunkZ = -r; chunkZ <= r; ++chunkZ)
                    {
                        ruleAreas.put(chunkX | chunkZ << 32L, tile.ruleSet);
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        if (nbt.hasKey("OreGen", NBT.TAG_LIST))
        {
            NBTTagList list = nbt.getTagList("OreGen", NBT.TAG_COMPOUND);

            for (int i = 0; i < list.tagCount(); ++i)
            {
                NBTTagCompound oreGen = list.getCompoundTagAt(i);
                BlockStack target = BlockStack.fromString(oreGen.getString("Target"));

                if (target != null)
                {
                    Map<BlockStack, Integer> size = getOreGen(target.block);
                    NBTTagList ores = oreGen.getTagList("Ores", NBT.TAG_COMPOUND);

                    for (int j = 0; j < ores.tagCount(); ++j)
                    {
                        NBTTagCompound tag = ores.getCompoundTagAt(j);
                        BlockStack ore = BlockStack.fromString(tag.getString("id"));

                        if (ore != null)
                        {
                            size.put(ore, tag.getInteger("size"));
                        }
                    }
                }
            }
        }

        if (nbt.hasKey("Power", NBT.TAG_INT_ARRAY))
        {
            int[] aint = nbt.getIntArray("Power");

            if (aint.length > 0)
            {
                int i = 0;

                for (int j = 0; j < aint.length; ++j)
                {
                    if (++i > 5)
                    {
                        power.put(new DimensionalCoords(aint[j - 4], aint[j - 3], aint[j - 2], aint[j - 1]), aint[j]);
                        i = 0;
                    }
                }
            }
        }

        if (nbt.hasKey("RuleHandlers", NBT.TAG_INT_ARRAY))
        {
            int[] aint = nbt.getIntArray("RuleHandlers");

            if (aint.length > 0)
            {
                int i = 0;

                for (int j = 0; j < aint.length; ++j)
                {
                    if (++i > 4)
                    {
                        ruleHandlerQueue.add(new DimensionalCoords(aint[j - 3], aint[j - 2], aint[j - 1], aint[j]));
                        i = 0;
                    }
                }
            }
        }

        forceSlow = nbt.getFloat("ForceSlow");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        if (!ores.isEmpty())
        {
            NBTTagList list = new NBTTagList();

            for (Map.Entry<Block, Map<BlockStack, Integer>> e : ores.entrySet())
            {
                NBTTagCompound oreGen = new NBTTagCompound();
                NBTTagList ores = new NBTTagList();

                for (Map.Entry<BlockStack, Integer> e1 : e.getValue().entrySet())
                {
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setString("id", BlockStack.toStringSafe(e1.getKey()));
                    tag.setInteger("size", e1.getValue());
                    ores.appendTag(tag);
                }

                oreGen.setString("Target", BlockStack.toStringSafe(new BlockStack(e.getKey())));
                oreGen.setTag("Ores", ores);
                list.appendTag(oreGen);
            }

            nbt.setTag("OreGen", list);
        }

        if (!power.isEmpty())
        {
            int[] aint = new int[power.size() * 5];
            int i = -1;

            for (Map.Entry<DimensionalCoords, Integer> e : power.entrySet())
            {
                aint[++i] = e.getKey().posX;
                aint[++i] = e.getKey().posY;
                aint[++i] = e.getKey().posZ;
                aint[++i] = e.getKey().dimension;
                aint[++i] = e.getValue();
            }

            nbt.setIntArray("Power", aint);
        }

        if (!ruleHandlers.isEmpty())
        {
            int[] aint = new int[ruleHandlers.size() * 4];
            int i = -1;

            for (TileEntityRuleHandler tile : ruleHandlers)
            {
                aint[++i] = tile.xCoord;
                aint[++i] = tile.yCoord;
                aint[++i] = tile.zCoord;
                aint[++i] = tile.getWorldObj().provider.dimensionId;
            }

            nbt.setIntArray("RuleHandlers", aint);
        }

        nbt.setFloat("ForceSlow", forceSlow);
    }

    public Map<BlockStack, Integer> getOreGen(Block target)
    {
        return ores.computeIfAbsent(target, k -> new HashMap<>());
    }

    public int getOreGen(Block target, BlockStack ore)
    {
        return getOreGen(target).getOrDefault(ore, 0);
    }

    public void power(World world, int x, int y, int z, int direction)
    {
        power.put(new DimensionalCoords(x, y, z, world.provider.dimensionId), 10);
    }

    public int getPower(World world, int x, int y, int z)
    {
        return power.isEmpty() ? 0 : power.getOrDefault(new DimensionalCoords(x, y, z, world.provider.dimensionId), 0) > 0 ? 15 : 0;
    }

    public void addRuleHandler(TileEntityRuleHandler tile)
    {
        ruleHandlers.add(tile);
        ruleAreasDirty = true;
    }

    public void removeRuleHandler(TileEntityRuleHandler tile)
    {
        ruleHandlers.remove(tile);
        ruleAreasDirty = true;
    }

    public RuleSet getRuleSet(World world, int x, int z)
    {
        return ruleAreas.getOrDefault(x >> 4 | z >> 4 << 32L, RuleHandler.getGlobal());
    }

    public void notifyRuleSetChange(String key)
    {
        for (TileEntityRuleHandler tile : ruleHandlers)
        {
            tile.notifyRuleSetChange(key);
        }
    }
}
