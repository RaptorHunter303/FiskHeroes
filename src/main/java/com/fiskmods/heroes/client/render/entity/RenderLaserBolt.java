package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.EntityLaserBolt;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public class RenderLaserBolt extends Render
{
    public void renderBolt(EntityLaserBolt entity, double x, double y, double z, float f, float partialTicks)
    {
        Tessellator tessellator = Tessellator.instance;
        Vec3 src = Vec3.createVectorHelper(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ);
        Vec3 dst = Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
        Vec3 color = SHRenderHelper.getColorFromHex(entity.getColor());
        int smoothFactor = Minecraft.getMinecraft().gameSettings.ambientOcclusion;
        int layers = 10 + smoothFactor * 20;

        src = dst.subtract(src);
        dst = dst.subtract(dst);
        src = src.normalize();

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);

        GL11.glTranslatef((float) x, (float) y, (float) z);
        SHRenderHelper.faceVec(src, dst);

        for (int i = 0; i <= layers; ++i)
        {
            if (i < layers)
            {
                GL11.glColor4d(color.xCoord, color.yCoord, color.zCoord, 1F / layers / 2);
                GL11.glDepthMask(false);
            }
            else
            {
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glDepthMask(true);
            }

            double size = 0.325 + (i < layers ? i * (2.5 / layers) : 0);
            double d = (i < layers ? 1 - i * (1.0 / layers) : 0) * 0.1;
            double width = 1D / 16 * size;
            double height = 1D / 16 * size;
            double length = src.distanceTo(dst) + d;

            tessellator.startDrawingQuads();
            tessellator.addVertex(-width, height, length);
            tessellator.addVertex(width, height, length);
            tessellator.addVertex(width, height, -d);
            tessellator.addVertex(-width, height, -d);
            tessellator.addVertex(width, -height, -d);
            tessellator.addVertex(width, -height, length);
            tessellator.addVertex(-width, -height, length);
            tessellator.addVertex(-width, -height, -d);
            tessellator.addVertex(-width, -height, -d);
            tessellator.addVertex(-width, -height, length);
            tessellator.addVertex(-width, height, length);
            tessellator.addVertex(-width, height, -d);
            tessellator.addVertex(width, height, length);
            tessellator.addVertex(width, -height, length);
            tessellator.addVertex(width, -height, -d);
            tessellator.addVertex(width, height, -d);
            tessellator.addVertex(width, -height, length);
            tessellator.addVertex(width, height, length);
            tessellator.addVertex(-width, height, length);
            tessellator.addVertex(-width, -height, length);
            tessellator.addVertex(width, -height, -d);
            tessellator.addVertex(width, height, -d);
            tessellator.addVertex(-width, height, -d);
            tessellator.addVertex(-width, -height, -d);
            tessellator.draw();
        }

        SHRenderHelper.resetLighting();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        renderBolt((EntityLaserBolt) entity, x, y, z, f, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }
}
