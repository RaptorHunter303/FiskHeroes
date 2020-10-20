package com.fiskmods.heroes.nei;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.item.ModItems;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class NEISuperheroesConfig implements IConfigureNEI
{
    @Override
    public void loadConfig()
    {
        registerHandler(new RecipeHandlerSuitFabricator());
        registerHandler(new RecipeHandlerCosmicFabricator());
        registerHandler(new RecipeHandlerVialArrow());
        registerHandler(new RecipeHandlerDisplayCase());
        registerHandler(new RecipeHandlerBattery());

        API.hideItem(new ItemStack(ModBlocks.displayStandTop, 1, OreDictionary.WILDCARD_VALUE));
        API.hideItem(new ItemStack(ModItems.exclusivityToken, 1, OreDictionary.WILDCARD_VALUE));
    }

    public static void registerHandler(Object obj)
    {
        if (obj instanceof ICraftingHandler)
        {
            API.registerRecipeHandler((ICraftingHandler) obj);
        }

        if (obj instanceof IUsageHandler)
        {
            API.registerUsageHandler((IUsageHandler) obj);
        }
    }

    @Override
    public String getName()
    {
        return "Fisk's Superheroes NEI Plugin";
    }

    @Override
    public String getVersion()
    {
        return FiskHeroes.VERSION;
    }
}
