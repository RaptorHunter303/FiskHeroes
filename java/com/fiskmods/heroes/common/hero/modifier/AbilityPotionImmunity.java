package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.FiskServerUtils;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class AbilityPotionImmunity extends Ability
{
    public final Potion[] immunities;

    public AbilityPotionImmunity(int tier, Potion... potions)
    {
        super(tier);
        immunities = potions;
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.START && enabled)
        {
            for (Potion potion : immunities)
            {
                FiskServerUtils.cure(entity, potion);
            }
        }
    }
}
