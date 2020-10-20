package com.fiskmods.heroes.common.recipe;

import com.fiskmods.heroes.common.arrowtype.ArrowTypeManager;
import com.fiskmods.heroes.common.item.ItemTrickArrow;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesFireworkArrow implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        int arrow = 0, firework = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (ArrowTypeManager.NORMAL.matches(stack))
                {
                    ++arrow;
                }
                else if (stack.getItem() == Items.fireworks)
                {
                    ++firework;
                }
                else
                {
                    return false;
                }
            }
        }

        return arrow == 1 && firework == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        ItemStack arrow = null;
        ItemStack firework = null;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (stack.getItem() == Items.fireworks)
                {
                    firework = stack;
                }
                else
                {
                    arrow = stack;
                }
            }
        }

        if (arrow != null && firework != null)
        {
            ItemStack stack = ArrowTypeManager.FIREWORK.makeItem();
            firework = firework.copy();
            firework.stackSize = 1;

            ItemTrickArrow.setItem(stack, firework);
            return stack;
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
        return ArrowTypeManager.FIREWORK.makeItem();
    }
}
