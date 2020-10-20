package com.fiskmods.heroes.common.interaction;

import java.util.Locale;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.achievement.SHAchievements;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;

public class InteractionBatfish extends Interaction
{
    @Override
    public boolean listen(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (type == InteractionType.RIGHT_CLICK_BLOCK)
        {
            TileEntity tile = sender.worldObj.getTileEntity(x, y, z);

            if (tile instanceof TileEntitySign)
            {
                TileEntitySign sign = (TileEntitySign) tile;

                for (String s : sign.signText)
                {
                    if (s.toLowerCase(Locale.ROOT).contains("batfish"))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            TileEntity tile = sender.worldObj.getTileEntity(x, y, z);

            if (tile instanceof TileEntitySign)
            {
                TileEntitySign sign = (TileEntitySign) tile;

                for (String s : sign.signText)
                {
                    if (s.matches("bAtFiSh|BaTfIsH"))
                    {
                        sender.triggerAchievement(SHAchievements.SECRET);
                        break;
                    }
                }
            }

            sender.worldObj.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, SHSounds.RANDOM_BATFISH.toString(), 1.0F, 1.0F);
        }
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
