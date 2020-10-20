package com.fiskmods.heroes.nei;

import static codechicken.lib.gui.GuiDraw.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.gui.GuiSuitFabricator;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.ItemHeroArmor;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.FabricatorHelper;
import com.fiskmods.heroes.util.SHFormatHelper;
import com.fiskmods.heroes.util.SHHelper;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class RecipeHandlerSuitFabricator extends TemplateRecipeHandler
{
    public class CachedSuitFabricatorRecipe extends CachedRecipe
    {
        public ArrayList<PositionedStack> ingredients;
        public PositionedStack result;

        public CachedSuitFabricatorRecipe(ItemStack out, Object[] items)
        {
            result = new PositionedStack(out, 140, 16);
            ingredients = new ArrayList<>();
            setIngredients(items);
        }

        public void setIngredients(Object[] items)
        {
            for (int i = 0; i < 6; ++i)
            {
                addSlotToContainer(i, 15 + 18 * i, 35, items);
            }
        }

        private void addSlotToContainer(int id, int x, int y, Object[] items)
        {
            if (items[id] != null)
            {
                PositionedStack stack = new PositionedStack(items[id], x - 5, y - 11, false);
                ingredients.add(stack);
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
            result.setPermutationToRender(cycleticks / 20 % result.items.length);
            return result;
        }

        public void computeVisuals()
        {
            for (PositionedStack p : ingredients)
            {
                p.generatePermutations();
            }
        }
    }

    public boolean isCosmic()
    {
        return false;
    }

    @Override
    public void loadTransferRects()
    {
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass()
    {
        return GuiSuitFabricator.class;
    }

    @Override
    public String getRecipeName()
    {
        return StatCollector.translateToLocal(isCosmic() ? "recipe.cosmic_suit_fabricator" : "recipe.suit_fabricator");
    }

    @Override
    public int recipiesPerPage()
    {
        return 2;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        try
        {
            Hero hero = SHHelper.getHeroFromArmor(result);

            if (hero != null && hero.isCosmic() == isCosmic())
            {
                Object[] items = new Object[6];
                ItemStack itemstack = new ItemStack(isCosmic() ? ModItems.eterniumShard : ModItems.tutridiumGem);
                int cost = FabricatorHelper.getMaterialCost(hero);

                while (FabricatorHelper.getEnergy(itemstack, isCosmic()) < cost)
                {
                    ++itemstack.stackSize;
                }

                for (int i = 0; i < 6; ++i)
                {
                    if (itemstack.stackSize <= 0)
                    {
                        break;
                    }

                    items[i] = new ItemStack(itemstack.getItem(), Math.min(itemstack.stackSize, 64), itemstack.getItemDamage());
                    itemstack.stackSize -= 64;
                }

                CachedSuitFabricatorRecipe recipe = new CachedSuitFabricatorRecipe(ItemHeroArmor.create(result.getItem(), hero, false), items);
                recipe.computeVisuals();
                arecipes.add(recipe);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        try
        {
            ingredient = ingredient.copy();
            ingredient.stackSize = 1;

            if (FabricatorHelper.getEnergy(ingredient, isCosmic()) > 1)
            {
                for (Hero hero : Hero.REGISTRY.getValues().stream().filter(t -> !t.isHidden() && t.isCosmic() == isCosmic()).collect(Collectors.toList()))
                {
                    List<ItemStack> armors = new ArrayList<>();

                    for (int i = 0; i < 4; ++i)
                    {
                        ItemStack stack = hero.getDefault().createArmor(i);

                        if (stack != null)
                        {
                            armors.add(stack);
                        }
                    }

                    if (!armors.isEmpty())
                    {
                        ItemStack[] items = new ItemStack[6];
                        ItemStack itemstack = new ItemStack(ingredient.getItem(), 1, ingredient.getItemDamage());
                        int cost = FabricatorHelper.getMaterialCost(hero);

                        while (FabricatorHelper.getEnergy(itemstack, isCosmic()) < cost)
                        {
                            ++itemstack.stackSize;
                        }

                        for (int i = 0; i < 6; ++i)
                        {
                            if (itemstack.stackSize <= 0)
                            {
                                break;
                            }

                            items[i] = itemstack.copy();
                            items[i].stackSize = Math.min(itemstack.stackSize, 64);
                            itemstack.stackSize -= 64;
                        }

                        CachedSuitFabricatorRecipe recipe = new CachedSuitFabricatorRecipe(armors.get(0), items);
                        recipe.result.items = armors.toArray(new ItemStack[0]);
                        recipe.computeVisuals();
                        arecipes.add(recipe);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getGuiTexture()
    {
        return FiskHeroes.MODID + ":textures/gui/container/suit_fabricator_nei.png";
    }

    @Override
    public String getOverlayIdentifier()
    {
        return isCosmic() ? "cosmic_suit_fabricator" : "suit_fabricator";
    }

    @Override
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(0, 0, 5, 11, 166, 41);

        CachedSuitFabricatorRecipe recipe1 = (CachedSuitFabricatorRecipe) arecipes.get(recipe);
        ItemStack itemstack = recipe1.result.item;

        if (itemstack != null)
        {
            boolean flag = GuiDraw.fontRenderer.getUnicodeFlag();
            GuiDraw.fontRenderer.setUnicodeFlag(true);
            String s = SHFormatHelper.formatHero(SHHelper.getHeroIterFromArmor(itemstack));
            String s1 = GuiDraw.fontRenderer.trimStringToWidth(s, 104 - GuiDraw.fontRenderer.getStringWidth("..."));

            if (GuiDraw.fontRenderer.getStringWidth(s) > GuiDraw.fontRenderer.getStringWidth(s1))
            {
                s1 += "...";
            }

            GuiDraw.drawString(s1, 13, 8, -1);
            GuiDraw.fontRenderer.setUnicodeFlag(flag);
        }
    }
}
