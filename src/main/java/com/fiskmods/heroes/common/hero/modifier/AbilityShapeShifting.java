package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;

public class AbilityShapeShifting extends Ability
{
    public static final String KEY_SHAPE_SHIFT = "SHAPE_SHIFT";
    public static final String KEY_SHAPE_SHIFT_RESET = "SHAPE_SHIFT_RESET";

    public AbilityShapeShifting(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled)
        {
            if ((int) (SHData.SHAPE_SHIFT_TIMER.get(entity) * 100) / 100F == 0.5F)
            {
                String nextDisguise = SHData.SHAPE_SHIFTING_TO.get(entity);

                if (nextDisguise == null || !nextDisguise.isEmpty())
                {
                    SHData.DISGUISE.setWithoutNotify(entity, nextDisguise);
                }
            }

            SHHelper.incr(SHData.SHAPE_SHIFT_TIMER, entity, SHConstants.FADE_SHAPE_SHIFT, SHData.SHAPE_SHIFTING.get(entity));

            if (SHData.DISGUISE.get(entity) != null && SHData.DISGUISE.get(entity).isEmpty())
            {
                SHData.DISGUISE.setWithoutNotify(entity, null);
            }
        }
    }
}
