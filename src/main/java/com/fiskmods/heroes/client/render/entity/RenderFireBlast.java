package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.common.entity.EntityFireBlast;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderFireBlast extends Render
{
    public void doRender(EntityFireBlast entity, double x, double y, double z, float f, float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        render(0.25F, entity.ticksExisted + partialTicks);
        GL11.glPopMatrix();
    }

    public static void render(float scale, float ticks)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

        for (int i = 0; i < 10; ++i)
        {
            float f = ticks * 2 + i * 10000;
            GL11.glPushMatrix();
            GL11.glRotatef(f, 1, 0, 0);
            GL11.glRotatef(f, 0, 1, 0);
            GL11.glRotatef(f, 0, 0, 1);

            SHRenderHooks.renderFire(scale, 1);
            GL11.glPopMatrix();
        }

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        SHRenderHelper.resetLighting();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        doRender((EntityFireBlast) entity, x, y, z, f, partialTicks);
    }
}
