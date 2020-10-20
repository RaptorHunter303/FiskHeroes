package com.fiskmods.heroes.client.hud;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.util.FiskMath;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class HUDElementLightsOut extends HUDElement
{
    public HUDElementLightsOut(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public boolean isVisible(ElementType type)
    {
        return type == ElementType.HOTBAR && SHData.LIGHTSOUT.get(mc.thePlayer);
    }

    @Override
    public void preRender(ElementType type, ScreenInfo screen, int mouseX, int mouseY, float partialTicks)
    {
        float f = SHData.LIGHTSOUT_TIMER.interpolate(mc.thePlayer);
        float f1 = FiskMath.animate(f, 1, 0, 0.2F, 0.1F);

        if (f1 > 0)
        {
            float angle = 360 * f;
            float totalAngle = 90;
            float[] r = {15, 30};

            setupAlpha();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(0.2F, 0.2F, 0.3F, f1 * 0.7F);

            for (int i = 0; i < 2; ++i)
            {
                GL11.glBegin(GL11.GL_QUADS);

                for (float f2 = angle; f2 > 0; --f2)
                {
                    float rad = (float) Math.toRadians(totalAngle + f2);
                    float rad1 = (float) Math.toRadians(totalAngle + f2 + 1);
                    GL11.glVertex2f(x + MathHelper.cos(rad1) * r[0], y + MathHelper.sin(rad1) * r[0]);
                    GL11.glVertex2f(x + MathHelper.cos(rad1) * r[1], y + MathHelper.sin(rad1) * r[1]);
                    GL11.glVertex2f(x + MathHelper.cos(rad) * r[1], y + MathHelper.sin(rad) * r[1]);
                    GL11.glVertex2f(x + MathHelper.cos(rad) * r[0], y + MathHelper.sin(rad) * r[0]);
                }

                totalAngle += angle;
                angle = 360 - angle;
                GL11.glColor4f(0, 0, 0, f1 * 0.1F);
                GL11.glEnd();
            }

            GL11.glColor4f(1, 1, 1, 1);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            finishAlpha();
        }
    }
}
