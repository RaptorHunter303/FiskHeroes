package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Tier;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class AbilitySteelTransformation extends Ability
{
    public static final String KEY_STEEL_TRANSFORM = "STEEL_TRANSFORM";

    public AbilitySteelTransformation(int tier)
    {
        super(tier);
    }

    @Override
    public float damageReduction(EntityLivingBase entity, Hero hero, DamageSource source, float reduction)
    {
        return SHData.STEELED.get(entity) ? Math.max(reduction, (float) Tier.T4.getProtection(entity)) : reduction;
    }
}
