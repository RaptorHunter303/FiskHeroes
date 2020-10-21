package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.hero.Hero;

import net.minecraftforge.event.TickEvent.Phase;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public interface HeroModifier
{
    String getUnlocalizedName();

    String getLocalizedName();

    String getName();

    default void onUpdate(LivingEntity entity, Hero hero, Phase phase, boolean enabled)
    {
    }

    default void onRemoved(LivingEntity entity, Hero hero)
    {
    }

    default boolean canTakeDamage(LivingEntity entity, LivingEntity attacker, Hero hero, DamageSource source, float amount)
    {
        return true;
    }

    default float damageTaken(LivingEntity entity, LivingEntity attacker, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        return amount;
    }

    default float damageDealt(LivingEntity entity, LivingEntity target, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        return amount;
    }

    default float damageReduction(LivingEntity entity, Hero hero, DamageSource source, float reduction)
    {
        return reduction;
    }
}
