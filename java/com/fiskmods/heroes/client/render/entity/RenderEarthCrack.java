package com.fiskmods.heroes.client.render.entity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectSpellcasting;
import com.fiskmods.heroes.common.entity.EntityEarthCrack;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.FiskMath;
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

public class RenderEarthCrack extends Render
{
    public void doRender(EntityEarthCrack entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        EntityLivingBase target = entity.target;

        if (target != null)
        {
            int hex = 0xB366FF;

            if (entity.caster != null)
            {
                HeroIteration iter = SHHelper.getHeroIter(entity.caster);

                if (iter != null)
                {
                    HeroRenderer renderer = HeroRenderer.get(iter);
                    HeroEffectSpellcasting effect = renderer.getEffect(HeroEffectSpellcasting.class, entity.caster);

                    if (effect != null)
                    {
                        hex = effect.getEarthCrackColor(hex);
                    }
                }
            }

            Random rand = new Random(), randPrev = new Random();
            double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            double targetX = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTicks;
            double targetY = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTicks;
            double targetZ = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTicks;
            long seed = -2743867098925L + 0xFFABC * entity.getEntityId();
            int ao = Minecraft.getMinecraft().gameSettings.ambientOcclusion;
            int layers = 3 + ao * 4;
            int segments = 6;

            Vec3 src = VectorHelper.centerOf(entity);
            Vec3 dst = Vec3.createVectorHelper(targetX, targetY, targetZ);
            double d = src.distanceTo(dst);
            float intensity = (float) (1.5 / d) * FiskMath.curveCrests(Math.min((entity.ticksExisted + partialTicks) / SHConstants.TICKS_EARTHCRACK * 2.5F, 1));
            float alpha = FiskMath.animate(entity.ticksExisted + partialTicks, SHConstants.TICKS_EARTHCRACK, 0, 10, 3) / 2;
            float[] color = SHRenderHelper.hexToRGB(hex);

            GL11.glPushMatrix();
            GL11.glTranslated(x - entityX, y - entityY, z - entityZ);
            GL11.glScalef(-1, -1, -1);
            SHRenderHelper.setupRenderLightning();

            for (int i = 0; i < 3; ++i)
            {
                dst = dst.addVector(0, target.height / 4, 0);
                segments = 1 + Math.min(MathHelper.ceiling_double_int(d / 3), 8);
                renderLightning(rand, randPrev, entity.ticksExisted + i * 1000, layers, src, dst, segments, intensity, color, alpha, seed);
            }

            Tessellator tessellator = Tessellator.instance;
            d = src.distanceTo(dst) + target.height;

            GL11.glDepthMask(false);
            GL11.glTranslated(-src.xCoord, -src.yCoord, -src.zCoord);
            SHRenderHelper.faceVec(dst, src);

            for (int layer = 0; layer < layers; ++layer)
            {
                double size = (target.width * 2 + (layer < layers ? layer * (16.0 / layers) : 0)) / 16;
                float opacity = alpha / layers;
                float f = 0;

                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(color[0], color[1], color[2], f);
                tessellator.addVertex(size, size, d);
                tessellator.setColorRGBA_F(color[0], color[1], color[2], opacity);
                tessellator.addVertex(size, size, 0);
                tessellator.addVertex(-size, size, 0);
                tessellator.setColorRGBA_F(color[0], color[1], color[2], f);
                tessellator.addVertex(-size, size, d);
                tessellator.setColorRGBA_F(color[0], color[1], color[2], opacity);
                tessellator.addVertex(-size, -size, 0);
                tessellator.addVertex(size, -size, 0);
                tessellator.setColorRGBA_F(color[0], color[1], color[2], f);
                tessellator.addVertex(size, -size, d);
                tessellator.addVertex(-size, -size, d);
                tessellator.setColorRGBA_F(color[0], color[1], color[2], opacity);
                tessellator.addVertex(-size, size, 0);
                tessellator.addVertex(-size, -size, 0);
                tessellator.setColorRGBA_F(color[0], color[1], color[2], f);
                tessellator.addVertex(-size, -size, d);
                tessellator.addVertex(-size, size, d);
                tessellator.addVertex(size, -size, d);
                tessellator.setColorRGBA_F(color[0], color[1], color[2], opacity);
                tessellator.addVertex(size, -size, 0);
                tessellator.addVertex(size, size, 0);
                tessellator.setColorRGBA_F(color[0], color[1], color[2], f);
                tessellator.addVertex(size, size, d);
                tessellator.addVertex(size, -size, d);
                tessellator.addVertex(size, size, d);
                tessellator.addVertex(-size, size, d);
                tessellator.addVertex(-size, -size, d);
                tessellator.setColorRGBA_F(color[0], color[1], color[2], opacity);
                tessellator.addVertex(-size, size, 0);
                tessellator.addVertex(size, size, 0);
                tessellator.addVertex(size, -size, 0);
                tessellator.addVertex(-size, -size, 0);
                tessellator.draw();
            }

            SHRenderHelper.finishRenderLightning();
            GL11.glPopMatrix();
        }
    }

    private void renderLightning(Random rand, Random randPrev, int ticks, int layers, Vec3 src, Vec3 dst, int segments, float intensity, float[] color, float alpha, long seed)
    {
        Tessellator tessellator = Tessellator.instance;

        for (int layer = 0; layer <= layers; ++layer)
        {
            double size = (0.25 + (layer < layers ? layer * (4.0 / layers) : 0)) / 16;
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

            rand.setSeed(seed + ticks / 2 * 10);
            randPrev.setSeed(seed + (ticks - 1) / 2 * 10);
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
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        doRender((EntityEarthCrack) entity, x, y, z, entityYaw, partialTicks);
    }
}
