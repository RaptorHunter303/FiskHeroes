package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArrowRendererGross extends ArrowRenderer
{
    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        if (!inQuiver)
        {
            float scale = 0.21F;
            GL11.glPushMatrix();
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(180, 0, 1, 0);

            for (int i = 0; i < 5; ++i)
            {
                GL11.glPushMatrix();
                GL11.glRotatef(i * i * 137, 0, 1, 0);
                GL11.glScalef(1, 1, 1.5F);
                GL11.glTranslatef(0.1125F, 0.4F - i * 0.06F, 0.01F);
                GL11.glRotatef(-335, 0, 0, 1);
                GL11.glRotatef(-50, 0, 1, 0);
                GL11.glScalef(scale, scale, scale);
                itemRenderer.renderItem(mc.thePlayer, new ItemStack(Items.nether_wart), 0);
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
        }
    }
}
