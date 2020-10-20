package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArrowRendererCarrot extends ArrowRenderer
{
    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        if (!inQuiver)
        {
            float scale = 0.3F;
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.03F, -0.8F, -0.013F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(205, 0, 0, 1);
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(-50, 0, 1, 0);
            itemRenderer.renderItem(mc.thePlayer, new ItemStack(Items.carrot), 0);
            GL11.glPopMatrix();
        }
    }
}
