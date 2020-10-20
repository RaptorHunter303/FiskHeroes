package com.fiskmods.heroes.nei;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.item.ItemSubatomicBattery;
import com.fiskmods.heroes.common.item.ItemTachyonDevice;

import codechicken.nei.recipe.ShapedRecipeHandler;
import net.minecraft.item.ItemStack;

public class RecipeHandlerBattery extends ShapedRecipeHandler
{
    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        if (result.getItem() instanceof ItemSubatomicBattery && ItemSubatomicBattery.getCharge(result) > 0)
        {
            super.loadCraftingRecipes(new ItemStack(result.getItem()));
        }
        else if (result.getItem() instanceof ItemTachyonDevice)
        {
            super.loadCraftingRecipes(new ItemStack(result.getItem(), 1, SHConstants.MAX_CHARGE_DEVICE));
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        if (ingredient.getItem() instanceof ItemSubatomicBattery && ItemSubatomicBattery.getCharge(ingredient) < 1)
        {
            super.loadUsageRecipes(new ItemStack(ingredient.getItem(), 1, SHConstants.MAX_SUBATOMIC_CHARGE));
        }
    }
}
