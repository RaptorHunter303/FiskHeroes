package com.fiskmods.heroes.common.event;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.item.IBattlegearSheathed;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.battlegear2.api.RenderPlayerEventChild.PlayerElementType;
import mods.battlegear2.api.RenderPlayerEventChild.PreRenderSheathed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public enum ClientEventHandlerBG
{
    INSTANCE;

    @SubscribeEvent
    public void onPreRenderSheathed(PreRenderSheathed event)
    {
        EntityPlayer player = event.entityPlayer;
        ItemStack itemstack = event.element;

        if (itemstack != null)
        {
            if (itemstack.getItem() instanceof IBattlegearSheathed)
            {
                IBattlegearSheathed sheathed = (IBattlegearSheathed) itemstack.getItem();

                if (!sheathed.renderSheathed(event.isOnBack))
                {
                    event.setCanceled(true);
                    return;
                }
            }

            if (!event.isOnBack)
            {
                boolean flag = player.getCurrentArmor(2) != null || player.getCurrentArmor(1) != null;

                if (player.getCurrentArmor(2) != null && SHHelper.getHeroFromArmor(player, 2) == null || player.getCurrentArmor(1) != null && SHHelper.getHeroFromArmor(player, 1) == null)
                {
                    flag = false;
                }

                if (flag)
                {
                    float f = 0.065F;

                    if (event.type == PlayerElementType.ItemMainhandSheathed)
                    {
                        f = -0.05F;
                    }

                    GL11.glTranslatef(f, 0, f);
                }
            }
        }
    }
}
