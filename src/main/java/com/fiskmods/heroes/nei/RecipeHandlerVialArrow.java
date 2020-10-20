package com.fiskmods.heroes.nei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fiskmods.heroes.common.arrowtype.ArrowTypeManager;
import com.fiskmods.heroes.common.item.ItemTrickArrow;

import codechicken.nei.recipe.ShapelessRecipeHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class RecipeHandlerVialArrow extends ShapelessRecipeHandler
{
    @Override
    public String getRecipeName()
    {
        return StatCollector.translateToLocal("recipe.vial_arrow");
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("crafting") && getClass() == RecipeHandlerVialArrow.class)
        {
            List<ItemStack> subItems = new ArrayList<>();
            Items.potionitem.getSubItems(Items.potionitem, Items.potionitem.getCreativeTab(), subItems);

            for (ItemStack potion : subItems)
            {
                if (!ItemPotion.isSplash(potion.getItemDamage()))
                {
                    ItemStack arrow = ArrowTypeManager.VIAL.makeItem();
                    ItemStack potion1 = potion.copy();

                    potion1.setItemDamage(potion.getItemDamage() | 16384);
                    ItemTrickArrow.setItem(arrow, potion1);

                    arecipes.add(new CachedShapelessRecipe(Arrays.asList(ArrowTypeManager.NORMAL.makeItem(), potion), arrow));
                }
            }
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        if (ArrowTypeManager.VIAL.matches(result))
        {
            ItemStack potion = ItemTrickArrow.getItem(result);

            if (potion != null)
            {
                potion = potion.copy();
                potion.setItemDamage(potion.getItemDamage() & ~16384);

                arecipes.add(new CachedShapelessRecipe(Arrays.asList(ArrowTypeManager.NORMAL.makeItem(), potion), result));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        if (ArrowTypeManager.NORMAL.matches(ingredient))
        {
            List<ItemStack> subItems = new ArrayList<>();
            Items.potionitem.getSubItems(Items.potionitem, Items.potionitem.getCreativeTab(), subItems);

            for (ItemStack potion : subItems)
            {
                if (!ItemPotion.isSplash(potion.getItemDamage()))
                {
                    ItemStack arrow = ArrowTypeManager.VIAL.makeItem();
                    ItemStack potion1 = potion.copy();

                    potion1.setItemDamage(potion.getItemDamage() | 16384);
                    ItemTrickArrow.setItem(arrow, potion1);

                    arecipes.add(new CachedShapelessRecipe(Arrays.asList(ingredient, potion), arrow));
                }
            }
        }
        else if (ingredient.getItem() == Items.potionitem && !ItemPotion.isSplash(ingredient.getItemDamage()))
        {
            ItemStack arrow = ArrowTypeManager.VIAL.makeItem();
            ItemStack potion = ingredient.copy();

            potion.setItemDamage(ingredient.getItemDamage() | 16384);
            ItemTrickArrow.setItem(arrow, potion);

            arecipes.add(new CachedShapelessRecipe(Arrays.asList(ArrowTypeManager.NORMAL.makeItem(), ingredient), arrow));
        }
    }
}
