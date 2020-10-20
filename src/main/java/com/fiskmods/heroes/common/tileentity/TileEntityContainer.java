package com.fiskmods.heroes.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.util.SHTileHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public abstract class TileEntityContainer extends TileEntitySH implements IInventory
{
    protected ItemStack[] inventory = new ItemStack[getSizeInventory()];
    private List<ITileContainerListener> listeners;

    public void addListener(ITileContainerListener listener)
    {
        if (listeners == null)
        {
            listeners = new ArrayList<>();
        }

        listeners.add(listener);
    }

    public void removeListener(ITileContainerListener listener)
    {
        listeners.remove(listener);
    }

    public void notifyInventoryListeners()
    {
        if (listeners != null)
        {
            listeners.forEach(t -> t.onInventoryChanged(this));
        }
    }

    public ItemStack[] getItemStacks()
    {
        if (hasWorldObj())
        {
            TileEntityContainer base = SHTileHelper.getTileBase(this);

            if (base != this)
            {
                if (base != null)
                {
                    return base.getItemStacks();
                }

                return new ItemStack[getSizeInventory()];
            }
        }

        return inventory;
    }

    public void setItemStacks(ItemStack[] itemstacks)
    {
        if (hasWorldObj())
        {
            TileEntityContainer base = SHTileHelper.getTileBase(this);

            if (base != this)
            {
                if (base != null)
                {
                    base.setItemStacks(itemstacks);
                }

                return;
            }
        }

        inventory = itemstacks;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return getItemStacks()[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (getItemStacks()[slot] != null)
        {
            ItemStack itemstack;

            if (getItemStacks()[slot].stackSize <= amount)
            {
                itemstack = getItemStacks()[slot];
                getItemStacks()[slot] = null;
                notifyInventoryListeners();
                return itemstack;
            }
            else
            {
                itemstack = getItemStacks()[slot].splitStack(amount);

                if (getItemStacks()[slot].stackSize == 0)
                {
                    getItemStacks()[slot] = null;
                }

                notifyInventoryListeners();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (getItemStacks()[slot] != null)
        {
            ItemStack itemstack = getItemStacks()[slot];
            getItemStacks()[slot] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack)
    {
        getItemStacks()[slot] = itemstack;

        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }

        notifyInventoryListeners();
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        if (!nbt.hasKey("LoadInventory", NBT.TAG_BYTE) || nbt.getBoolean("LoadInventory"))
        {
            NBTTagList nbttaglist = nbt.getTagList("Items", 10);
            setItemStacks(new ItemStack[getSizeInventory()]);

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                byte slot = nbttagcompound1.getByte("Slot");

                if (slot >= 0 && slot < getItemStacks().length)
                {
                    getItemStacks()[slot] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
                }
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        boolean flag = SHTileHelper.getTileBase(this) == this;
        nbt.setBoolean("LoadInventory", flag);

        if (!flag)
        {
            return;
        }

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < getItemStacks().length; ++i)
        {
            if (getItemStacks()[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                getItemStacks()[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        nbt.setTag("Items", nbttaglist);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getBlock(xCoord, yCoord, zCoord) == getBlockType() && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
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
        return true;
    }

    public interface ITileContainerListener
    {
        void onInventoryChanged(TileEntityContainer tile);
    }
}
