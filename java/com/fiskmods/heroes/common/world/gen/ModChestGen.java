package com.fiskmods.heroes.common.world.gen;

import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class ModChestGen
{
    public static void register()
    {
        ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(ModItems.gameboiiCartridge, 0, 1, 1, 2));
        ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(ModItems.gameboiiCartridge, 0, 1, 1, 1));

        ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(ModItems.tutridiumGem, 0, 1, 4, 2));
    }
}
