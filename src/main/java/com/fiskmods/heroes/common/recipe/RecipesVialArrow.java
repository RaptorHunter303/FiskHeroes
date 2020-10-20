package com.fiskmods.heroes.common.recipe;

import com.fiskmods.heroes.common.arrowtype.ArrowTypeManager;
import com.fiskmods.heroes.common.item.ItemTrickArrow;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesVialArrow implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        int arrow = 0, potion = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (ArrowTypeManager.NORMAL.matches(stack))
                {
                    ++arrow;
                }
                else if (stack.getItem() == Items.potionitem && !ItemPotion.isSplash(stack.getItemDamage()))
                {
                    ++potion;
                }
                else
                {
                    return false;
                }
            }
        }

        return arrow == 1 && potion == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        ItemStack arrow = null;
        ItemStack potion = null;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (stack.getItem() == Items.potionitem)
                {
                    potion = stack;
                }
                else
                {
                    arrow = stack;
                }
            }
        }

        if (arrow != null && potion != null)
        {
            ItemStack stack = ArrowTypeManager.VIAL.makeItem();
            potion = potion.copy();

            potion.setItemDamage(potion.getItemDamage() & ~8192 | 16384);
            potion.stackSize = 1;

            ItemTrickArrow.setItem(stack, potion);
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
        ItemStack itemstack = ArrowTypeManager.VIAL.makeItem();
        ItemTrickArrow.setItem(itemstack, new ItemStack(Items.potionitem, 1, 8192));

        return itemstack;
    }
}
