package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.EntityCanaryCry;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public class RenderSoundWave extends Render
{
    public void render(EntityCanaryCry entity, double x, double y, double z, float f, float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y + entity.height / 2, (float) z);
        GL11.glRotatef(SHRenderHelper.interpolate(entity.rotationYaw, entity.prevRotationYaw), 0, 1, 0);
        GL11.glRotatef(-SHRenderHelper.interpolate(entity.rotationPitch, entity.prevRotationPitch), 1, 0, 0);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1, 1, 1, entity.getOpacity(partialTicks));

        float angleIncr = 10;
        float radius = entity.getRadius(partialTicks);
        float radius1 = radius / 1.25F;
        float length = Math.max(radius / 5, 0.1F);
        Vec3 prevVec = Vec3.createVectorHelper(0, radius, 0);
        Vec3 prevVec1 = Vec3.createVectorHelper(0, radius1, 0);

        for (int i = 0; i <= 360F / angleIncr; ++i)
        {
            Vec3 vec3 = Vec3.createVectorHelper(0, radius, 0);
            Vec3 vec31 = Vec3.createVectorHelper(0, radius1, 0);
            float pitch = 0;
            float yaw = 0;
            float roll = i * angleIncr;
            vec3.rotateAroundX(-pitch * (float) Math.PI / 180.0F);
            vec3.rotateAroundY(-yaw * (float) Math.PI / 180.0F);
            vec3.rotateAroundZ(-roll * (float) Math.PI / 180.0F);
            vec31.rotateAroundX(-pitch * (float) Math.PI / 180.0F);
            vec31.rotateAroundY(-yaw * (float) Math.PI / 180.0F);
            vec31.rotateAroundZ(-roll * (float) Math.PI / 180.0F);

            tessellator.addVertex(vec3.xCoord, vec3.yCoord, 0);
            tessellator.addVertex(vec3.xCoord, vec3.yCoord, length);
            tessellator.addVertex(prevVec.xCoord, prevVec.yCoord, length);
            tessellator.addVertex(prevVec.xCoord, prevVec.yCoord, 0);

            tessellator.addVertex(vec31.xCoord, vec31.yCoord, 0);
            tessellator.addVertex(vec3.xCoord, vec3.yCoord, 0);
            tessellator.addVertex(prevVec.xCoord, prevVec.yCoord, 0);
            tessellator.addVertex(prevVec1.xCoord, prevVec1.yCoord, 0);

            prevVec = VectorHelper.copy(vec3);
            prevVec1 = VectorHelper.copy(vec31);
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        GL11.glDepthMask(false);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
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
        render((EntityCanaryCry) entity, x, y, z, f, partialTicks);
    }
}
