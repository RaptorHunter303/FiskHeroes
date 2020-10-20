package com.fiskmods.heroes.common.interaction;

import java.util.HashSet;
import java.util.Set;

import com.fiskmods.heroes.common.network.MessageInteraction;
import com.fiskmods.heroes.common.network.SHNetworkManager;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public enum InteractionType
{
    KEY_PRESS,
    KEY_HOLD,
    RIGHT_CLICK_AIR,
    RIGHT_CLICK_BLOCK,
    LEFT_CLICK_BLOCK;

    static final Set<Interaction> LISTENERS = new HashSet<>();

    public boolean interact(EntityPlayer player, int x, int y, int z, boolean queued)
    {
        boolean flag = false;

        if (player.worldObj.isRemote)
        {
            for (Interaction interaction : Interaction.REGISTRY.values())
            {
                if (interaction.tryListen(player, player, this, Side.CLIENT, x, y, z))
                {
                    if (interaction.syncWithServer())
                    {
                        SHNetworkManager.wrapper.sendToServer(new MessageInteraction(player, interaction, this, x, y, z));
                    }

                    if (queued)
                    {
                        return true;
                    }
                    else
                    {
                        flag = true;
                    }
                }
            }
        }

        return flag;
    }

    public static InteractionType get(Action action)
    {
        switch (action)
        {
        case RIGHT_CLICK_AIR:
            return RIGHT_CLICK_AIR;
        case RIGHT_CLICK_BLOCK:
            return RIGHT_CLICK_BLOCK;
        case LEFT_CLICK_BLOCK:
            return LEFT_CLICK_BLOCK;
        }

        return KEY_PRESS;
    }
}
