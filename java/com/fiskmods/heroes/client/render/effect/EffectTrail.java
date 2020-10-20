package com.fiskmods.heroes.client.render.effect;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.client.json.trail.JsonTrail;
import com.fiskmods.heroes.client.json.trail.JsonTrailLightning;
import com.fiskmods.heroes.client.render.LightningData;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectTrail;
import com.fiskmods.heroes.common.entity.EntitySpeedBlur;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public enum EffectTrail implements Effect
{
    INSTANCE;

    @Override
    public void doRender(Entry e, Entity anchor, boolean isClientPlayer, boolean isFirstPerson, float partialTicks)
    {
        try
        {
            if (anchor instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) anchor;
                JsonTrail trail = SpeedsterHelper.getJsonTrail(player);

                if (trail != null)
                {
                    float scale = player.height / 1.8F;
                    float offset = (scale - 1) * 1.62F - player.height;
                    Vec3 pos = player.getPosition(partialTicks);
                    Vec3 playerPos = pos.addVector(0, offset + (1 - scale) * 1.62F * 2, 0);

                    GL11.glPushMatrix();
                    GL11.glScalef(-1, -1, -1);

                    if (trail.lightning != null)
                    {
                        LinkedList<EntitySpeedBlur> list = HeroEffectTrail.getTrail(player);
                        JsonTrailLightning lightning = trail.lightning.get();

                        if (list.size() > 0)
                        {
                            float space = player.height / lightning.getDensity();
                            float differ = lightning.getDiffer() * scale;
                            Vec3 color = trail.lightning.getVecColor(player);

                            SHRenderHelper.setupRenderLightning();

                            for (int i = 0; i < lightning.getDensity(); ++i)
                            {
                                Vec3 add = Vec3.createVectorHelper(0, i * space, 0);

                                if (player != mc.thePlayer || mc.gameSettings.thirdPersonView > 0)
                                {
                                    EntitySpeedBlur blur = list.getLast();
                                    Vec3 start = blur.getLightningPosVector(i).subtract(blur.getPosition(partialTicks)).addVector(0, offset + player.yOffset, 0);
                                    Vec3 end = blur.getLightningPosVector(i).subtract(playerPos);

                                    float alphaStart = 1 - (blur.progress + partialTicks) / blur.trail.fade;
                                    float alphaEnd = 1 - (blur.progress - 1 + partialTicks) / blur.trail.fade;
                                    SHRenderHelper.drawLightningLine(VectorHelper.add(start, add.addVector(0, list.getFirst().lightningFactor[i] * differ, 0)), VectorHelper.add(end, add.addVector(0, blur.lightningFactor[i] * differ, 0)), 5, 1, color, scale, alphaStart * lightning.getOpacity(), alphaEnd * lightning.getOpacity());
                                }

                                for (int j = 0; j < list.size() - 1; ++j)
                                {
                                    EntitySpeedBlur blur = list.get(j);
                                    EntitySpeedBlur blur1 = list.get(j + 1);
                                    Vec3 start = blur.getLightningPosVector(i).subtract(playerPos);
                                    Vec3 end = blur1.getLightningPosVector(i).subtract(playerPos);

                                    float alphaStart = 1 - (blur.progress + partialTicks) / blur.trail.fade;
                                    float alphaEnd = 1 - (blur1.progress + partialTicks) / blur1.trail.fade;
                                    SHRenderHelper.drawLightningLine(VectorHelper.add(start, add.addVector(0, blur.lightningFactor[i] * differ, 0)), VectorHelper.add(end, add.addVector(0, blur1.lightningFactor[i] * differ, 0)), 5, 1, color, scale, alphaStart * lightning.getOpacity(), alphaEnd * lightning.getOpacity());
                                }
                            }

                            SHRenderHelper.finishRenderLightning();
                        }
                    }

                    if (player != mc.thePlayer || mc.gameSettings.thirdPersonView > 0)
                    {
                        LinkedList<LightningData> list = HeroEffectTrail.getLightningData(player);

                        if (list.size() > 0)
                        {
                            float opacity = 1;

                            if (trail.flicker != null)
                            {
                                opacity = trail.flicker.get().getOpacity();
                            }

                            SHRenderHelper.setupRenderLightning();

                            for (LightningData element : list)
                            {
                                LightningData data = element;
                                float progress = 1 - (data.progress + partialTicks) / 4;

                                GL11.glPushMatrix();
                                GL11.glTranslated(-data.pos.xCoord, -data.pos.yCoord + (player == mc.thePlayer ? 1.62F : 0), -data.pos.zCoord);
                                SHRenderHelper.renderLightning(data.lightning, progress * opacity);
                                GL11.glPopMatrix();
                            }

                            SHRenderHelper.finishRenderLightning();
                        }
                    }

                    GL11.glPopMatrix();

                    if (trail.particles != null)
                    {
                        LinkedList<EntitySpeedBlur> list = HeroEffectTrail.getTrail(player);

                        if (list.size() > 0)
                        {
                            float space = player.height / trail.particles.getDensity();
                            float differ = trail.particles.getDiffer() * scale;
                            float size = trail.particles.getScale() * scale * 2;

                            Tessellator tessellator = Tessellator.instance;
                            mc.getTextureManager().bindTexture(trail.particles.getTexture());

                            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

                            for (int i = 0; i < trail.particles.getDensity(); ++i)
                            {
                                Vec3 add = Vec3.createVectorHelper(0, i * space, 0);

                                for (EntitySpeedBlur entity : list)
                                {
                                    Vec3 vec3 = VectorHelper.add(entity.getParticlePosVector(i).subtract(playerPos), add.addVector(0, entity.particleFactor[i] * differ, 0));
                                    float progress = 1 - (entity.progress + partialTicks) / Math.max(entity.trail.fade, trail.particles.getFade());

                                    GL11.glPushMatrix();
                                    GL11.glTranslated(-vec3.xCoord, -vec3.yCoord, -vec3.zCoord);
                                    GL11.glRotatef(180 - RenderManager.instance.playerViewY, 0, 1, 0);
                                    GL11.glRotatef(-RenderManager.instance.playerViewX, 1, 0, 0);
                                    GL11.glColor4f(1, 1, 1, progress * trail.particles.getOpacity());
                                    tessellator.startDrawingQuads();
                                    tessellator.setNormal(0, 1, 0);
                                    tessellator.addVertexWithUV(-size, -size, 0, 0, 1);
                                    tessellator.addVertexWithUV(size, -size, 0, 1, 1);
                                    tessellator.addVertexWithUV(size, size, 0, 1, 0);
                                    tessellator.addVertexWithUV(-size, size, 0, 0, 0);
                                    tessellator.draw();
                                    GL11.glPopMatrix();
                                }
                            }

                            GL11.glDisable(GL11.GL_BLEND);
                            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                        }
                    }
                }
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    @Override
    public void onUpdate(Entry e, Entity anchor)
    {
        EntityPlayer player;

        if (anchor instanceof EntityPlayer)
        {
            player = (EntityPlayer) anchor;
        }
        else
        {
            e.markForDeletion();
            return;
        }

        if (HeroEffectTrail.getTrail(player).isEmpty() && HeroEffectTrail.getLightningData(player).isEmpty())
        {
            e.markForDeletion();
        }
    }
}
