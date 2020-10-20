package com.fiskmods.heroes.common.enchantment;

import com.fiskmods.heroes.common.config.SHConfig;

import net.minecraft.enchantment.Enchantment;

public class SHEnchantments
{
    public static Enchantment electroMagnet;
    public static Enchantment capacity;
    public static Enchantment infinity;

    public static void register()
    {
        electroMagnet = new EnchantmentElectroMagnet(SHConfig.electroMagneticId, 1);
        capacity = new EnchantmentHolding(SHConfig.infinityId, 5);
        infinity = new EnchantmentBottomless(SHConfig.capacityId, 2);
    }
}
