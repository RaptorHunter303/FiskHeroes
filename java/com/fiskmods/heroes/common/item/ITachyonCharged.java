package com.fiskmods.heroes.common.item;

import net.minecraft.item.ItemStack;

public interface ITachyonCharged
{
    int getTachyonMaxCharge(ItemStack itemstack);

    boolean isTachyonBattery(ItemStack itemstack);

    boolean renderTachyonBar(ItemStack itemstack);
}
