package com.fiskmods.heroes.common.enchantment;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class EnchantmentBottomless extends Enchantment
{
    public EnchantmentBottomless(int id, int weight)
    {
        super(id, weight, EnumEnchantmentType.weapon);
        setName(FiskHeroes.MODID + ".infinity");
    }

    @Override
    public int getMinEnchantability(int level)
    {
        return 7 * level;
    }

    @Override
    public int getMaxEnchantability(int level)
    {
        return super.getMinEnchantability(level) + 50;
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    @Override
    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() == ModItems.quiver;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment)
    {
        return super.canApplyTogether(enchantment) && enchantment.effectId != SHEnchantments.capacity.effectId;
    }
}
