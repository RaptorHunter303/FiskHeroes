package com.fiskmods.heroes.client.render.effect;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectHeatVision;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectHeatVision.Beam;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public enum EffectHeatVision implements Effect
{
    INSTANCE;

    @Override
    public void doRender(Entry e, Entity anchor, boolean isClientPlayer, boolean isFirstPerson, float partialTicks)
    {
        if (anchor instanceof EntityLivingBase)
        {
            EntityLivingBase entity = (EntityLivingBase) anchor;
            float shooting = SHData.SHOOTING_TIMER.interpolate(entity);

            if (shooting > 0)
            {
                HeroIteration iter = SHHelper.getHeroIter(entity);
                List<Beam> beams = HeroEffectHeatVision.DEFAULT_BEAMS;
                int hex = 0xFF0000;

                if (iter != null)
                {
                    HeroRenderer renderer = HeroRenderer.get(iter);
                    HeroEffectHeatVision effect = renderer.getEffect(HeroEffectHeatVision.class, entity);

                    if (effect != null)
                    {
                        hex = effect.getColor();

                        if (effect.getBeams() != null)
                        {
                            beams = effect.getBeams();
                        }
                    }
                }

                Tessellator tessellator = Tessellator.instance;
                Vec3 color = SHRenderHelper.getColorFromHex(hex);
                int layers = 10 + mc.gameSettings.ambientOcclusion * 20;

                double yaw = SHRenderHelper.interpolate(entity.rotationYawHead, entity.prevRotationYawHead);
                double pitch = SHRenderHelper.interpolate(entity.rotationPitch, entity.prevRotationPitch);
                double range = SHData.HEAT_VISION_LENGTH.interpolate(entity);
                float scale = SHData.SCALE.interpolate(entity);

                if (isFirstPerson)
                {
                    yaw = SHRenderHelper.interpolate(entity.rotationYaw, entity.prevRotationYaw);
                }

                GL11.glPushMatrix();
                GL11.glTranslatef(0, (float) VectorHelper.getOffset(entity) - (isFirstPerson ? 0 : 0.21F * scale), 0);
                GL11.glRotated(-yaw, 0, 1, 0);
                GL11.glRotated(pitch, 1, 0, 0);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
                SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);

                if (isFirstPerson)
                {
                    scale = 1;
                }

                scale /= 16;

                for (Beam beam : beams)
                {
                    Vec3 src;
                    Vec3 dst;

                    if (isFirstPerson)
                    {
                        src = Vec3.createVectorHelper(beam.x * scale, beam.y * scale - 0.2F, -0.2F);
                        dst = Vec3.createVectorHelper(0, 0, range);
                    }
                    else
                    {
                        src = Vec3.createVectorHelper(beam.x * scale, beam.y * scale, 4 * scale);
                        dst = Vec3.createVectorHelper(0, 4 * scale * (1 - Math.abs(pitch) / 90), range);
                    }

                    GL11.glPushMatrix();
                    GL11.glTranslated(src.xCoord, src.yCoord, src.zCoord);
                    SHRenderHelper.faceVec(src, dst);

                    for (int j = 0; j <= layers; ++j)
                    {
                        if (j < layers)
                        {
                            GL11.glColor4f((float) color.xCoord, (float) color.yCoord, (float) color.zCoord, 1F / layers / 2 * shooting);
                            GL11.glDepthMask(false);
                        }
                        else
                        {
                            GL11.glColor4f(1, 1, 1, shooting);
                            GL11.glDepthMask(true);
                        }

                        double length = src.distanceTo(dst) + (j < layers ? j * (1.0 / layers) : 0) * 0.0625F;
                        double size = j < layers ? j * (2.5 / layers) : 0;
                        double width = (beam.width + size) * scale / 2;
                        double height = (beam.height + size) * scale / 2;

                        tessellator.startDrawingQuads();
                        tessellator.addVertex(-width, height, length);
                        tessellator.addVertex(width, height, length);
                        tessellator.addVertex(width, height, 0);
                        tessellator.addVertex(-width, height, 0);
                        tessellator.addVertex(width, -height, 0);
                        tessellator.addVertex(width, -height, length);
                        tessellator.addVertex(-width, -height, length);
                        tessellator.addVertex(-width, -height, 0);
                        tessellator.addVertex(-width, -height, 0);
                        tessellator.addVertex(-width, -height, length);
                        tessellator.addVertex(-width, height, length);
                        tessellator.addVertex(-width, height, 0);
                        tessellator.addVertex(width, height, length);
                        tessellator.addVertex(width, -height, length);
                        tessellator.addVertex(width, -height, 0);
                        tessellator.addVertex(width, height, 0);
                        tessellator.addVertex(width, -height, length);
                        tessellator.addVertex(width, height, length);
                        tessellator.addVertex(-width, height, length);
                        tessellator.addVertex(-width, -height, length);
                        tessellator.draw();
                    }

                    GL11.glPopMatrix();
                }

                SHRenderHelper.resetLighting();
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    public void onUpdate(Entry e, Entity anchor)
    {
        if (SHData.SHOOTING_TIMER.interpolate(anchor) == 0 || !Ability.HEAT_VISION.test(anchor))
        {
            e.markForDeletion();
        }
    }
}
