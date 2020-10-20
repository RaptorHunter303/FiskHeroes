package com.fiskmods.heroes.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IDualItem
{
    float getSwingProgress(EntityPlayer player, float partialTicks);

    boolean onEntitySwingOffHand(EntityPlayer player, ItemStack itemstack);

    boolean onSwingEnd(EntityPlayer player, ItemStack itemstack, boolean right);
}
