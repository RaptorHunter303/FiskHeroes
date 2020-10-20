package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelSmokePellet;
import com.fiskmods.heroes.common.entity.gadget.EntitySmokePellet;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSmokePellet extends Render
{
    private ModelSmokePellet model = new ModelSmokePellet();

    public void render(EntitySmokePellet entity, double x, double y, double z, float f, float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y + entity.height / 2, (float) z);

        float f1 = entity.ticksExisted + partialTicks;
        GL11.glRotatef(-90, 0, 1, 0);
        GL11.glRotatef(SHRenderHelper.interpolate(entity.rotationYaw, entity.prevRotationYaw), 0, 1, 0);
        GL11.glRotatef(SHRenderHelper.interpolate(entity.rotationPitch, entity.prevRotationPitch) - f1 * 20, 0, 0, 1);

        float scale = 0.3F;
        GL11.glScalef(scale, -scale, -scale);

        bindEntityTexture(entity);
        model.render();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return new ResourceLocation(FiskHeroes.MODID, "textures/models/smoke_pellet.png");
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        render((EntitySmokePellet) entity, x, y, z, f, partialTicks);
    }
}
