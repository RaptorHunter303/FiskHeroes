package com.fiskmods.heroes.client.render.effect;

import java.util.List;
import java.util.function.Function;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectEnergyProj;
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
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public enum EffectEnergyProjection implements Effect
{
    INSTANCE;

    private final ResourceLocation texture = new ResourceLocation("textures/entity/beacon_beam.png");

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
                    HeroEffectEnergyProj effect = renderer.getEffect(HeroEffectEnergyProj.class, entity);

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

                float time = entity.ticksExisted + partialTicks;
                float t = -time * 0.2F - MathHelper.floor_float(-time * 0.1F);
                float p = -(float) Math.toRadians(pitch);

                if (isFirstPerson)
                {
                    yaw = SHRenderHelper.interpolate(entity.rotationYaw, entity.prevRotationYaw);
                }

                GL11.glPushMatrix();
                GL11.glTranslatef(0, (float) VectorHelper.getOffset(entity) - (isFirstPerson ? 0 : 0.21F * scale), 0);
                GL11.glRotated(-yaw, 0, 1, 0);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
                SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);

                mc.getTextureManager().bindTexture(texture);

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

                    dst.rotateAroundX(p);

                    GL11.glPushMatrix();
                    GL11.glTranslated(src.xCoord, src.yCoord, src.zCoord);
                    SHRenderHelper.faceVec(src, dst);
                    GL11.glRotatef(time * 10, 0, 0, 1);

                    double dist = src.distanceTo(dst);

                    for (int j = 0; j <= layers; ++j)
                    {
                        if (j < layers)
                        {
                            GL11.glColor4f((float) color.xCoord, (float) color.yCoord, (float) color.zCoord, 0.75F / layers * shooting);
                            GL11.glDepthMask(false);
                        }
                        else
                        {
                            GL11.glColor4f(1, 1, 1, shooting);
                            GL11.glDepthMask(true);
                        }

                        double length = dist + (j < layers ? j * (0.0625 / layers) : 0);
                        double size = j < layers ? j * (6.0 / layers) : 0;
                        double w = (beam.width + size) / 2 * scale;
                        double h = (beam.height + size) / 2 * scale;

                        float minV = t * 2;
                        float maxV = (float) (length / Math.min(w, h) / 2) + minV;
                        tessellator.startDrawingQuads();

                        if (j < layers)
                        {
                            float f = 1;
                            float s = 4;
                            float speed = 0.5F;
                            double a = MathHelper.ceiling_double_int(length) * 4;
                            Function<Integer, Double> func = k -> 1 + Math.max(Math.sin(time * speed - k / a * length * f) / s, 0);

                            for (int k = 0; k < a; ++k)
                            {
                                double l0 = length * k / a, l1 = length * (k + 1) / a;
                                double d0 = func.apply(k), d1 = func.apply(k + 1);
                                double w0 = w * d0, w1 = w * d1;
                                double h0 = w * d0, h1 = h * d1;

                                tessellator.addVertexWithUV(-w1, h1, l1, 0, maxV);
                                tessellator.addVertexWithUV(w1, h1, l1, 1, maxV);
                                tessellator.addVertexWithUV(w0, h0, l0, 1, minV);
                                tessellator.addVertexWithUV(-w0, h0, l0, 0, minV);
                                tessellator.addVertexWithUV(w0, -h0, l0, 1, minV);
                                tessellator.addVertexWithUV(w1, -h1, l1, 1, maxV);
                                tessellator.addVertexWithUV(-w1, -h1, l1, 0, maxV);
                                tessellator.addVertexWithUV(-w0, -h0, l0, 0, minV);
                                tessellator.addVertexWithUV(-w0, -h0, l0, 0, minV);
                                tessellator.addVertexWithUV(-w1, -h1, l1, 0, maxV);
                                tessellator.addVertexWithUV(-w1, h1, l1, 1, maxV);
                                tessellator.addVertexWithUV(-w0, h0, l0, 1, minV);
                                tessellator.addVertexWithUV(w1, h1, l1, 1, maxV);
                                tessellator.addVertexWithUV(w1, -h1, l1, 0, maxV);
                                tessellator.addVertexWithUV(w0, -h0, l0, 0, minV);
                                tessellator.addVertexWithUV(w0, h0, l0, 1, minV);

                                if (k == 0)
                                {
                                    tessellator.addVertexWithUV(-w0, h0, 0, 0, 0);
                                    tessellator.addVertexWithUV(w0, h0, 0, 0, 0);
                                    tessellator.addVertexWithUV(w0, -h0, 0, 0, 0);
                                    tessellator.addVertexWithUV(-w0, -h0, 0, 0, 0);
                                }
                                else if (k == a - 1)
                                {
                                    tessellator.addVertexWithUV(w1, -h1, length, 0, 0);
                                    tessellator.addVertexWithUV(w1, h1, length, 0, 0);
                                    tessellator.addVertexWithUV(-w1, h1, length, 0, 0);
                                    tessellator.addVertexWithUV(-w1, -h1, length, 0, 0);
                                }
                            }
                        }
                        else
                        {
                            tessellator.addVertexWithUV(w, -h, length, 0, 0);
                            tessellator.addVertexWithUV(w, h, length, 0, 0);
                            tessellator.addVertexWithUV(-w, h, length, 0, 0);
                            tessellator.addVertexWithUV(-w, -h, length, 0, 0);
                            tessellator.addVertexWithUV(-w, h, 0, 0, 0);
                            tessellator.addVertexWithUV(w, h, 0, 0, 0);
                            tessellator.addVertexWithUV(w, -h, 0, 0, 0);
                            tessellator.addVertexWithUV(-w, -h, 0, 0, 0);
                            tessellator.addVertexWithUV(-w, h, length, 0, maxV);
                            tessellator.addVertexWithUV(w, h, length, 1, maxV);
                            tessellator.addVertexWithUV(w, h, 0, 1, minV);
                            tessellator.addVertexWithUV(-w, h, 0, 0, minV);
                            tessellator.addVertexWithUV(w, -h, 0, 1, minV);
                            tessellator.addVertexWithUV(w, -h, length, 1, maxV);
                            tessellator.addVertexWithUV(-w, -h, length, 0, maxV);
                            tessellator.addVertexWithUV(-w, -h, 0, 0, minV);
                            tessellator.addVertexWithUV(-w, -h, 0, 0, minV);
                            tessellator.addVertexWithUV(-w, -h, length, 0, maxV);
                            tessellator.addVertexWithUV(-w, h, length, 1, maxV);
                            tessellator.addVertexWithUV(-w, h, 0, 1, minV);
                            tessellator.addVertexWithUV(w, h, length, 1, maxV);
                            tessellator.addVertexWithUV(w, -h, length, 0, maxV);
                            tessellator.addVertexWithUV(w, -h, 0, 0, minV);
                            tessellator.addVertexWithUV(w, h, 0, 1, minV);
                        }

                        tessellator.draw();
                    }

                    GL11.glPopMatrix();
                }

                SHRenderHelper.resetLighting();
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    public void onUpdate(Entry e, Entity anchor)
    {
        if (SHData.SHOOTING_TIMER.interpolate(anchor) == 0 || !Ability.ENERGY_PROJECTION.test(anchor))
        {
            e.markForDeletion();
        }
    }
}
