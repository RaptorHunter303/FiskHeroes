package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.util.FabricatorHelper.IMaterialEnergy;

import net.minecraft.item.Item;

public class ItemMaterialEnergy extends Item implements IMaterialEnergy
{
    private final int value;

    public ItemMaterialEnergy(int energy)
    {
        value = energy;
    }

    @Override
    public int getEnergyValue()
    {
        return value;
    }
}
