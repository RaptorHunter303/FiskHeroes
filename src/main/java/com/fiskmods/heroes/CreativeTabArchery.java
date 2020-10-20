package com.fiskmods.heroes;

import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabArchery extends CreativeTabs
{
    public CreativeTabArchery()
    {
        super(FiskHeroes.MODID + ".archery");
    }

    @Override
    public Item getTabIconItem()
    {
        return ModItems.compoundBow;
    }
}
