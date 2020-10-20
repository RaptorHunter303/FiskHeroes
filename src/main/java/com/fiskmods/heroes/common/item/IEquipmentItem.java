package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;

import net.minecraft.item.ItemStack;

public interface IEquipmentItem
{
    boolean canEquip(ItemStack itemstack, TileEntityDisplayStand tile);
}
