package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectPropelledFlight;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.world.ModDimensions;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class AbilityFlight extends Ability
{
    public AbilityFlight(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (enabled)
        {
            if (phase == Phase.END)
            {
                if (entity.worldObj.isRemote)
                {
                    onClientUpdate(entity, hero);
                }

                entity.fallDistance = 0;
            }
            else if (this == Ability.PROPELLED_FLIGHT && applyMotion(entity) && entity.worldObj.isRemote)
            {
                boolean clientPlayer = FiskHeroes.proxy.isClientPlayer(entity);
                boolean firstPerson = clientPlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
                float scale = SHData.SCALE.get(entity);

                if (!clientPlayer || !firstPerson || scale >= 1)
                {
                    HeroIteration iter = SHHelper.getHeroIter(entity);

                    if (iter != null)
                    {
                        HeroRenderer renderer = HeroRenderer.get(iter);
                        HeroEffectPropelledFlight effect = renderer.getEffect(HeroEffectPropelledFlight.class, entity);

                        if (effect != null)
                        {
                            effect.doParticles(entity, iter, renderer, rand, scale, clientPlayer, firstPerson);
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void onClientUpdate(EntityLivingBase entity, Hero hero)
    {
        if (FiskHeroes.proxy.isClientPlayer(entity))
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (this == Ability.PROPELLED_FLIGHT)
            {
                SHData.JETPACKING.set(entity, mc.gameSettings.keyBindJump.getIsKeyPressed() && !mc.thePlayer.capabilities.isFlying);
            }
            else if (!mc.thePlayer.capabilities.isFlying && !mc.thePlayer.onGround)
            {
                boolean hovering = SHData.HOVERING.get(entity);

                if (!mc.thePlayer.isInWater() && mc.thePlayer.dimension != ModDimensions.QUANTUM_REALM_ID)
                {
                    if (hovering)
                    {
                        float scale = SHData.SCALE.get(entity);
                        float f = Math.max(scale, 1);

                        if (entity.motionY < -0.125F * f)
                        {
                            entity.motionY += 0.125F * f;
                        }
                        else if (entity.motionY > 0.05F * f)
                        {
                            entity.motionY -= 0.05F * f;
                        }
                        else
                        {
                            if (scale < 1)
                            {
                                scale = (1 - scale) / 2;
                            }

                            entity.motionY = MathHelper.sin(entity.ticksExisted / 15F) * 0.025F * scale;
                        }
                    }
                    else if (mc.thePlayer.motionY < 0)
                    {
                        mc.thePlayer.motionY += 0.07;
                    }

//                    if (mc.thePlayer.motionY < 0)
//                    {
//                        mc.thePlayer.motionY += 0.07;
//                    }
                }

                float f = hovering ? 0.2F : 1;

                if (SpeedsterHelper.canPlayerRun(mc.thePlayer))
                {
                    f *= (SHData.SPEED.get(mc.thePlayer) + 1) * 1.1F;
                }

                if (mc.thePlayer.isSprinting())
                {
                    f *= 1.2F;
                }

                mc.thePlayer.moveFlying(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, 0.075F * f);
                f = 1 + (f - 1) / 3;

                if (mc.gameSettings.keyBindJump.getIsKeyPressed())
                {
                    mc.thePlayer.motionY += (mc.thePlayer.dimension == ModDimensions.QUANTUM_REALM_ID ? 0.1F : hovering ? 0.2F : 0.125F) * f;
                }

                if (mc.gameSettings.keyBindSneak.getIsKeyPressed())
                {
                    mc.thePlayer.motionY -= (mc.thePlayer.dimension == ModDimensions.QUANTUM_REALM_ID ? 0.1F : hovering ? 0.125F : 0.075F) * f;
                }
            }
        }
    }

    public boolean applyMotion(EntityLivingBase entity)
    {
        boolean flying = SHData.JETPACKING.get(entity);
        boolean hovering = SHData.HOVERING.get(entity);

        if (flying && !hovering)
        {
            if (entity.motionY < 0)
            {
                entity.motionY += 0.18F;
            }
            else
            {
                entity.motionY += 0.12F;
            }

            entity.moveFlying(entity.moveStrafing, entity.moveForward, 0.15F);
            return true;
        }

        if (hovering)
        {
            float scale = SHData.SCALE.get(entity);
            float f = Math.max(scale, 1);

            if (entity.motionY < -0.15F * f)
            {
                entity.motionY += 0.15F * f;
            }
            else
            {
                if (scale < 1)
                {
                    scale = (1 - scale) / 2;
                }

                entity.motionY = MathHelper.sin(entity.ticksExisted / 10F) * 0.025F * scale;
            }

            return true;
        }

        return false;
    }
}
