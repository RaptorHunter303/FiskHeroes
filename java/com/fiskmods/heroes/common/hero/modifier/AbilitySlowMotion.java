package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.data.SHData;

import net.minecraft.entity.player.EntityPlayer;

public class AbilitySlowMotion extends Ability
{
    public static final String KEY_SLOW_MOTION = "SLOW_MOTION";

    public AbilitySlowMotion(int tier)
    {
        super(tier);
    }

    @Override
    public boolean renderIcon(EntityPlayer player)
    {
        return SHData.SLOW_MOTION.get(player);
    }

    @Override
    public int getX()
    {
        return 36;
    }

    @Override
    public int getY()
    {
        return 0;
    }
}
