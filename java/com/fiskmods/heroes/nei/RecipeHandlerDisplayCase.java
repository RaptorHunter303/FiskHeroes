package com.fiskmods.heroes.nei;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.common.BlockStack;
import com.fiskmods.heroes.common.item.ItemDisplayCase;
import com.fiskmods.heroes.common.item.ItemDisplayCase.DisplayCase;
import com.fiskmods.heroes.common.item.ModItems;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class RecipeHandlerDisplayCase extends ShapedRecipeHandler
{
    public class CachedCasingRecipe extends CachedShapedRecipe
    {
        public CachedCasingRecipe(int width, int height, Object[] items, ItemStack out)
        {
            super(width, height, items, out);
            result = new PositionedStack(out, 119, 24);
            ingredients = new ArrayList<>();
            setIngredients(width, height, items);
        }

        @Override
        public void setIngredients(int width, int height, Object[] items)
        {
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    if (items[y * width + x] == null)
                    {
                        continue;
                    }

                    PositionedStack stack = new PositionedStack(items[y * width + x], 25 + x * 18, 6 + y * 18, false);
                    stack.setMaxSize(1);
                    ingredients.add(stack);
                    // map.put(y * width + x, stack);
                }
            }
        }

        @Override
        public List<PositionedStack> getIngredients()
        {
            return getCycledIngredients(cycleticks / 20, ingredients);
        }

        @Override
        public PositionedStack getResult()
        {
            return result;
        }

        @Override
        public void computeVisuals()
        {
            for (PositionedStack p : ingredients)
            {
                p.generatePermutations();
            }
        }
    }

    @Override
    public String getRecipeName()
    {
        return StatCollector.translateToLocal("recipe.display_case");
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("crafting") && getClass() == RecipeHandlerDisplayCase.class)
        {

        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        if (result.getItem() == ModItems.displayCase && ItemDisplayCase.isModified(result))
        {
            DisplayCase casing = ItemDisplayCase.getCasing(result);
            Object[] ingredients = new Object[9];
            ingredients[0] = BlockStack.toItemStackSafe(casing.corners);
            ingredients[1] = BlockStack.toItemStackSafe(casing.top);
            ingredients[3] = BlockStack.toItemStackSafe(casing.walls);
            ingredients[5] = BlockStack.toItemStackSafe(casing.front);
            ingredients[7] = BlockStack.toItemStackSafe(casing.bottom);

            ingredients[4] = new ItemStack(ModItems.displayCase);
            CachedShapedRecipe recipe = new CachedShapedRecipe(3, 3, ingredients, result);
            recipe.computeVisuals();
            arecipes.add(recipe);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        if (ingredient.getItem() == ModItems.displayCase)
        {
            Object[] ingredients = new Object[9];
            ItemStack result = new ItemStack(ModItems.displayCase);
            DisplayCase casing = new DisplayCase();

            ingredients[0] = BlockStack.toItemStackSafe(casing.corners);
            ingredients[1] = BlockStack.toItemStackSafe(casing.top);
            ingredients[3] = BlockStack.toItemStackSafe(casing.walls);
            ingredients[5] = BlockStack.toItemStackSafe(casing.front);
            ingredients[7] = BlockStack.toItemStackSafe(casing.bottom);
            ingredients[4] = ingredient;
            ItemDisplayCase.setCasing(result, casing);

            CachedShapedRecipe recipe = new CachedShapedRecipe(3, 3, ingredients, result);
            recipe.computeVisuals();
            arecipes.add(recipe);
        }
    }
}
