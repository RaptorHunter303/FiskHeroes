package com.fiskmods.heroes.client.model;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.asm.ASMHooks;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectArmAnimation;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectCape;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectEnergyProj;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.common.entity.EntityRenderItemPlayer;
import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.Heroes;
import com.fiskmods.heroes.common.item.IDualItem;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHClientUtils;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;

import mods.battlegear2.client.utils.BattlegearRenderHelper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ModelHelper
{
    public static void renderBipedPre(ModelBiped model, Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        GL11.glPushMatrix();

        if (FiskHeroes.isBattlegearLoaded)
        {
            BattlegearRenderHelper.moveOffHandArm(entity, model, f5);
        }

        if (entity instanceof EntityPlayer)
        {
            ClientEventHandler.setupPlayerRotation((EntityPlayer) entity);
        }

        setRotationAngles(model, f, f1, f2, f3, f4, f5, entity);
    }

    public static void renderBipedPost(ModelBiped model, Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        GL11.glPopMatrix();
    }

    public static void setRotationAngles(ModelBiped model, float f, float f1, float f2, float f3, float f4, float f5, Entity entity1)
    {
        if (entity1 instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity1;

            if (SHClientUtils.isInanimate(player))
            {
                model.bipedLeftArm.rotationPointY = 2;
            }

            if (player instanceof EntityDisplayMannequin || player instanceof EntityRenderItemPlayer)
            {
                for (ModelRenderer modelRenderer : (List<ModelRenderer>) model.boxList)
                {
                    modelRenderer.rotateAngleX = 0;
                    modelRenderer.rotateAngleY = 0;
                    modelRenderer.rotateAngleZ = 0;
                }

                return;
            }

            if (SpeedsterHelper.isOnTreadmill(player))
            {
                f = SHData.TREADMILL_LIMB_PROGRESS.interpolate(player);
                f1 = SHData.TREADMILL_LIMB_FACTOR.interpolate(player);

                float f6 = getLimbSwingSpeed(model, f, f1, f2, f3, f4, f5, entity1);
                float f7 = getLimbSwingDegree(model, f, f1, f2, f3, f4, f5, entity1);

                model.bipedRightLeg.rotateAngleX = MathHelper.cos(f6 * 0.6662F) * 1.4F * f7;
                model.bipedLeftLeg.rotateAngleX = MathHelper.cos(f6 * 0.6662F + (float) Math.PI) * 1.4F * f7;
                model.bipedRightArm.rotateAngleX = MathHelper.cos(f6 * 0.6662F + (float) Math.PI) * 2 * f7 * 0.5F;
                model.bipedLeftArm.rotateAngleX = MathHelper.cos(f6 * 0.6662F) * 2 * f7 * 0.5F;
                model.bipedRightLeg.rotateAngleY = 0;
                model.bipedLeftLeg.rotateAngleY = 0;
                model.bipedRightArm.rotateAngleZ = 0;
                model.bipedLeftArm.rotateAngleZ = 0;
            }
        }

        if (entity1 instanceof EntityLivingBase)
        {
            EntityLivingBase entity = (EntityLivingBase) entity1;
            HeroIteration iter = SHHelper.getHeroIter(entity);
            ItemStack heldItem = entity.getHeldItem();

            if (entity instanceof EntityPlayer)
            {
                if (heldItem != null && heldItem.getItem() == ModItems.chronosRifle)
                {
                    float f6 = 0.25F;
                    model.bipedRightArm.rotateAngleX *= f6;
                    model.bipedRightArm.rotateAngleY *= f6;
                    model.bipedRightArm.rotateAngleZ *= f6;
                    model.bipedLeftArm.rotateAngleX *= f6;
                    model.bipedLeftArm.rotateAngleY *= f6;
                    model.bipedLeftArm.rotateAngleZ *= f6;

                    model.bipedRightArm.rotationPointX -= 0.5F;
                    model.bipedRightArm.rotationPointZ += 1;
                    model.bipedRightArm.rotateAngleX -= 0.8F;
                    model.bipedRightArm.rotateAngleY -= 0.7F;
                    model.bipedRightArm.rotateAngleZ += 0.1F;
                    model.bipedLeftArm.rotateAngleX -= model.isSneak ? 0.9F : 0.7F;
                    model.bipedLeftArm.rotateAngleY += 0.4F;
                    model.bipedLeftArm.rotateAngleZ -= 0.2F;
                }
                else
                {
                    model.bipedLeftArm.rotationPointY = 2;
                }
            }

            byte b = SHData.FLIGHT_ANIMATION.get(entity);

            if (b != 0)
            {
                double d = Math.min(Math.sqrt((entity.prevPosX - entity.posX) * (entity.prevPosX - entity.posX) + (entity.prevPosZ - entity.posZ) * (entity.prevPosZ - entity.posZ)), 1);
                double d1 = 1 - d;

                model.bipedRightArm.rotateAngleX *= d1;
                model.bipedRightArm.rotateAngleY *= d1;
                model.bipedRightArm.rotateAngleZ *= d1;
                model.bipedLeftArm.rotateAngleX *= d1;
                model.bipedLeftArm.rotateAngleY *= d1;
                model.bipedLeftArm.rotateAngleZ *= d1;
                model.bipedRightLeg.rotateAngleX *= d1;
                model.bipedRightLeg.rotateAngleY *= d1;
                model.bipedRightLeg.rotateAngleZ *= d1;
                model.bipedLeftLeg.rotateAngleX *= d1;
                model.bipedLeftLeg.rotateAngleY *= d1;
                model.bipedLeftLeg.rotateAngleZ *= d1;

                d *= b;
                model.bipedHead.rotateAngleX -= 0.52359877559829887307710723054658 * 2 * d;
                model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
            }

            float t = SHData.AIMING_TIMER.interpolate(entity);

            if (t > 0)
            {
                float headY = MathHelper.wrapAngleTo180_float(f3) / (180F / (float) Math.PI);
                t = FiskMath.curve(t);

                if (heldItem != null && heldItem.getItem() == ModItems.chronosRifle)
                {
                    float headX = Math.min(MathHelper.wrapAngleTo180_float(f4) / (180F / (float) Math.PI), 0.6F);
                    float deg = Math.abs(headY) / ((float) Math.PI / 2);

                    model.bipedRightArm.rotationPointX += t * 1;
                    model.bipedRightArm.rotateAngleX += t * (0.1F + headX);
                    model.bipedRightArm.rotateAngleY += t * (0.4F + headY - headX * 0.25F);
                    model.bipedRightArm.rotationPointZ += t * (1.5F + Math.min(headY * 4, 0) * (1 - deg) - deg * 2);

                    model.bipedLeftArm.rotationPointX -= t * (1.5F + Math.max(headY, -0.5F));
                    model.bipedLeftArm.rotationPointZ -= t * (0.5F + Math.max(headY, -0.5F));
                    model.bipedLeftArm.rotationPointY = 2 + t;
                    model.bipedLeftArm.rotateAngleX -= t * (0.5F - headX * 1.3F + Math.min(headX + 1, 0));
                    model.bipedLeftArm.rotateAngleY += t * (0.4F + Math.min(headY, 0.5F) * 1.25F - headX * 0.25F + 0.4F * Math.min(headX, 0));
                    model.bipedLeftArm.rotateAngleZ += t * (0.2F + Math.max(headX, 0) + Math.min(headX, 0) * 0.7F);
                }
                else
                {
                    model.bipedRightArm.rotateAngleX = FiskServerUtils.interpolate(model.bipedRightArm.rotateAngleX, model.bipedHead.rotateAngleX - (float) Math.PI / 2, t);
                    model.bipedRightArm.rotateAngleY = FiskServerUtils.interpolate(model.bipedRightArm.rotateAngleY, headY, t);
                    model.bipedRightArm.rotateAngleZ = FiskServerUtils.interpolate(model.bipedRightArm.rotateAngleZ, model.bipedHead.rotateAngleZ, t);

                    // TODO: Dual aiming
//                    model.bipedLeftArm.rotateAngleX = FiskServerUtils.interpolate(model.bipedLeftArm.rotateAngleX, (model.bipedHead.rotateAngleX - (float) Math.PI / 2) * 1F, t);
//                    model.bipedLeftArm.rotateAngleY = FiskServerUtils.interpolate(model.bipedLeftArm.rotateAngleY, headY + 0.2F, t);
//                    model.bipedLeftArm.rotateAngleZ = FiskServerUtils.interpolate(model.bipedLeftArm.rotateAngleZ, model.bipedHead.rotateAngleZ, t);
                }
            }

            t = SHData.SHIELD_BLOCKING_TIMER.interpolate(entity);

            if (t > 0)
            {
                t = FiskMath.curve(t);
                float f6 = FiskServerUtils.interpolate(1, 0.2F, t);

                model.bipedRightArm.rotateAngleX *= f6;
                model.bipedRightArm.rotateAngleX -= 1.3 * t;
                model.bipedRightArm.rotateAngleY -= 1.1 * t;
                model.bipedRightArm.rotateAngleZ += 0.3 * t;
            }

            if (heldItem != null && entity instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) entity;

                if (player.isUsingItem() && heldItem.getItem() == ModItems.captainAmericasShield)
                {
                    model.bipedRightArm.rotateAngleX -= 0.2F;
                    model.bipedRightArm.rotateAngleY -= 0.6F;
                }

                if (heldItem.getItem() instanceof IDualItem)
                {
                    float swingProgress = ((IDualItem) heldItem.getItem()).getSwingProgress(player, ClientEventHandler.renderTick);

                    if (swingProgress > 0)
                    {
                        if (model.bipedBody.rotateAngleY != 0)
                        {
                            model.bipedLeftArm.rotateAngleY -= model.bipedBody.rotateAngleY;
                            model.bipedLeftArm.rotateAngleX -= model.bipedBody.rotateAngleY;
                        }

                        model.bipedBody.rotateAngleY = -MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2) * 0.2F;

                        model.bipedRightArm.rotationPointZ = MathHelper.sin(model.bipedBody.rotateAngleY) * 5;
                        model.bipedRightArm.rotationPointX = -MathHelper.cos(model.bipedBody.rotateAngleY) * 5;

                        model.bipedLeftArm.rotationPointZ = -MathHelper.sin(model.bipedBody.rotateAngleY) * 5;
                        model.bipedLeftArm.rotationPointX = MathHelper.cos(model.bipedBody.rotateAngleY) * 5;

                        model.bipedRightArm.rotateAngleY += model.bipedBody.rotateAngleY;
                        model.bipedRightArm.rotateAngleX += model.bipedBody.rotateAngleY;
                        float f6 = 1 - swingProgress;
                        f6 = 1 - f6 * f6 * f6;
                        double f8 = MathHelper.sin(f6 * (float) Math.PI) * 1.2D;
                        double f10 = MathHelper.sin(swingProgress * (float) Math.PI) * -(model.bipedHead.rotateAngleX - 0.7F) * 0.75F;
                        model.bipedLeftArm.rotateAngleX -= f8 + f10;
                        model.bipedLeftArm.rotateAngleY += model.bipedBody.rotateAngleY * 3;
                        model.bipedLeftArm.rotateAngleZ = MathHelper.sin(swingProgress * (float) Math.PI) * -0.4F;
                    }
                }
            }

            if (SHData.TICKS_GLIDING.get(entity) > 4 && entity instanceof EntityPlayer)
            {
                float f6 = SHData.TICKS_GLIDING.get(entity) + ClientEventHandler.renderTick;
                float f7 = MathHelper.clamp_float(f6 * f6 / 100, 0, 1);
                float f8 = 1 - f7;
                float f9 = -0.261799F;
                float f10 = 1;
                float f11 = entity.getSwingProgress(ClientEventHandler.renderTick);
                f11 = 1 - (f11 > 0.5F ? 1 - f11 : f11) * 2;

                Vec3 motion = SHRenderHelper.getMotion((EntityPlayer) entity);

                if (motion.yCoord < 0)
                {
                    motion = motion.normalize();
                    f10 = 1 - (float) Math.pow(-motion.yCoord, 1.5D);
                }

                f9 = f10 * -1.570796F + (1 - f10) * f9;
                model.bipedHead.rotateAngleX = model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX * f8 - f7 * 0.7853982F;

                if (model.aimedBow)
                {
                    model.bipedRightArm.rotateAngleX = -f7 * (0.7853982F + (float) Math.PI / 2);
                    model.bipedLeftArm.rotateAngleX = -f7 * (0.7853982F + (float) Math.PI / 2);
                }

                if (!model.aimedBow && iter != null)
                {
                    HeroRenderer renderer = HeroRenderer.get(iter);

                    if (renderer.hasEffect(HeroEffectCape.class))
                    {
                        if (model.heldItemRight == 0)
                        {
                            model.bipedRightArm.rotateAngleZ -= f7 * f9 * 0.8 * f11;
                            model.bipedRightArm.rotateAngleX -= f7 * 0.025 * f11;
                        }

                        if (model.heldItemLeft == 0)
                        {
                            model.bipedLeftArm.rotateAngleZ += f7 * f9 * 0.8;
                            model.bipedLeftArm.rotateAngleX -= f7 * 0.025;
                        }
                    }
                    else
                    {
                        if (model.heldItemRight == 0)
                        {
                            model.bipedRightArm.rotateAngleZ -= f7 * f9 * 0.8 * f11;
                            model.bipedRightArm.rotateAngleX += f7 * 0.2 * f11;
                        }

                        if (model.heldItemLeft == 0)
                        {
                            model.bipedLeftArm.rotateAngleZ += f7 * f9 * 0.8;
                            model.bipedLeftArm.rotateAngleX += f7 * 0.2;
                        }
                    }
                }
            }

            if (iter != null)
            {
                HeroRenderer renderer = HeroRenderer.get(iter);
                HeroEffectEnergyProj energyProj = renderer.getEffect(HeroEffectEnergyProj.class, entity);
                HeroEffectArmAnimation armAnim = renderer.getEffect(HeroEffectArmAnimation.class, entity);

                if (energyProj != null && energyProj.shouldUseHands())
                {
                    t = FiskMath.curve(SHData.SHOOTING_TIMER.interpolate(entity));

                    model.bipedRightArm.rotateAngleX = FiskServerUtils.interpolate(model.bipedRightArm.rotateAngleX, Math.min(f4 / (180F / (float) Math.PI) - 0.9F, -0.1F), t);
                    model.bipedRightArm.rotateAngleY = FiskServerUtils.interpolate(model.bipedRightArm.rotateAngleY, f3 / (180F / (float) Math.PI) - 0.4F, t);
                    model.bipedLeftArm.rotateAngleX = FiskServerUtils.interpolate(model.bipedLeftArm.rotateAngleX, Math.min(f4 / (180F / (float) Math.PI) - 1.3F, -0.3F), t);
                    model.bipedLeftArm.rotateAngleY = FiskServerUtils.interpolate(model.bipedLeftArm.rotateAngleY, f3 / (180F / (float) Math.PI) + 0.3F, t);
                }

                if (armAnim != null)
                {
                    ItemStack itemstack = entity.getEquipmentInSlot(2);
                    t = armAnim.swordPose(entity, itemstack);

                    if (t > 0)
                    {
                        t = FiskMath.curve(t) * (1 - f1);
                        model.bipedRightArm.rotateAngleX -= 0.7 * t;
                        model.bipedRightArm.rotateAngleY -= 0.2 * t;
                        model.bipedRightArm.rotateAngleZ -= 0.3 * t;
                    }
                }

                if (iter.hero == Heroes.senor_cactus)
                {
                    t = SHData.HAT_TIP.interpolate(entity);
                    t = MathHelper.sin((float) ((1 - t) * Math.PI));
                    t *= t;

                    model.bipedRightArm.rotateAngleX = FiskServerUtils.interpolate(model.bipedRightArm.rotateAngleX, -2.4F, t);
                    model.bipedRightArm.rotateAngleY *= 1 - t;
                    model.bipedRightArm.rotateAngleZ *= 1 - t;
                }
            }

            t = SHData.LIGHTSOUT_TIMER.interpolate(entity);

            if (t > 0)
            {
                t = FiskMath.curve(FiskMath.animate(t, 1, 0, 0.3F, 0.2F));

                model.bipedRightArm.rotateAngleX = FiskServerUtils.interpolate(model.bipedRightArm.rotateAngleX, -3.1F, t);
                model.bipedRightArm.rotateAngleZ = FiskServerUtils.interpolate(model.bipedRightArm.rotateAngleZ, -0.2F, t);
                model.bipedRightArm.rotateAngleY *= 1 - t;

                model.bipedHead.rotateAngleX = FiskServerUtils.interpolate(model.bipedHead.rotateAngleX, -1.2F, t);
                model.bipedHead.rotateAngleY = FiskServerUtils.interpolate(model.bipedHead.rotateAngleY, 0.5F, t);
            }
        }
    }

    public static void rotateCorpse(EntityPlayer player, float partialTicks)
    {
        float f = SHClientUtils.getGlidingProgress(player, partialTicks);

        if (f > 0)
        {
            Vec3 lookVec = player.getLook(partialTicks);
            Vec3 motion = SHRenderHelper.getMotion(player);

            double d0 = motion.xCoord * motion.xCoord + motion.zCoord * motion.zCoord;
            double d1 = lookVec.xCoord * lookVec.xCoord + lookVec.zCoord * lookVec.zCoord;
            GL11.glRotatef(f * (-90 - player.rotationPitch), 1, 0, 0);

            if (d0 > 0 && d1 > 0)
            {
                double d2 = (motion.xCoord * lookVec.xCoord + motion.zCoord * lookVec.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = motion.xCoord * lookVec.zCoord - motion.zCoord * lookVec.xCoord;
                GL11.glRotatef((float) (Math.signum(d3) * Math.acos(d2) * 180 / Math.PI), 0, 1, 0);
            }
        }
    }

    public static float getLimbSwingSpeed(ModelBiped model, float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            Hero hero = SHHelper.getHero(player);

            if (SpeedsterHelper.hasSuperSpeed(player) && (SpeedsterHelper.isOnTreadmill(player) || SHData.SPEEDING.get(player)))
            {
                float multiplier = Math.max(SpeedsterHelper.getPlayerTopSpeed(player) / 40, 1);
                f *= Math.min(multiplier, 5);
            }
        }

        return f;
    }

    public static float getLimbSwingDegree(ModelBiped model, float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;

            if (SHData.TICKS_GLIDING.get(player) > 4)
            {
                float f7 = SHData.TICKS_GLIDING.get(player) + ClientEventHandler.renderTick;
                float f8 = 1 - MathHelper.clamp_float(f7 * f7 / 100, 0, 1);
                f1 *= f8;
            }
        }

        return f1 * ASMHooks.getStrengthScale(entity);
    }
}
