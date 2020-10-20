package com.fiskmods.heroes.common.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class SlotArmor extends Slot
{
    protected final EntityPlayer player;
    protected final int armorIndex;

    public SlotArmor(IInventory inventory, int armorIndex, int index, int x, int y, EntityPlayer player)
    {
        super(inventory, index, x, y);
        this.armorIndex = armorIndex;
        this.player = player;
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return stack != null && stack.getItem().isValidArmor(stack, armorIndex, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getBackgroundIconIndex()
    {
        return ItemArmor.func_94602_b(armorIndex);
    }
}
