package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.model.ModelSpike;
import com.fiskmods.heroes.common.entity.EntityIcicle;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderIcicle extends Render
{
    private final ResourceLocation texture = new ResourceLocation("textures/blocks/ice_packed.png");
    private final ModelSpike spike = new ModelSpike(0.07F, 0.6F, 0.25F);

    public void renderIcicle(EntityIcicle entity, double x, double y, double z, float f, float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y + entity.height / 2, (float) z);
        GL11.glRotatef(SHRenderHelper.interpolate(entity.rotationYaw, entity.prevRotationYaw) - 90, 0, 1, 0);
        GL11.glRotatef(SHRenderHelper.interpolate(entity.rotationPitch, entity.prevRotationPitch) - 90, 0, 0, 1);
        GL11.glTranslatef(0, -0.2F, 0);
        GL11.glEnable(GL11.GL_BLEND);
        bindTexture(texture);
        spike.render(1);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        renderIcicle((EntityIcicle) entity, x, y, z, f, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return texture;
    }
}
