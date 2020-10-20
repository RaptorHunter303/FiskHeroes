package com.fiskmods.heroes.common.recipe;

import com.fiskmods.heroes.common.arrowtype.ArrowTypeManager;
import com.fiskmods.heroes.common.book.widget.IManualRecipe;
import com.fiskmods.heroes.common.item.ItemTrickArrow;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public enum ManualRecipeVialArrow implements IManualRecipe
{
    INSTANCE;

    @Override
    public void putStacks(ItemStack output, ItemStack[] stacks)
    {
        stacks[0] = ArrowTypeManager.NORMAL.makeItem();
        stacks[1] = new ItemStack(Items.potionitem, 1, 0);
        stacks[1].setStackDisplayName(I18n.format("book.metahumanLog.potionAny"));

        ItemStack potion = stacks[1].copy();
        potion.setItemDamage(potion.getItemDamage() | 16384);

        ItemTrickArrow.setItem(output, potion);
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ArrowTypeManager.VIAL.makeItem();
    }
}
