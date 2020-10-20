package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public class AbilityShield extends Ability
{
    public static final String FUNC_TOGGLE = "isToggleShield";
    public static final String KEY_SHIELD = "SHIELD";

    public AbilityShield(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END)
        {
            if (enabled && entity.worldObj.isRemote && FiskHeroes.proxy.isClientPlayer(entity))
            {
                boolean flag = Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed();

                if (!hero.getFuncBoolean(entity, FUNC_TOGGLE, true))
                {
                    flag = hero.isKeyPressed(entity, KEY_SHIELD);
                    SHData.SHIELD.set(entity, flag);
                }

                SHData.SHIELD_BLOCKING.set(entity, flag);
            }

            SHHelper.incr(SHData.SHIELD_TIMER, entity, SHConstants.TICKS_SHIELD_UNFOLD, SHData.SHIELD.get(entity));
            SHHelper.incr(SHData.SHIELD_BLOCKING_TIMER, entity, SHConstants.TICKS_SHIELD_BLOCKING, SHData.SHIELD_BLOCKING.get(entity));
        }
    }
}
