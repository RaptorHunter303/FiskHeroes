package com.fiskmods.heroes.common.container;

import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.ItemHeroArmor;
import com.fiskmods.heroes.common.item.ItemQuiver;
import com.fiskmods.heroes.common.tileentity.TileEntityContainer;
import com.fiskmods.heroes.common.tileentity.TileEntityContainer.ITileContainerListener;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ContainerSuitIterator extends ContainerBasic implements ITileContainerListener
{
    public IInventory input = new InventoryBasic("Iterate", true, 4);
    private int posX;
    private int posY;
    private int posZ;

    public int iterationId = -1;

    public ContainerSuitIterator(InventoryPlayer inventory, World world, int x, int y, int z)
    {
        super(world);
        posX = x;
        posY = y;
        posZ = z;
        TileEntity tileentity = world.getTileEntity(x, y + 1, z);

        if (tileentity instanceof TileEntityDisplayStand)
        {
            TileEntityDisplayStand tile = (TileEntityDisplayStand) tileentity;
            tile.addListener(this);
            input = tile;

            updateInput();
        }
        else
        {
            ((InventoryBasic) input).func_110134_a(new Listener());
        }

        for (int i = 0; i < 4; ++i)
        {
            addSlotToContainer(new SlotArmor(input, i, i, 15, 18 + i * 18, inventory.player));
        }

        addPlayerInventory(inventory, 22);
    }

    private class Listener implements IInvBasic
    {
        @Override
        public void onInventoryChanged(InventoryBasic inventory)
        {
            onCraftMatrixChanged(inventory);
        }
    }

    @Override
    public void onInventoryChanged(TileEntityContainer tile)
    {
        onCraftMatrixChanged(tile);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
        if (inventory == input)
        {
            updateInput();
        }
    }

    private void updateInput()
    {
        HeroIteration iter = SHHelper.getHeroIter(SHHelper.getEquipment(input, 0));
        iterationId = iter != null ? iter.getId() : -1;

        for (Object crafter : crafters)
        {
            ICrafting icrafting = (ICrafting) crafter;
            icrafting.sendProgressBarUpdate(this, 0, iterationId);
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting)
    {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, iterationId);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        if (id == 0)
        {
            iterationId = value;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);

        if (input instanceof TileEntityDisplayStand)
        {
            ((TileEntityDisplayStand) input).removeListener(this);
        }

        if (!worldObj.isRemote && input instanceof InventoryBasic)
        {
            for (int i = 0; i < input.getSizeInventory(); ++i)
            {
                ItemStack itemstack = input.getStackInSlotOnClosing(i);

                if (itemstack != null)
                {
                    player.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return worldObj.getBlock(posX, posY, posZ) == ModBlocks.suitIterator && player.getDistanceSq(posX + 0.5, posY + 0.5, posZ + 0.5) <= 64;
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

            if (slotId < input.getSizeInventory())
            {
                if (!mergeItemStack(itemstack1, input.getSizeInventory(), inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else
            {
                boolean flag = true;

                for (int i = 0; i < input.getSizeInventory(); ++i)
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

                if (flag)
                {
                    return null;
                }
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

    public void updateIteration(int id)
    {
        Hero hero = SHHelper.getHero(SHHelper.getEquipment(input, 0));
        iterationId = id;

        if (hero != null)
        {
            HeroIteration iter = hero.getIteration(iterationId);

            for (int i = 0; i < 4; ++i)
            {
                ItemStack stack = input.getStackInSlot(i);

                if (stack != null)
                {
                    ItemHeroArmor.set(stack, iter, false);
                }
            }

            if (input instanceof TileEntityDisplayStand)
            {
                input.markDirty();
            }
        }

        detectAndSendChanges();
    }
}
