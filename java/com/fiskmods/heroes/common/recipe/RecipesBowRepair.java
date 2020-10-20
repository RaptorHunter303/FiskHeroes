package com.fiskmods.heroes.common.recipe;

import com.fiskmods.heroes.common.item.ItemCompoundBow;
import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesBowRepair implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        int bow = 0, string = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (stack.getItem() == ModItems.compoundBow && ItemCompoundBow.isBroken(stack))
                {
                    ++bow;
                }
                else if (stack.getItem() == Items.string)
                {
                    ++string;
                }
                else
                {
                    return false;
                }
            }
        }

        return bow == 1 && string == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null && stack.getItem() == ModItems.compoundBow && ItemCompoundBow.isBroken(stack))
            {
                return ItemCompoundBow.setBroken(stack.copy(), false);
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
        return new ItemStack(ModItems.compoundBow);
    }
}
