package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.client.render.arrow.ArrowRenderer;
import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderTrickArrow extends Render
{
    public void renderArrow(EntityTrickArrow arrow, double x, double y, double z, float f, float partialTicks)
    {
        ArrowRenderer arrowRenderer = ArrowRenderer.get(ArrowType.getArrowById(arrow.getArrowId()));
        boolean inEntity = partialTicks >= 85;

        if (inEntity)
        {
            partialTicks -= 85;
        }
        else
        {
            arrowRenderer.preRender(arrow, x, y, z, partialTicks, false);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(arrow.prevRotationYaw + (arrow.rotationYaw - arrow.prevRotationYaw) * partialTicks - 90, 0, 1, 0);
        GL11.glRotatef(arrow.prevRotationPitch + (arrow.rotationPitch - arrow.prevRotationPitch) * partialTicks, 0, 0, 1);
        GL11.glRotatef(90, 0, 0, 1);
        GL11.glRotatef(180, 0, 1, 0);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        float f11 = arrow.arrowShake - partialTicks;

        if (f11 > 0)
        {
            float f12 = -MathHelper.sin(f11 * 3) * f11;
            GL11.glRotatef(f12, 0, 0, 1);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        if (inEntity)
        {
            GL11.glTranslatef(0, 0.1F, 0);
        }
        else
        {
            GL11.glTranslatef(0, 0.5F, 0);
        }

        if (arrowRenderer != null)
        {
            GL11.glRotatef(-SHRenderHelper.interpolate(arrow.spinTime, arrow.prevSpinTime) * 32, 0, 1, 0);
            arrowRenderer.render(arrow, x, y, z, partialTicks, false);
        }

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        renderArrow((EntityTrickArrow) entity, x, y, z, f, partialTicks);
    }
}
