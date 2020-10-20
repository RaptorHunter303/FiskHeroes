package com.fiskmods.heroes.common.enchantment;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class EnchantmentElectroMagnet extends Enchantment
{
    public EnchantmentElectroMagnet(int id, int weight)
    {
        super(id, weight, EnumEnchantmentType.weapon);
        setName(FiskHeroes.MODID + ".electroMagnet");
    }

    @Override
    public int getMinEnchantability(int level)
    {
        return 15;
    }

    @Override
    public int getMaxEnchantability(int level)
    {
        return super.getMinEnchantability(level) + 50;
    }

    @Override
    public int getMaxLevel()
    {
        return 1;
    }

    @Override
    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() == ModItems.captainAmericasShield;
    }
}
