package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.render.effect.EffectHeatVision;
import com.fiskmods.heroes.client.render.effect.EffectRenderHandler;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.WeaknessMetalSkin.HeatSource;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.collect.Iterables;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class AbilityHeatVision extends Ability
{
    public static final String KEY_HEAT_VISION = "HEAT_VISION";

    public AbilityHeatVision(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled)
        {
            if (entity.worldObj.isRemote && SHData.SHOOTING_TIMER.get(entity) > 0)
            {
                if (!Iterables.any(EffectRenderHandler.get(entity), e -> e.effect == EffectHeatVision.INSTANCE))
                {
                    EffectRenderHandler.add(entity, EffectHeatVision.INSTANCE, -1);
                }
            }

            if (SHData.SHOOTING.get(entity))
            {
                double range = Rule.RANGE_HEATVISION.get(entity, hero);
                MovingObjectPosition rayTrace = SHHelper.rayTrace(entity, range, 6, 1);

                if (rayTrace != null)
                {
                    if (rayTrace.entityHit != null)
                    {
                        rayTrace.entityHit.attackEntityFrom(ModDamageSources.BURN.apply(entity), Rule.DMG_HEATVISION.get(entity, hero));
                        HeatSource.HEAT_VISION.applyHeat(rayTrace.entityHit);
                    }
                    else if (!entity.worldObj.isRemote && rayTrace.typeOfHit == MovingObjectType.BLOCK && FiskServerUtils.canEntityEdit(entity, rayTrace, null))
                    {
                        SHHelper.melt(entity.worldObj, rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ, Rule.GRIEF_HEATVISION.get(entity.worldObj, rayTrace.blockX, rayTrace.blockZ) ? 15 : 3);
                    }
                }

                SHData.HEAT_VISION_LENGTH.setWithoutNotify(entity, rayTrace != null && rayTrace.hitInfo instanceof Double ? (Double) rayTrace.hitInfo : range);
            }

            if (entity.worldObj.isRemote && FiskHeroes.proxy.isClientPlayer(entity))
            {
                SHData.SHOOTING.set(entity, hero.isKeyPressed(entity, KEY_HEAT_VISION));
            }
        }
    }
}
