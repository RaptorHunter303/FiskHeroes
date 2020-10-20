package com.fiskmods.heroes.common.container;

import com.fiskmods.heroes.common.tileentity.TileEntitySuitFabricator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSuitFabricator extends ContainerBasic<TileEntitySuitFabricator>
{
    public ContainerSuitFabricator(InventoryPlayer inventory, TileEntitySuitFabricator tile)
    {
        super(tile);

        for (int i = 0; i < 6; ++i)
        {
            addSlotToContainer(new Slot(tile, i, 15 + i * 18, 94));
        }

        for (int i = 0; i < 4; ++i)
        {
            addSlotToContainer(new SlotSuitFabricator(tile, tileentity.output, i, 145, 18 + i * 18));
        }

        addPlayerInventory(inventory, 44);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) inventorySlots.get(slotId);
        int MATERIALS = 5;
        int OUTPUT = 9;

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotId <= OUTPUT && slotId > MATERIALS)
            {
                if (!mergeItemStack(itemstack1, OUTPUT + 1, OUTPUT + 36 + 1, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (slotId > OUTPUT)
            {
                if (!mergeItemStack(itemstack1, 0, MATERIALS + 1, false))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(itemstack1, OUTPUT + 1, OUTPUT + 37, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean func_94530_a(ItemStack itemstack, Slot slot)
    {
        return slot.inventory != tileentity.output && super.func_94530_a(itemstack, slot);
    }
}
