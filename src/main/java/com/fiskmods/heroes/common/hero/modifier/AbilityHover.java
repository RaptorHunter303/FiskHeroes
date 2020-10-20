package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.data.SHData;

import net.minecraft.entity.player.EntityPlayer;

public class AbilityHover extends Ability
{
    public static final String KEY_HOVER = "HOVER";

    public AbilityHover(int tier)
    {
        super(tier);
    }

    @Override
    public boolean renderIcon(EntityPlayer player)
    {
        return SHData.HOVERING.get(player);
    }

    @Override
    public int getX()
    {
        return 90;
    }

    @Override
    public int getY()
    {
        return 0;
    }
}
