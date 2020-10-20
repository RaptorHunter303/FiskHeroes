package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ArrowRendererCactus extends ArrowRenderer
{
    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        if (!inQuiver)
        {
            GL11.glPushMatrix();
            float f = 0.2F;
            GL11.glScalef(f, f, f);
            GL11.glRotatef(180, 0, 0, 1);
            GL11.glTranslatef(0, 1.4F, 0);

            for (int i = 0; i < 2; ++i)
            {
                GL11.glTranslatef(0, 1, 0);
                itemRenderer.renderItem(mc.thePlayer, new ItemStack(Blocks.cactus), 0);
            }

            GL11.glPopMatrix();
        }
    }
}
