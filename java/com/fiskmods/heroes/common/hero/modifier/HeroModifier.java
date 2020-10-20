package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.hero.Hero;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public interface HeroModifier
{
    String getUnlocalizedName();

    String getLocalizedName();

    String getName();

    default void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
    }

    default void onRemoved(EntityLivingBase entity, Hero hero)
    {
    }

    default boolean canTakeDamage(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount)
    {
        return true;
    }

    default float damageTaken(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        return amount;
    }

    default float damageDealt(EntityLivingBase entity, EntityLivingBase target, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        return amount;
    }

    default float damageReduction(EntityLivingBase entity, Hero hero, DamageSource source, float reduction)
    {
        return reduction;
    }
}
