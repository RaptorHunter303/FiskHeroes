package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.model.item.ModelBoxingGlove;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

public class ArrowRendererBoxingGlove extends ArrowRenderer
{
    protected ModelBoxingGlove model = new ModelBoxingGlove();

    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        if (!inQuiver)
        {
            float scale = 0.75F;
            GL11.glPushMatrix();
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0, -0.5F, 0);
            model.render();
            GL11.glPopMatrix();
        }
    }
}
