package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.hero.Hero;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class AbilityHeatResistance extends Ability
{
    public AbilityHeatResistance(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled)
        {
            entity.fireResistance = 60;
        }
    }

    @Override
    public void onRemoved(EntityLivingBase entity, Hero hero)
    {
        entity.fireResistance = 1;
    }

    @Override
    public float damageTaken(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        amount = super.damageTaken(entity, attacker, hero, source, amount, originalAmount);

        if (source.isFireDamage() && !source.isUnblockable())
        {
            amount /= 2;
        }

        return amount;
    }
}
