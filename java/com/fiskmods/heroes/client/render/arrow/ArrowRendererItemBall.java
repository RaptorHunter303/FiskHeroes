package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.item.ItemStack;

public class ArrowRendererItemBall extends ArrowRenderer
{
    public final ItemStack itemType;

    public ArrowRendererItemBall(ItemStack stack)
    {
        itemType = stack;
    }

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
            GL11.glTranslatef(0.1125F, 0.59F, 0.01F);
            GL11.glRotatef(-335, 0, 0, 1);
            GL11.glRotatef(-50, 0, 1, 0);
            GL11.glScalef(scale, scale, scale);
            itemRenderer.renderItem(mc.thePlayer, itemType, 0);
            GL11.glPopMatrix();
        }
    }
}
