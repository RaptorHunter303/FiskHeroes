package com.fiskmods.heroes.common.container;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.enchantment.SHEnchantments;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.FiskServerUtils;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.util.Constants.NBT;

public class InventoryQuiver implements IInventory
{
    public final EntityPlayer thePlayer;
    public final ItemStack quiverItem;
    public final int itemSlot;

    private ItemStack[] itemstacks = new ItemStack[5];

    private InventoryQuiver(EntityPlayer player, ItemStack itemstack, int slot)
    {
        thePlayer = player;
        quiverItem = itemstack;
        itemSlot = slot;

        itemstack = getQuiverItem();

        if (itemstack != null)
        {
            if (!itemstack.hasTagCompound())
            {
                itemstack.setTagCompound(new NBTTagCompound());
            }

            readFromNBT(itemstack.getTagCompound());
        }
    }

    public InventoryQuiver(EntityPlayer player, ItemStack itemstack)
    {
        this(player, itemstack, -1);
    }

    public InventoryQuiver(EntityPlayer player, int slot)
    {
        this(player, null, slot);
    }

    public ItemStack getQuiverItem()
    {
        return quiverItem == null ? FiskServerUtils.getStackInSlot(thePlayer, itemSlot) : quiverItem;
    }

    public ItemStack getCurrentArrow()
    {
        int i = getFirstSlotID();
        int k = SHData.SELECTED_ARROW.get(thePlayer);

        if (itemstacks[k] != null)
        {
            i = k;
        }

        if (i < 0)
        {
            i = 0;
        }

        return getStackInSlot(i);
    }

    public ItemStack getFirstItemStack()
    {
        for (ItemStack itemstack : itemstacks)
        {
            if (itemstack != null)
            {
                return itemstack;
            }
        }

        return null;
    }

    public int getFirstSlotID()
    {
        for (int i = 0; i < itemstacks.length; ++i)
        {
            ItemStack itemstack = itemstacks[i];

            if (itemstack != null)
            {
                return i;
            }
        }

        return -1;
    }

    public void consumeArrowItemStack()
    {
        int i = getFirstSlotID();
        int k = SHData.SELECTED_ARROW.get(thePlayer);

        if (itemstacks[k] != null)
        {
            i = k;
        }

        if (i >= 0)
        {
            if (--itemstacks[i].stackSize <= 0)
            {
                itemstacks[i] = null;
            }

            markDirty();
        }
    }

    public boolean addItemStackToInventory(ItemStack itemstack)
    {
        if (addItemStackToInventoryTemp(itemstack))
        {
            markDirty();
            return true;
        }

        return false;
    }

    private boolean addItemStackToInventoryTemp(final ItemStack itemstack)
    {
        if (itemstack != null && itemstack.stackSize != 0 && itemstack.getItem() != null)
        {
            try
            {
                if (itemstack.isItemDamaged())
                {
                    int slot = getFirstEmptyStack();

                    if (slot >= 0)
                    {
                        itemstacks[slot] = ItemStack.copyItemStack(itemstack);
                        itemstacks[slot].animationsToGo = 5;
                        itemstack.stackSize = 0;
                        return true;
                    }

                    return false;
                }

                int stackSize;

                do
                {
                    stackSize = itemstack.stackSize;
                    itemstack.stackSize = storePartialItemStack(itemstack);
                }
                while (itemstack.stackSize > 0 && itemstack.stackSize < stackSize);

                return itemstack.stackSize < stackSize;
            }
            catch (Throwable throwable)
            {
                CrashReport crash = CrashReport.makeCrashReport(throwable, "Adding item to quiver");
                CrashReportCategory category = crash.makeCategory("Item being added");
                category.addCrashSection("Item ID", Item.getIdFromItem(itemstack.getItem()));
                category.addCrashSection("Item data", itemstack.getItemDamage());
                category.addCrashSectionCallable("Item name", () -> itemstack.getDisplayName());
                throw new ReportedException(crash);
            }
        }

        return false;
    }

    public int getFirstEmptyStack()
    {
        for (int i = 0; i < itemstacks.length; ++i)
        {
            if (itemstacks[i] == null)
            {
                return i;
            }
        }

        return -1;
    }

    private int storePartialItemStack(ItemStack itemstack)
    {
        Item item = itemstack.getItem();
        int toAdd = itemstack.stackSize;
        int slot;

        if (itemstack.getMaxStackSize() == 1)
        {
            slot = getFirstEmptyStack();

            if (slot < 0)
            {
                return toAdd;
            }

            if (itemstacks[slot] == null)
            {
                itemstacks[slot] = ItemStack.copyItemStack(itemstack);
            }

            return 0;
        }

        slot = storeItemStack(itemstack);

        if (slot < 0)
        {
            slot = getFirstEmptyStack();
        }

        if (slot < 0)
        {
            return toAdd;
        }

        if (itemstacks[slot] == null)
        {
            itemstacks[slot] = new ItemStack(item, 0, itemstack.getItemDamage());

            if (itemstack.hasTagCompound())
            {
                itemstacks[slot].setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
            }
        }

        int i = toAdd;

        if (toAdd > itemstacks[slot].getMaxStackSize() - itemstacks[slot].stackSize)
        {
            i = itemstacks[slot].getMaxStackSize() - itemstacks[slot].stackSize;
        }

        if (i > getInventoryStackLimit() - itemstacks[slot].stackSize)
        {
            i = getInventoryStackLimit() - itemstacks[slot].stackSize;
        }

        if (i == 0)
        {
            return toAdd;
        }

        itemstacks[slot].stackSize += i;
        itemstacks[slot].animationsToGo = 5;

        return toAdd - i;
    }

    private int storeItemStack(ItemStack itemstack)
    {
        for (int i = 0; i < itemstacks.length; ++i)
        {
            if (itemstacks[i] != null && itemstacks[i].getItem() == itemstack.getItem() && itemstacks[i].isStackable() && itemstacks[i].stackSize < itemstacks[i].getMaxStackSize() && itemstacks[i].stackSize < getInventoryStackLimit() && (!itemstacks[i].getHasSubtypes() || itemstacks[i].getItemDamage() == itemstack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstacks[i], itemstack))
            {
                return i;
            }
        }

        return -1;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        NBTTagList nbttaglist = nbt.getTagList("Slots", NBT.TAG_COMPOUND);
        boolean flag = false;

        itemstacks = new ItemStack[getSizeInventory()];

        for (int i = 0; i < getSizeInventory(); i++)
        {
            if (nbt.hasKey("ext" + i, NBT.TAG_COMPOUND))
            {
                NBTTagCompound tag = nbt.getCompoundTag("ext" + i);
                tag.setByte("Slot", (byte) i);

                nbttaglist.appendTag(tag);
                nbt.removeTag("ext" + i);
                flag = true;
            }
        }

        for (int i = 0; i < Math.min(nbttaglist.tagCount(), getSizeInventory()); ++i)
        {
            NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
            itemstacks[tag.getByte("Slot")] = ItemStack.loadItemStackFromNBT(tag);
        }

        if (flag)
        {
            nbt.setTag("Slots", nbttaglist);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < getSizeInventory(); i++)
        {
            if (itemstacks[i] != null)
            {
                NBTTagCompound tag = itemstacks[i].writeToNBT(new NBTTagCompound());
                tag.setByte("Slot", (byte) i);

                nbttaglist.appendTag(tag);
            }
        }

        nbt.setTag("Slots", nbttaglist);

        return nbt;
    }

    @Override
    public int getSizeInventory()
    {
        return itemstacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return itemstacks[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        ItemStack stack = getStackInSlot(slot);

        if (stack != null)
        {
            if (stack.stackSize > amount)
            {
                stack = stack.splitStack(amount);
                markDirty();
            }
            else
            {
                setInventorySlotContents(slot, null);
            }
        }

        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        ItemStack stack = getStackInSlot(slot);
        setInventorySlotContents(slot, null);

        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        itemstacks[slot] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit())
        {
            stack.stackSize = getInventoryStackLimit();
        }

        markDirty();
    }

    @Override
    public String getInventoryName()
    {
        return "Quiver";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        ItemStack quiver = getQuiverItem();

        if (quiver != null)
        {
            return 16 + 16 * EnchantmentHelper.getEnchantmentLevel(SHEnchantments.capacity.effectId, quiver);
        }

        return 16;
    }

    @Override
    public void markDirty()
    {
        ItemStack quiver = getQuiverItem();

        if (quiver != null && !thePlayer.worldObj.isRemote)
        {
            writeToNBT(quiver.getTagCompound());
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        if (quiverItem == null)
        {
            ItemStack quiver = getQuiverItem();
            return quiver != null && quiver.getItem() == ModItems.quiver;
        }

        return true;
    }

    @Override
    public void openInventory()
    {
    }

    @Override
    public void closeInventory()
    {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return stack.getItem() == ModItems.trickArrow;
    }
}
