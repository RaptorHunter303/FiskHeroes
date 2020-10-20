package com.fiskmods.heroes.client.render.entity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectLightningAttack;
import com.fiskmods.heroes.common.entity.EntityLightningCast;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderLightningCast extends Render
{
    public void doRender(EntityLightningCast entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        Entity anchor = entity.anchorEntity;

        if (anchor != null)
        {
            int hex = 0xD9FFFF;

            if (entity.casterEntity != null)
            {
                HeroIteration iter = SHHelper.getHeroIter(entity.casterEntity);

                if (iter != null)
                {
                    HeroRenderer renderer = HeroRenderer.get(iter);
                    HeroEffectLightningAttack effect = renderer.getEffect(HeroEffectLightningAttack.class, entity.casterEntity);

                    if (effect != null)
                    {
                        hex = effect.getColor();
                    }
                }
            }

            Tessellator tessellator = Tessellator.instance;
            Random rand = new Random(), randPrev = new Random();

            long seed = -2743867098925L + 0xFFABC * entity.getEntityId();
            int ao = Minecraft.getMinecraft().gameSettings.ambientOcclusion;
            int layers = 3 + ao * 4;
            int segments = 3;

            double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            float alpha = (float) Math.sin(Math.PI * (entity.ticksExisted + partialTicks) / SHConstants.TICKS_LIGHTNING_CAST);
            float[] color = SHRenderHelper.hexToRGB(hex);

            Vec3 src;
            Vec3 dst = Vec3.createVectorHelper(entityX, entityY, entityZ);

            if (anchor instanceof EntityLivingBase)
            {
                src = VectorHelper.getOffsetCoords((EntityLivingBase) anchor, -0.4, -0.6, 0.4, partialTicks);
            }
            else
            {
                double anchorX = anchor.lastTickPosX + (anchor.posX - anchor.lastTickPosX) * partialTicks;
                double anchorY = anchor.lastTickPosY + (anchor.posY - anchor.lastTickPosY) * partialTicks;
                double anchorZ = anchor.lastTickPosZ + (anchor.posZ - anchor.lastTickPosZ) * partialTicks;
                src = Vec3.createVectorHelper(anchorX, anchorY, anchorZ);
            }

            float intensity = (float) (1.5 / src.distanceTo(dst));

            GL11.glPushMatrix();
            GL11.glTranslated(x - entityX, y - entityY, z - entityZ);
            GL11.glScalef(-1, -1, -1);
            SHRenderHelper.setupRenderLightning();

            for (int layer = 0; layer <= layers; ++layer)
            {
                double size = (0.25 + (layer < layers ? layer * (1.25 / layers) : 0)) / 16;
                float[] afloat;
                float opacity;

                if (layer < layers)
                {
                    GL11.glDepthMask(false);
                    afloat = color;
                    opacity = alpha * 0.5F / layers;
                }
                else
                {
                    GL11.glDepthMask(true);
                    afloat = new float[] {1, 1, 1};
                    opacity = alpha;
                }

                rand.setSeed(seed + anchor.ticksExisted / 2 * 10);
                randPrev.setSeed(seed + (anchor.ticksExisted - 1) / 2 * 10);
                Vec3 src1 = src;
                Vec3 dst1 = dst;

                GL11.glPushMatrix();
                GL11.glTranslated(-src1.xCoord, -src1.yCoord, -src1.zCoord);
                SHRenderHelper.faceVec(dst1, src1);
                GL11.glRotatef(90, 1, 0, 0);

                double length = src1.distanceTo(dst1);
                src1 = Vec3.createVectorHelper(0, 0, 0);

                for (int i = 0; i < segments; ++i)
                {
                    float f = (float) i / segments;
                    dst1 = Vec3.createVectorHelper(0, (i + 1) * length / segments, 0);

                    if (i < segments - 1)
                    {
                        float angle = (float) Math.toRadians(90 * intensity) * (1 - f);
                        dst1.rotateAroundX((SHRenderHelper.interpolate(rand.nextFloat(), randPrev.nextFloat()) - 0.5F) * 2 * angle);
                        dst1.rotateAroundY((SHRenderHelper.interpolate(rand.nextFloat(), randPrev.nextFloat()) - 0.5F) * 2 * angle);
                        dst1.rotateAroundZ((SHRenderHelper.interpolate(rand.nextFloat(), randPrev.nextFloat()) - 0.5F) * 2 * angle);
                    }
                    else
                    {
                        dst1 = Vec3.createVectorHelper(0, length, 0);
                    }

                    dst1.yCoord = MathHelper.clamp_double(dst1.yCoord, 0, length);
                    double segmentLength = src1.distanceTo(dst1);

                    tessellator.startDrawingQuads();
                    tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacity);
                    tessellator.addVertex(size, size, segmentLength);
                    tessellator.addVertex(size, size, 0);
                    tessellator.addVertex(-size, size, 0);
                    tessellator.addVertex(-size, size, segmentLength);
                    tessellator.addVertex(-size, -size, 0);
                    tessellator.addVertex(size, -size, 0);
                    tessellator.addVertex(size, -size, segmentLength);
                    tessellator.addVertex(-size, -size, segmentLength);
                    tessellator.addVertex(-size, size, 0);
                    tessellator.addVertex(-size, -size, 0);
                    tessellator.addVertex(-size, -size, segmentLength);
                    tessellator.addVertex(-size, size, segmentLength);
                    tessellator.addVertex(size, -size, segmentLength);
                    tessellator.addVertex(size, -size, 0);
                    tessellator.addVertex(size, size, 0);
                    tessellator.addVertex(size, size, segmentLength);
                    tessellator.addVertex(size, -size, segmentLength);
                    tessellator.addVertex(size, size, segmentLength);
                    tessellator.addVertex(-size, size, segmentLength);
                    tessellator.addVertex(-size, -size, segmentLength);
                    tessellator.addVertex(-size, size, 0);
                    tessellator.addVertex(size, size, 0);
                    tessellator.addVertex(size, -size, 0);
                    tessellator.addVertex(-size, -size, 0);

                    GL11.glPushMatrix();
                    GL11.glTranslated(src1.xCoord, src1.yCoord, src1.zCoord);
                    SHRenderHelper.faceVec(src1, dst1);
                    tessellator.draw();
                    GL11.glPopMatrix();
                    src1 = dst1;
                }

                GL11.glPopMatrix();
            }

            SHRenderHelper.finishRenderLightning();
            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        doRender((EntityLightningCast) entity, x, y, z, entityYaw, partialTicks);
    }
}
