package com.fiskmods.heroes.common.item;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IDualItem
{
    float getSwingProgress(PlayerEntity player, float partialTicks);

    boolean onEntitySwingOffHand(PlayerEntity player, ItemStack itemstack);

    boolean onSwingEnd(PlayerEntity player, ItemStack itemstack, boolean right);
}
