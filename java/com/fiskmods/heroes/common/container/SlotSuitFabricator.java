package com.fiskmods.heroes.common.container;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.tileentity.TileEntityCosmicFabricator;
import com.fiskmods.heroes.common.tileentity.TileEntitySuitFabricator;
import com.fiskmods.heroes.util.FabricatorHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSuitFabricator extends Slot
{
    private final TileEntitySuitFabricator tile;

    public SlotSuitFabricator(TileEntitySuitFabricator tileentity, IInventory iinventory, int id, int x, int y)
    {
        super(iinventory, id, x, y);
        tile = tileentity;
    }

    @Override
    public boolean isItemValid(ItemStack itemstack)
    {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player)
    {
        Hero hero = tile.getHero();
        return hero != null && FabricatorHelper.getMaxTier(player) >= hero.getTier().tier;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack itemstack)
    {
        Hero hero = tile.getHero();
        int cost = FabricatorHelper.getMaterialCost(hero);
        int spent = 0;

        boolean flag = tile instanceof TileEntityCosmicFabricator;

        for (int slot = tile.getSizeInventory() - 1; slot >= 0; --slot)
        {
            ItemStack itemstack1 = tile.getStackInSlot(slot);

            if (itemstack1 != null)
            {
                int amount = 0;

                while (spent + tile.energy < cost && amount < itemstack1.stackSize)
                {
                    ++amount;
                    spent += FabricatorHelper.getEnergy(itemstack1, flag) / itemstack1.stackSize;
                }

                tile.decrStackSize(slot, amount);

                if (spent + tile.energy >= cost)
                {
                    break;
                }
            }
        }

        tile.energy += spent - cost;
    }
}
