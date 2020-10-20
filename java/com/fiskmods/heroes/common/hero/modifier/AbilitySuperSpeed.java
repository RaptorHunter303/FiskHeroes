package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AbilitySuperSpeed extends Ability
{
    public static final String KEY_SUPER_SPEED = "SUPER_SPEED";
    public static final String KEY_DECELERATE = "DECELERATE";
    public static final String KEY_ACCELERATE = "ACCELERATE";

    public AbilitySuperSpeed(int tier)
    {
        super(tier);
    }

    @Override
    public boolean isActive(EntityLivingBase entity)
    {
        return SHData.SPEED.get(entity) >= 0 && !StatusEffect.has(entity, StatEffect.SPEED_DAMPENER);
    }

    @Override
    public boolean renderIcon(EntityPlayer player)
    {
        return SHData.SPEEDING.get(player);
    }

    @Override
    public int getX()
    {
        return 18;
    }

    @Override
    public int getY()
    {
        return 0;
    }
}
