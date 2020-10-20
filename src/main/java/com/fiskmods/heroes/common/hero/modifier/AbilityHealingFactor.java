package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class AbilityHealingFactor extends AbilityPotionImmunity
{
    public AbilityHealingFactor(int tier)
    {
        super(tier, Potion.poison, Potion.wither);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        super.onUpdate(entity, hero, phase, enabled);

        if (phase == Phase.END && enabled)
        {
            if (entity.getHealth() < entity.getMaxHealth() && SHData.TIME_SINCE_DAMAGED.get(entity) > SHConstants.TICKS_HEALING_FACTOR)
            {
                entity.heal(1.0F);
            }
        }
    }
}
