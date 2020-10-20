package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.EntitySpellDuplicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSpellDuplicate extends Render
{
    private void render(EntitySpellDuplicate entity, double x, double y, double z, float f, float partialTicks)
    {
        EntityLivingBase owner = entity.getOwner();

        if (owner != null)
        {
            if (entity.deathTime > 0)
            {
                GL11.glColorMask(false, entity.deathTime >= 2, true, true);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef((float) x, (float) y + owner.yOffset, (float) z);
            GL11.glRotatef(entity.getMirroredRotation(partialTicks), 0, 1, 0);
            RenderManager.instance.renderEntityWithPosYaw(owner, 0, 0, 0, f, partialTicks);
            GL11.glColorMask(true, true, true, true);
            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        render((EntitySpellDuplicate) entity, x, y, z, f, partialTicks);
    }
}
