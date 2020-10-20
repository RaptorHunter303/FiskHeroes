package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArrowRendererPufferfish extends ArrowRenderer
{
    public final boolean isExplosive;

    public ArrowRendererPufferfish(boolean explosive)
    {
        isExplosive = explosive;
    }

    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        if (!inQuiver)
        {
            float scale = 0.21F;
            GL11.glPushMatrix();
            GL11.glScalef(1, 1, 2);
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(180, 0, 1, 0);
            GL11.glTranslatef(0.1125F, 0.59F, 0.01F);
            GL11.glRotatef(-335, 0, 0, 1);
            GL11.glRotatef(-50, 0, 1, 0);
            GL11.glScalef(scale, scale, scale);

            itemRenderer.renderItem(mc.thePlayer, new ItemStack(Items.fish, 1, 3), 0);
            GL11.glPopMatrix();

            if (isExplosive)
            {
                scale = 0.1F;
                GL11.glPushMatrix();
                GL11.glScalef(scale, scale, scale);
                GL11.glRotatef(90, 0, 0, 1);

                for (int i = 0; i < 3; ++i)
                {
                    GL11.glTranslatef(-1, 0, 0);
                    itemRenderer.renderItem(mc.thePlayer, new ItemStack(Blocks.tnt), 0);
                }

                GL11.glPopMatrix();
            }
        }
    }
}
