package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectRepulsorBlast;
import com.fiskmods.heroes.common.entity.EntityRepulsorBlast;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderRepulsorBlast extends Render
{
    public void doRender(EntityRepulsorBlast entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        Vec3 dst = entity.targetVec;

        if (dst != null)
        {
            int hex = 0xFFE77C;

            if (entity.casterEntity != null)
            {
                HeroIteration iter = SHHelper.getHeroIter(entity.casterEntity);

                if (iter != null)
                {
                    HeroRenderer renderer = HeroRenderer.get(iter);
                    HeroEffectRepulsorBlast effect = renderer.getEffect(HeroEffectRepulsorBlast.class, entity.casterEntity);

                    if (effect != null)
                    {
                        hex = effect.getColor();
                    }
                }
            }

            Tessellator tessellator = Tessellator.instance;
            Vec3 src = VectorHelper.centerOf(entity);
            float alpha = (float) Math.sin(Math.PI * (entity.ticksExisted + partialTicks) / SHConstants.TICKS_REPULSOR);
            float[] color = SHRenderHelper.hexToRGB(hex);

            double d = src.distanceTo(dst);
            double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            int ao = Minecraft.getMinecraft().gameSettings.ambientOcclusion;
            int layers = 3 + ao * 4;
            int segments = 6;

            GL11.glPushMatrix();
            GL11.glTranslated(x - entityX, y - entityY, z - entityZ);
            GL11.glScalef(-1, -1, -1);
            GL11.glTranslated(-src.xCoord, -src.yCoord, -src.zCoord);
            SHRenderHelper.faceVec(dst, src);
            SHRenderHelper.setupRenderLightning();

            for (int layer = 0; layer <= layers; ++layer)
            {
                double size = (0.75 + (layer < layers ? layer * (1.5 / layers) : 0)) / 16;
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

                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacity);
                tessellator.addVertex(size, size, d);
                tessellator.addVertex(size, size, 0);
                tessellator.addVertex(-size, size, 0);
                tessellator.addVertex(-size, size, d);
                tessellator.addVertex(-size, -size, 0);
                tessellator.addVertex(size, -size, 0);
                tessellator.addVertex(size, -size, d);
                tessellator.addVertex(-size, -size, d);
                tessellator.addVertex(-size, size, 0);
                tessellator.addVertex(-size, -size, 0);
                tessellator.addVertex(-size, -size, d);
                tessellator.addVertex(-size, size, d);
                tessellator.addVertex(size, -size, d);
                tessellator.addVertex(size, -size, 0);
                tessellator.addVertex(size, size, 0);
                tessellator.addVertex(size, size, d);
                tessellator.addVertex(size, -size, d);
                tessellator.addVertex(size, size, d);
                tessellator.addVertex(-size, size, d);
                tessellator.addVertex(-size, -size, d);
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

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        doRender((EntityRepulsorBlast) entity, x, y, z, entityYaw, partialTicks);
    }
}
