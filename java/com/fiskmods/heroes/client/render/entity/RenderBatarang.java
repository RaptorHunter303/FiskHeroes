package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelBatarangBvS;
import com.fiskmods.heroes.common.entity.gadget.EntityBatarang;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderBatarang extends Render
{
    private ModelBatarangBvS model = new ModelBatarangBvS();

    public void render(EntityBatarang entity, double x, double y, double z, float f, float partialTicks)
    {
        bindEntityTexture(entity);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90, 0, 1, 0);
        GL11.glTranslatef(0, 0.1F, 0);
        GL11.glRotatef(180, 1, 0, 0);

        float f11 = entity.arrowShake - partialTicks;

        if (f11 > 0.0F)
        {
            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }

        float scale = 0.25F;
        GL11.glScalef(scale, scale, scale);

        float f1 = entity.spinTime + (entity.spinTime - entity.prevSpinTime) * partialTicks;
        GL11.glRotatef(f1 * 64, 0, 0, 1);
        GL11.glTranslatef(0, 0.56F, 0);
        model.render();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return new ResourceLocation(FiskHeroes.MODID, "textures/models/batarang_bvs.png");
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        render((EntityBatarang) entity, x, y, z, f, partialTicks);
    }
}
