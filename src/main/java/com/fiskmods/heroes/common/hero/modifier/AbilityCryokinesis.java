package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.TemperatureHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class AbilityCryokinesis extends Ability
{
    public static final String KEY_CHARGE_ICE = "CHARGE_ICE";

    public AbilityCryokinesis(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled)
        {
            SHHelper.incr(SHData.CRYO_CHARGE, entity, SHConstants.TICKS_CRYO_CHARGE, SHData.CRYO_CHARGING.get(entity), false);

            if (entity.worldObj.isRemote && FiskHeroes.proxy.isClientPlayer(entity))
            {
                boolean flag = SHData.CRYO_CHARGING.get(entity);
                SHData.CRYO_CHARGING.set(entity, hero.isKeyPressed(entity, KEY_CHARGE_ICE) && SHData.CRYO_CHARGE.get(entity) < 1);

                if (!flag && SHData.CRYO_CHARGING.get(entity))
                {
                    FiskHeroes.proxy.playSound(entity, "cryokinesis", 0, 0);
                }
            }
        }
    }

    @Override
    public float damageDealt(EntityLivingBase entity, EntityLivingBase target, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        if (FiskServerUtils.isMeleeDamage(source))
        {
            amount += Rule.DMG_ICEFISTBONUS.get(entity, hero) * SHData.CRYO_CHARGE.get(entity);

            if (!entity.worldObj.isRemote)
            {
                if (TemperatureHelper.getCurrentBodyTemperature(target) > 0)
                {
                    TemperatureHelper.setTemperature(target, TemperatureHelper.getTemperature(target) - 1);
                }

                SHData.CRYO_CHARGE.incr(entity, -1F / SHConstants.ICE_FIST_USES);
            }
        }

        return amount;
    }
}
