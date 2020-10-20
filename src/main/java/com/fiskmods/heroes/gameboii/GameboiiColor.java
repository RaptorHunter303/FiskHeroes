package com.fiskmods.heroes.gameboii;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public enum GameboiiColor
{
    BLACK,
    BLUE,
    GREEN,
    ORANGE,
    PURPLE,
    RED,
    WHITE,
    YELLOW;

    public static GameboiiColor get(int damage)
    {
        return values()[MathHelper.clamp_int(damage, 0, values().length - 1)];
    }

    public static GameboiiColor get(ItemStack stack)
    {
        return get(stack.getItemDamage());
    }
}
