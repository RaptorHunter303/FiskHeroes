package com.fiskmods.heroes.client.render.arrow;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

public class ArrowRendererTriple extends ArrowRenderer
{
    public final ArrowType tripleType;

    public ArrowRendererTriple(ArrowType triple)
    {
        tripleType = triple;
    }

    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        ArrowRenderer renderer = ArrowRenderer.get(tripleType);

        if (renderer == null)
        {
            return;
        }

        if (!inQuiver)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.01F, 0, 0);
            renderer.render(arrow, x, y, z, partialTicks, inQuiver);

            GL11.glPushMatrix();
            GL11.glTranslatef(0.03F, 0, 0);
            GL11.glRotatef(3.5F, 0, 0, 1);
            GL11.glRotatef(2, 1, 0, 0);
            renderer.render(arrow, x, y, z, partialTicks, inQuiver);
            GL11.glPopMatrix();

            GL11.glTranslatef(-0.03F, 0, 0);
            GL11.glRotatef(-3.5F, 0, 0, 1);
            GL11.glRotatef(-2, 1, 0, 0);
            renderer.render(arrow, x, y, z, partialTicks, inQuiver);
            GL11.glPopMatrix();
        }
        else
        {
            for (int l = 0; l < 3; ++l)
            {
                GL11.glPushMatrix();
                Random rand = new Random((l + 1) * 1000000000);
                GL11.glTranslatef((rand.nextFloat() / 10 - 0.05F) * 0.5F, 0.0F, (rand.nextFloat() / 10 - 0.05F) * 0.5F);

                renderer.render(arrow, x, y, z, partialTicks, false);
                GL11.glPopMatrix();
            }
        }
    }
}
