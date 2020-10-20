package com.fiskmods.heroes.common.container;

import com.fiskmods.heroes.common.item.ItemQuiver;
import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerQuiver extends ContainerBasic
{
    public final InventoryQuiver inventory;

    public ContainerQuiver(InventoryPlayer inventoryPlayer, InventoryQuiver inventoryQuiver)
    {
        super(inventoryPlayer.player.worldObj);
        inventory = inventoryQuiver;

        for (int i = 0; i < inventoryQuiver.getSizeInventory(); ++i)
        {
            addSlotToContainer(new Slot(inventoryQuiver, i, 44 + i * 18, 20)
            {
                @Override
                public boolean isItemValid(ItemStack itemstack)
                {
                    return itemstack.getItem() == ModItems.trickArrow;
                }
            });
        }

        addPlayerInventory(inventoryPlayer, -33);
    }

    @Override
    public Slot makeInventorySlot(InventoryPlayer inventoryPlayer, int index, int x, int y)
    {
        if (inventory.quiverItem == null && index == inventory.itemSlot)
        {
            return new Slot(inventoryPlayer, index, x, y)
            {
                @Override
                public boolean canTakeStack(EntityPlayer player)
                {
                    return false;
                }
            };
        }

        return super.makeInventorySlot(inventoryPlayer, index, x, y);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) inventorySlots.get(slotId);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (itemstack != null && itemstack.getItem() instanceof ItemQuiver)
            {
                return null;
            }

            if (slotId < inventory.getSizeInventory())
            {
                if (!mergeItemStack(itemstack1, inventory.getSizeInventory(), inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (itemstack1.getItem() == ModItems.trickArrow)
            {
                if (!mergeItemStack(itemstack1, 0, inventory.getSizeInventory(), false))
                {
                    return null;
                }
            }
            else
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
}
