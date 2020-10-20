package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.render.item.RenderItemCapsShield;
import com.fiskmods.heroes.common.entity.EntityThrownShield;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderThrownShield extends Render
{
    public void renderShield(EntityThrownShield entity, double x, double y, double z, float f, float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90, 0, 1, 0);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0, 0, 1);
        GL11.glRotatef(180, 1, 0, 0);

        GL11.glRotatef(SHRenderHelper.interpolate(entity.spinTime, entity.prevSpinTime) * 32, 0, 1, 0);
        RenderItemCapsShield.render(entity.getShieldItem(), false);
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
        renderShield((EntityThrownShield) entity, x, y, z, f, partialTicks);
    }
}
