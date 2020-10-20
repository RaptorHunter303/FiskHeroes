package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.damagesource.IExtendedDamage.DamageType;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class AbilityCactusPhysiology extends Ability
{
    public static final String KEY_SHOOT_SPIKES = "SHOOT_SPIKES";

    public AbilityCactusPhysiology(int tier)
    {
        super(tier);
    }

    @Override
    public boolean canTakeDamage(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount)
    {
        return !DamageType.CACTUS.isPresent(source) && super.canTakeDamage(entity, attacker, hero, source, amount);
    }

    @Override
    public float damageTaken(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        amount = super.damageTaken(entity, attacker, hero, source, amount, originalAmount);

        if (FiskServerUtils.isMeleeDamage(source) && !SHHelper.hasEnabledModifier(attacker, Ability.CACTUS_PHYSIOLOGY))
        {
            attacker.attackEntityFrom(DamageSource.causeThornsDamage(entity), Math.max(originalAmount * 0.25F, 1));
        }

        return amount;
    }
}
