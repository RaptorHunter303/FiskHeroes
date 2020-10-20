package com.fiskmods.heroes.common.recipe;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.common.BlockStack;
import com.fiskmods.heroes.common.item.ItemDisplayCase;
import com.fiskmods.heroes.common.item.ItemDisplayCase.DisplayCase;
import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesDisplayCase implements IRecipe
{
    private final int[][] allowedBlocks = {{0, -1}, {0, 1}, {-1, -1}, {1, 0}, {-1, 0}};

    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        int width = (int) Math.sqrt(inventory.getSizeInventory());
        ItemStack bottom = null;

        Integer[] casingPos = null;
        List<Integer[]> blocks = new ArrayList<>();

        try
        {
            for (int i = 0; i < inventory.getSizeInventory(); ++i)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                int row = i % width;
                int column = i / width;

                if (stack != null)
                {
                    if (casingPos == null && stack.getItem() == ModItems.displayCase)
                    {
                        casingPos = new Integer[] {row, column};
                        bottom = inventory.getStackInRowAndColumn(row, column + 1);
                    }
                    else if (!(stack.getItem() instanceof ItemBlock))
                    {
                        return false;
                    }
                    else
                    {
                        blocks.add(new Integer[] {row, column});
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (casingPos == null)
        {
            return false;
        }

        for (Integer[] pos : blocks)
        {
            boolean flag = false;

            for (int[] aint : allowedBlocks)
            {
                if (pos[0] == aint[0] + casingPos[0] && pos[1] == aint[1] + casingPos[1])
                {
                    flag = true;
                    break;
                }
            }

            if (!flag)
            {
                return false;
            }
        }

        return bottom != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        int width = (int) Math.sqrt(inventory.getSizeInventory());

        try
        {
            for (int i = 0; i < inventory.getSizeInventory(); ++i)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                int row = i % width;
                int column = i / width;

                if (stack != null)
                {
                    if (stack.getItem() == ModItems.displayCase)
                    {
                        DisplayCase casing = new DisplayCase();

                        if (column - 1 >= 0)
                        {
                            casing.setTop(BlockStack.fromItemStack(inventory.getStackInRowAndColumn(row, column - 1)));

                            if (row - 1 >= 0)
                            {
                                casing.setCorners(BlockStack.fromItemStack(inventory.getStackInRowAndColumn(row - 1, column - 1)));
                            }
                        }

                        if (column + 1 < width)
                        {
                            casing.setBottom(BlockStack.fromItemStack(inventory.getStackInRowAndColumn(row, column + 1)));
                        }

                        if (row + 1 < width)
                        {
                            casing.setFront(BlockStack.fromItemStack(inventory.getStackInRowAndColumn(row + 1, column)));
                        }

                        if (row - 1 >= 0)
                        {
                            casing.setWalls(BlockStack.fromItemStack(inventory.getStackInRowAndColumn(row - 1, column)));
                        }

                        ItemStack result = new ItemStack(ModItems.displayCase);
                        ItemDisplayCase.setCasing(result, casing);

                        return result;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
        return new ItemStack(ModItems.displayCase);
    }
}
