package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.entity.EntityShadowDome;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;

public class AbilityUmbrakinesis extends Ability
{
    public static final String KEY_SHADOWFORM = "SHADOWFORM";
    public static final String KEY_SHADOWDOME = "SHADOWDOME";

    public AbilityUmbrakinesis(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled)
        {
            boolean flag = SHData.SHADOWFORM.get(entity);

            if (flag)
            {
                entity.extinguish();

                if (entity.worldObj.isRemote && !FiskHeroes.proxy.isClientPlayer(entity))
                {
                    entity.setInvisible(!SHHelper.canPlayerSeeMartianInvis(FiskHeroes.proxy.getPlayer()));
                }
                else
                {
                    entity.setInvisible(true);
                }
            }

            boolean flag1 = SHData.LIGHTSOUT.get(entity);

            if (entity.worldObj.isRemote)
            {
                if (FiskHeroes.proxy.isClientPlayer(entity))
                {
                    SHData.LIGHTSOUT.set(entity, !flag && hero.isKeyPressed(entity, KEY_SHADOWDOME));
                }

                float f;

                if (flag1 && (f = SHData.LIGHTSOUT_TIMER.get(entity)) > -1)
                {
                    float t = FiskMath.animate(f, 1, 0, 0.3F, 0.2F);

                    if (t >= 1)
                    {
                        Vec3 src = VectorHelper.getSideCoordsRenderYawOffset(entity, 0.5F, false).addVector(0, 0.25F, 0);
                        Vec3 dest = src.addVector(0, 16, 0);
                        double length = src.distanceTo(dest);
                        float spread = 0.0F * t;

                        for (double point = 0; point <= length; point += 0.15F)
                        {
                            Vec3 vec3 = VectorHelper.interpolate(dest, src, point);
                            SHParticleType.SHADOW_SMOKE.spawn(vec3.xCoord + (Math.random() - 0.5) * spread, vec3.yCoord + (Math.random() - 0.5) * spread, vec3.zCoord + (Math.random() - 0.5) * spread, 0, 0.1F, 0);
                        }
                    }
                }
            }

            SHHelper.incr(SHData.LIGHTSOUT_TIMER, entity, Rule.TICKS_SHADOWDOMECHARGE.get(entity, hero), flag1);
            SHHelper.incr(SHData.SHADOWFORM_TIMER, entity, SHConstants.FADE_SHADOWFORM, flag);

            if (!flag1 || SHData.LIGHTSOUT_TIMER.get(entity) >= 1)
            {
                SHData.LIGHTSOUT_TIMER.setWithoutNotify(entity, 0F);
                SHData.LIGHTSOUT_TIMER.getPrevData().setWithoutNotify(entity, 0F);
                SHData.LIGHTSOUT.setWithoutNotify(entity, false);

                if (flag1 && !entity.worldObj.isRemote)
                {
                    entity.worldObj.spawnEntityInWorld(new EntityShadowDome(entity.worldObj, entity));
                }
            }
        }
    }

    @Override
    public boolean canTakeDamage(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount)
    {
        if (SHData.SHADOWFORM.get(entity) && isActive(entity))
        {
            return source != DamageSource.inWall && !FiskServerUtils.isMeleeDamage(source) && !FiskServerUtils.isProjectileDamage(source) && !source.isFireDamage() && !source.canHarmInCreative();
        }

        return true;
    }

    @Override
    public float damageTaken(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        return source.isExplosion() && SHData.SHADOWFORM.get(entity) ? amount * 2 : amount;
    }

    @Override
    public float damageDealt(EntityLivingBase entity, EntityLivingBase target, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        return SHData.SHADOWFORM.get(entity) && FiskServerUtils.isMeleeDamage(source) ? 0 : amount;
    }

    @Override
    public boolean isActive(EntityLivingBase entity)
    {
        return !StatusEffect.has(entity, StatEffect.PHASE_SUPPRESSANT);
    }

    @Override
    public boolean renderIcon(EntityPlayer player)
    {
        return SHData.SHADOWFORM.get(player);
    }

    @Override
    public int getX()
    {
        return 108;
    }

    @Override
    public int getY()
    {
        return 0;
    }
}
