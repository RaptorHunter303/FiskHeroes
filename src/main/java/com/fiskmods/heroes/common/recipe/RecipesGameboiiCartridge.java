package com.fiskmods.heroes.common.recipe;

import com.fiskmods.heroes.common.item.ItemGameboii;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.gameboii.GameboiiCartridge;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesGameboiiCartridge implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        ItemStack gameboii = null;
        int console = 0, game = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (stack.getItem() == ModItems.gameboii)
                {
                    gameboii = stack;
                    ++console;
                }
                else if (stack.getItem() == ModItems.gameboiiCartridge)
                {
                    ++game;
                }
                else
                {
                    return false;
                }
            }
        }

        return console == 1 && (game == 1 || ItemGameboii.get(gameboii) != null);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        ItemStack console = null, game = null;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (stack.getItem() == ModItems.gameboii)
                {
                    console = stack;
                }
                else
                {
                    game = stack;
                }
            }
        }

        if (console != null)
        {
            return ItemGameboii.set(console.copy(), GameboiiCartridge.get(game));
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
        return new ItemStack(ModItems.gameboii);
    }
}
