package com.fiskmods.heroes.common.book.widget;

import net.minecraft.item.ItemStack;

public interface IManualRecipe
{
    void putStacks(ItemStack output, ItemStack[] stacks);

    ItemStack getRecipeOutput();
}
