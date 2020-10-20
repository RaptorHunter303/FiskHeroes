package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class AbilitySpellcasting extends Ability
{
    public static final String FUNC_SPELLS = "getSpells";
    public static final String KEY_MENU = "SPELL_MENU";

    public AbilitySpellcasting(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END)
        {
            float f = SHData.SPELLCAST_TIMER.get(entity);
            f += (SHData.SPELL_FRACTION.get(entity) - f) / 4;

            SHData.SPELLCAST_TIMER.setWithoutNotify(entity, MathHelper.clamp_float(f, 0, 1));
        }
    }
}
