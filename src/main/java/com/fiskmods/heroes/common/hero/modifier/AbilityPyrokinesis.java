package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.WeaknessMetalSkin.HeatSource;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class AbilityPyrokinesis extends Ability
{
    public AbilityPyrokinesis(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled && SHData.AIMING.get(entity))
        {
            double range = Rule.RANGE_FLAMEBLAST.get(entity, hero);
            MovingObjectPosition rayTrace = SHHelper.rayTrace(entity, range, 6, 1);

            if (rayTrace != null)
            {
                if (rayTrace.entityHit != null)
                {
                    double d0 = entity.posX - rayTrace.entityHit.posX;
                    double d1;

                    for (d1 = entity.posZ - rayTrace.entityHit.posZ; d0 * d0 + d1 * d1 < 1E-4; d1 = (Math.random() - Math.random()) * 0.01)
                    {
                        d0 = (Math.random() - Math.random()) * 0.01;
                    }

                    if (rayTrace.entityHit.attackEntityFrom(ModDamageSources.BURN.apply(entity), Rule.DMG_FLAMEBLAST.get(entity, hero)))
                    {
                        SHHelper.ignite(rayTrace.entityHit, SHConstants.IGNITE_FLAME_BLAST);

                        if (rayTrace.entityHit instanceof EntityLivingBase)
                        {
                            ((EntityLivingBase) rayTrace.entityHit).knockBack(rayTrace.entityHit, 0, d0 * 50, d1 * 50);
                        }
                    }

                    HeatSource.FLAME_BLAST.applyHeat(rayTrace.entityHit);
                }
                else if (!entity.worldObj.isRemote && rayTrace.typeOfHit == MovingObjectType.BLOCK && FiskServerUtils.canEntityEdit(entity, rayTrace, null))
                {
                    SHHelper.melt(entity.worldObj, rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ, Rule.GRIEF_FLAMEBLAST.get(entity.worldObj, rayTrace.blockX, rayTrace.blockZ) ? 7 : 3);
                }
            }

            if (entity.worldObj.isRemote)
            {
                double length = rayTrace != null && rayTrace.hitInfo instanceof Double ? (Double) rayTrace.hitInfo : range;
                float aiming = SHData.AIMING_TIMER.get(entity);
                float spread = 0.4F * aiming;
                float motion = 0.05F * aiming;

                Vec3 src = VectorHelper.getOffsetCoords(entity, -0.375, -0.2, 0.6);
                Vec3 dest = VectorHelper.getOffsetCoords(entity, 0, 0, length);
                length = src.distanceTo(dest);

                for (double point = 0; point <= length; point += 0.15)
                {
                    Vec3 vec3 = VectorHelper.interpolate(dest, src, point);
                    SHParticleType.SHORT_FLAME.spawn(vec3.xCoord + (Math.random() - 0.5) * spread, vec3.yCoord + (Math.random() - 0.5) * spread, vec3.zCoord + (Math.random() - 0.5) * spread, (Math.random() - 0.5) * motion, (Math.random() - 0.5) * motion, (Math.random() - 0.5) * motion);
                }
            }
        }
    }

    @Override
    public float damageDealt(EntityLivingBase entity, EntityLivingBase target, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        if (FiskServerUtils.isMeleeDamage(source))
        {
            target.setFire(SHConstants.IGNITE_FLAME_FISTS);
        }

        return amount;
    }
}
