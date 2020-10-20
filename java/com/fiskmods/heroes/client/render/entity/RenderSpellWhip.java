package com.fiskmods.heroes.client.render.entity;

import java.util.Random;
import java.util.function.Function;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectSpellcasting;
import com.fiskmods.heroes.common.entity.EntitySpellWhip;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderSpellWhip extends Render
{
    public void doRender(EntitySpellWhip entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        EntityLivingBase caster = entity.casterEntity;

        if (caster != null)
        {
            HeroIteration iter = SHHelper.getHeroIter(caster);
            int hex = 0xFF4800;

            if (iter != null)
            {
                HeroRenderer renderer = HeroRenderer.get(iter);
                HeroEffectSpellcasting effect = renderer.getEffect(HeroEffectSpellcasting.class, caster);

                if (effect != null)
                {
                    hex = effect.getWhipColor(hex);
                }
            }

            Tessellator tessellator = Tessellator.instance;
            double casterX = SHRenderHelper.interpolate(caster.posX, caster.prevPosX);
            double casterY = SHRenderHelper.interpolate(caster.posY, caster.prevPosY);
            double casterZ = SHRenderHelper.interpolate(caster.posZ, caster.prevPosZ);
            double entityX;
            double entityY;
            double entityZ;

            if (entity.hookedEntity != null)
            {
                Entity target = entity.hookedEntity;
                entityX = SHRenderHelper.interpolate(target.posX, target.prevPosX);
                entityY = SHRenderHelper.interpolate(target.posY, target.prevPosY) + target.height / 2 - target.yOffset; // FIXME: - Test in multiplayer for offset discrepancies
                entityZ = SHRenderHelper.interpolate(target.posZ, target.prevPosZ);
            }
            else
            {
                entityX = SHRenderHelper.interpolate(entity.posX, entity.prevPosX);
                entityY = SHRenderHelper.interpolate(entity.posY, entity.prevPosY);
                entityZ = SHRenderHelper.interpolate(entity.posZ, entity.prevPosZ);
            }

            if (renderManager.options.thirdPersonView > 0 || caster != Minecraft.getMinecraft().thePlayer)
            {
                float yaw = (float) Math.toRadians(SHRenderHelper.interpolate(caster.renderYawOffset, caster.prevRenderYawOffset));
                double offsetY = caster == Minecraft.getMinecraft().thePlayer ? 0 : caster.getEyeHeight();
                double sin = MathHelper.sin(yaw), cos = MathHelper.cos(yaw);
                double side = 0.35, front = 0.1;

                casterX -= cos * side + sin * front;
                casterZ -= sin * side - cos * front;
                casterY += offsetY - 1.08;
            }
            else
            {
                float swingProgress = caster.getSwingProgress(partialTicks);
                float f = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
                Vec3 src = Vec3.createVectorHelper(-0.3, -0.4, 0.4);
                src.rotateAroundX(-(float) Math.toRadians(caster.prevRotationPitch + (caster.rotationPitch - caster.prevRotationPitch) * partialTicks));
                src.rotateAroundY(-(float) Math.toRadians(caster.prevRotationYaw + (caster.rotationYaw - caster.prevRotationYaw) * partialTicks));
                src.rotateAroundY(f * 0.5F);
                src.rotateAroundX(-f * 0.7F);

                casterX += src.xCoord;
                casterY += src.yCoord;
                casterZ += src.zCoord;
            }

            float pull = 1 - SHRenderHelper.interpolate(entity.whipPull, entity.prevWhipPull);
            float stretch = SHRenderHelper.interpolate(entity.stretch, entity.prevStretch);

            if (pull < 1)
            {
                entityX = FiskServerUtils.interpolate(casterX, entityX, pull);
                entityY = FiskServerUtils.interpolate(casterY, entityY, pull);
                entityZ = FiskServerUtils.interpolate(casterZ, entityZ, pull);
            }

            x = entityX - RenderManager.renderPosX;
            y = entityY - RenderManager.renderPosY;
            z = entityZ - RenderManager.renderPosZ;
            double diffX = casterX - entityX;
            double diffY = casterY - entityY;
            double diffZ = casterZ - entityZ;
            float segments = 16;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
            SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);

            Random rand = new Random(), randPrev = new Random();
            Function<Double, Double> func = d -> (d + (SHRenderHelper.interpolate(rand.nextDouble(), randPrev.nextDouble()) - 0.5) * 0.2 * pull);

            for (int i = 0; i < 3; ++i)
            {
                tessellator.startDrawing(GL11.GL_LINE_STRIP);
                tessellator.setColorOpaque_I(hex);

                rand.setSeed(0xFAB68F * i + entity.ticksExisted * 20);
                randPrev.setSeed(0xFAB68F * i + (entity.ticksExisted - 1) * 20);
                for (int j = 0; j <= segments; ++j)
                {
                    float f1 = j / segments;
                    double y1 = FiskServerUtils.interpolate((f1 * f1 * f1 + f1) * 0.5, f1, stretch);

                    if (i == 0 || j == segments)
                    {
                        tessellator.addVertex(x + diffX * f1, y + diffY * y1 + 0.25, z + diffZ * f1);
                    }
                    else
                    {
                        tessellator.addVertex(x + func.apply(diffX) * f1, y + func.apply(diffY) * y1 + 0.25, z + func.apply(diffZ) * f1);
                    }
                }

                tessellator.draw();
            }

            SHRenderHelper.resetLighting();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
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
        doRender((EntitySpellWhip) entity, x, y, z, entityYaw, partialTicks);
    }
}
