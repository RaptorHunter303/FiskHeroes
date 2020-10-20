package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ArrowRendererExplosive extends ArrowRenderer
{
    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);
        float f = 0.1F;
        GL11.glPushMatrix();
        GL11.glScalef(f, f, f);
        GL11.glRotatef(90, 0, 0, 1);

        for (int i = 0; i < 3; ++i)
        {
            GL11.glTranslatef(-1, 0, 0);
            itemRenderer.renderItem(mc.thePlayer, new ItemStack(Blocks.tnt), 0);
        }

        GL11.glPopMatrix();
    }
}
