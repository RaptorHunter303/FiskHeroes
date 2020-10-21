package com.fiskmods.heroes;

import com.fiskmods.heroes.common.item.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CreativeTabArchery extends ItemGroup
{
    public CreativeTabArchery()
    {
        super(FiskHeroes.MOD_ID + ".archery");
    }




    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.compoundBow);
    }
}
