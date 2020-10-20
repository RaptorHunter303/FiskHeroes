package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;

public class AbilityBlade extends Ability
{
    public static final String KEY_BLADE = "BLADE";

    public AbilityBlade(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END)
        {
            SHHelper.incr(SHData.BLADE_TIMER, entity, SHConstants.TICKS_BLADE_UNFOLD, SHData.BLADE.get(entity));
        }
    }
}
