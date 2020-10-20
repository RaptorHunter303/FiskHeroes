package com.fiskmods.heroes.common.interaction;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public abstract class Interaction
{
    public static final Map<String, Interaction> REGISTRY = new HashMap<>();

    public static void register(String key, Interaction interaction)
    {
        interaction.key = key;
        REGISTRY.put(key, interaction);
    }

    public static Interaction getInteraction(String key)
    {
        return REGISTRY.get(key);
    }

    public static final TargetPoint TARGET_NONE = new TargetPoint(0, 0, 0, 0, 0);
    public static final TargetPoint TARGET_ALL = null;

    private String key;

    public abstract boolean listen(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z);

    public abstract void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z);

    public boolean tryListen(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (listen(sender, clientPlayer, type, side, x, y, z))
        {
            receive(sender, clientPlayer, type, side, x, y, z);
            return true;
        }

        return false;
    }

    public boolean syncWithServer()
    {
        return true;
    }

    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return new TargetPoint(player.dimension, x, y, z, 32);
    }

    public String getRegistryKey()
    {
        return key;
    }
}
