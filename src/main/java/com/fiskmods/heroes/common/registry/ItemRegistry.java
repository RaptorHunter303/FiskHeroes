package com.fiskmods.heroes.common.registry;

import com.fiskmods.heroes.FiskHeroes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ItemRegistry
{
    public static Item registerItem(String unlocalizedName, Item item)
    {
        item.setCreativeTab(FiskHeroes.TAB_ITEMS);
        return registerItemNoTab(unlocalizedName, item);
    }

    public static Item registerItemNoTab(String unlocalizedName, Item item)
    {
        item.setUnlocalizedName(unlocalizedName);
        item.setTextureName(FiskHeroes.MODID + ":" + unlocalizedName);

        GameRegistry.registerItem(item, unlocalizedName);

        return item;
    }
}
