package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.damagesource.IExtendedDamage.DamageType;
import com.fiskmods.heroes.common.hero.Hero;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class WeaknessCold extends Weakness
{
    @Override
    public float damageTaken(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        amount = super.damageTaken(entity, attacker, hero, source, amount, originalAmount);

        if (DamageType.COLD.isPresent(source))
        {
            amount *= 2;
        }

        return amount;
    }
}
