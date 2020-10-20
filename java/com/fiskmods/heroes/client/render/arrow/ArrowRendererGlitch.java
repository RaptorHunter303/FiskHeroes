package com.fiskmods.heroes.client.render.arrow;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.util.SHRenderHelper;

public class ArrowRendererGlitch extends ArrowRenderer
{
    private Random rand = new Random();

    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        if (inQuiver)
        {
            super.render(arrow, x, y, z, partialTicks, inQuiver);
            return;
        }

        long l = arrow.ticksExisted;
        rand.setSeed(-28964355487L + l * 100000);
        mc.getTextureManager().bindTexture(getTexture(arrow));

        if (rand.nextFloat() < 0.75F)
        {
            model.render(0.0625F);
            return;
        }

        int q = rand.nextInt(3) + 2;
        float[][][] afloat = new float[2][q][3];

        for (int i = 0; i < 2; ++i)
        {
            rand.setSeed(-28964355487L + (l - i) * 100000);

            for (int j = 0; j < q; ++j)
            {
                for (int k = 0; k < 3; ++k)
                {
                    afloat[i][j][k] = rand.nextFloat() * 2 - 1;
                }
            }
        }

        float spread = 0.2F;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        for (int i = 0; i < q; ++i)
        {
            float f0 = SHRenderHelper.interpolate(afloat[0][i][0], afloat[1][i][0]);
            float f1 = SHRenderHelper.interpolate(afloat[0][i][1], afloat[1][i][1]);
            float f2 = SHRenderHelper.interpolate(afloat[0][i][2], afloat[1][i][2]);

            GL11.glPushMatrix();
            GL11.glTranslatef(f0 * spread, 0, f2 * spread);
            GL11.glColor4f(0.6F + f0, 0.6F + f1, 0.6F + f2, 1);
            model.render(0.0625F);
            GL11.glPopMatrix();
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
