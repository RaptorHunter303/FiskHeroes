package com.fiskmods.heroes.common.item;

import net.minecraft.item.ItemStack;

public interface IScopeWeapon
{
    default boolean canUseScope(ItemStack stack)
    {
        return true;
    }

    boolean isProperScope();
}
