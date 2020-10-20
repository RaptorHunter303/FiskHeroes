package com.fiskmods.heroes.common.container;

import java.util.List;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.item.IEquipmentItem;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.base.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDisplayStand extends ContainerBasic<TileEntityDisplayStand>
{
    public ContainerDisplayStand(InventoryPlayer inventoryPlayer, TileEntityDisplayStand tile)
    {
        super(tile);

        for (int i = 0; i < 4; ++i)
        {
            addSlotToContainer(new SlotArmor(tile, i, i, 71, 8 + i * 18, tile.fakePlayer));
        }

        for (int i = 0; i < 4; ++i)
        {
            addSlotToContainer(new SlotArmor(inventoryPlayer, i, inventoryPlayer.getSizeInventory() - 1 - i, 152, 8 + i * 18, inventoryPlayer.player));
        }

        addSlotToContainer(new SlotSpecial(tile, 4, 46, 8, t -> t.getItem() instanceof IEquipmentItem && ((IEquipmentItem) t.getItem()).canEquip(t, tile)));
        addSlotToContainer(new SlotSpecial(tile, TileEntityDisplayStand.SLOT_CASING, 8, 8, t -> t.getItem() == ModItems.displayCase));
        addSlotToContainer(new Slot(tile, 6, 46, 26));
        addPlayerInventory(inventoryPlayer, 0);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId)
    {
        ItemStack stack = null;
        Slot slot = (Slot) inventorySlots.get(slotId);
        int HELMET1 = 0;
        int BOOTS2 = 7;
        int CHEST = 8;
        int CASING = 9;
        int HAND = 10;
        int MAX = HAND;

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            stack = itemstack1.copy();

            if (slotId == CHEST || slotId == HAND)
            {
                if (!mergeItemStack(itemstack1, MAX + 28, MAX + 37, false) && !mergeItemStack(itemstack1, MAX + 1, MAX + 28, false))
                {
                    return null;
                }
            }
            else if (slotId > MAX)
            {
                boolean flag = true;

                for (int i = 0; i <= CASING; ++i)
                {
                    Slot slot1 = (Slot) inventorySlots.get(i);

                    if (slot1 != null && slot1.isItemValid(itemstack1))
                    {
                        if (mergeItemStack(itemstack1, i, i + 1, false))
                        {
                            flag = false;
                            break;
                        }
                    }
                }

                if (flag && tileentity.fakePlayer != null && SHHelper.isHero(tileentity.fakePlayer))
                {
                    Hero hero = SHHelper.getHero(tileentity.fakePlayer);
                    List<ItemStack> equipmentStacks = hero.getEquipmentStacks();

                    for (ItemStack equipment : equipmentStacks)
                    {
                        if (itemstack1.getItem() == equipment.getItem() && (equipment.isItemStackDamageable() || itemstack1.getItemDamage() == equipment.getItemDamage()))
                        {
                            if (mergeItemStack(itemstack1, HAND, HAND + 1, false))
                            {
                                flag = false;
                                break;
                            }
                        }
                    }
                }

                if (flag)
                {
                    if (slotId >= MAX + 28 && slotId < MAX + 37)
                    {
                        if (!mergeItemStack(itemstack1, MAX + 1, MAX + 28, false))
                        {
                            return null;
                        }
                    }
                    else if (!mergeItemStack(itemstack1, MAX + 28, MAX + 37, false))
                    {
                        return null;
                    }
                }
            }
            else if (slotId >= HELMET1 && slotId <= BOOTS2)
            {
                boolean flag = true;

                for (int i = 0; i <= CASING; ++i)
                {
                    Slot slot1 = (Slot) inventorySlots.get(i);

                    if (slot1 != null && slot1.isItemValid(itemstack1))
                    {
                        if (mergeItemStack(itemstack1, i, i + 1, false))
                        {
                            flag = false;
                            break;
                        }
                    }
                }

                if (flag && !mergeItemStack(itemstack1, MAX + 1, MAX + 37, false))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(itemstack1, MAX + 1, MAX + 37, false))
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

            if (itemstack1.stackSize == stack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return stack;
    }

    @Override
    public void detectAndSendChanges()
    {
        for (int i = 0; i < inventorySlots.size(); ++i)
        {
            ItemStack itemstack = ((Slot) inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack) inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack))
            {
                tileentity.markDirty();
                break;
            }
        }

        super.detectAndSendChanges();
    }

    private class SlotSpecial extends Slot
    {
        private final Predicate<ItemStack> predicate;

        public SlotSpecial(IInventory inventory, int index, int x, int y, Predicate<ItemStack> predicate)
        {
            super(inventory, index, x, y);
            this.predicate = predicate;
        }

        @Override
        public int getSlotStackLimit()
        {
            return 1;
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return predicate.apply(stack);
        }
    }
}
