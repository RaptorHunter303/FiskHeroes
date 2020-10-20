package com.fiskmods.heroes.common.recipe;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.item.ItemTachyonDevice;
import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesTachyonRecharge implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        int battery = 0, tachyon = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (stack.getItem() == Item.getItemFromBlock(ModBlocks.tachyonicParticleShell))
                {
                    ++tachyon;
                }
                else if (ItemTachyonDevice.isBattery(stack))
                {
                    ++battery;
                }
                else
                {
                    return false;
                }
            }
        }

        return battery == 1 && tachyon > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        ItemStack battery = null;
        int tachyon = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (ItemTachyonDevice.isBattery(stack))
                {
                    battery = stack.copy();
                    battery.stackSize = 1;
                }
                else if (stack.getItem() == Item.getItemFromBlock(ModBlocks.tachyonicParticleShell))
                {
                    ++tachyon;
                }
            }
        }

        if (battery != null)
        {
            ItemTachyonDevice.setCharge(battery, ItemTachyonDevice.getCharge(battery) + tachyon * SHConstants.MAX_TACHYON_RECHARGE);
            return battery;
        }

        return null;
    }

    @Override
    public int getRecipeSize()
    {
        return 10;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return new ItemStack(ModItems.tachyonDevice);
    }
}
