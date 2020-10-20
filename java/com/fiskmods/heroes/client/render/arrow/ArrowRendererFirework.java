package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArrowRendererFirework extends ArrowRenderer
{
    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        if (!inQuiver)
        {
            float scale = 0.4F;
            GL11.glPushMatrix();
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(180, 0, 1, 0);
            GL11.glTranslatef(0.23F, 0.5F, 0.0125F);
            GL11.glRotatef(-335, 0, 0, 1);
            GL11.glRotatef(-50, 0, 1, 0);
            GL11.glScalef(scale, scale, scale);

            itemRenderer.renderItem(mc.thePlayer, new ItemStack(Items.fireworks), 0);
            GL11.glPopMatrix();
        }
    }
}
