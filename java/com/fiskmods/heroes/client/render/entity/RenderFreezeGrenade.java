package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelFreezeGrenade;
import com.fiskmods.heroes.common.entity.gadget.EntityFreezeGrenade;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderFreezeGrenade extends Render
{
    private ModelFreezeGrenade model = new ModelFreezeGrenade();

    public void render(EntityFreezeGrenade entity, double x, double y, double z, float f, float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y + entity.height / 2, (float) z);

        float f1 = entity.ticksExisted + partialTicks;
        GL11.glRotatef(-90, 0, 1, 0);
        GL11.glRotatef(SHRenderHelper.interpolate(entity.rotationYaw, entity.prevRotationYaw), 0, 1, 0);
        GL11.glRotatef(SHRenderHelper.interpolate(entity.rotationPitch, entity.prevRotationPitch) - f1 * 20, 0, 0, 1);
        GL11.glTranslatef(0, 0.025F, 0);

        float scale = 0.5F;
        GL11.glScalef(scale, -scale, -scale);

        bindEntityTexture(entity);
        model.render();
        bindTexture(new ResourceLocation(FiskHeroes.MODID, "textures/models/freeze_grenade_lights.png"));
        GL11.glDisable(GL11.GL_LIGHTING);
        SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
        model.render();
        SHRenderHelper.resetLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return new ResourceLocation(FiskHeroes.MODID, "textures/models/freeze_grenade.png");
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        render((EntityFreezeGrenade) entity, x, y, z, f, partialTicks);
    }
}
