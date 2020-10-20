package com.fiskmods.heroes.common.recipe;

import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesAutomaticQuiver implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        int quiver = 0, hopper = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (stack.getItem() == ModItems.quiver && stack.getItemDamage() == 0)
                {
                    ++quiver;
                }
                else if (stack.getItem() == Item.getItemFromBlock(Blocks.hopper))
                {
                    ++hopper;
                }
                else
                {
                    return false;
                }
            }
        }

        return quiver == 1 && hopper == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null && stack.getItem() == ModItems.quiver)
            {
                stack = stack.copy();
                stack.stackSize = 1;
                stack.setItemDamage(1);

                return stack;
            }
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
        return new ItemStack(ModItems.quiver, 1, 1);
    }
}
