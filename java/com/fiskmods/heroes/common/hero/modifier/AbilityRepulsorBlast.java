package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;

public class AbilityRepulsorBlast extends Ability
{
    public AbilityRepulsorBlast(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.START && enabled)
        {
            if (SHData.AIMING.get(entity) && !SHData.PREV_AIMING.get(entity))
            {
                entity.playSound(SHSounds.ABILITY_REPULSOR_CHARGE.toString(), 1.0F, 1.0F);
            }
            else if (!SHData.AIMING.get(entity) && SHData.PREV_AIMING.get(entity))
            {
                entity.playSound(SHSounds.ABILITY_REPULSOR_POWERDOWN.toString(), 0.5F, 1.0F);
            }
        }
    }
}
