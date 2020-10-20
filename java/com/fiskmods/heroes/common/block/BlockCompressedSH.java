package com.fiskmods.heroes.common.block;

import com.fiskmods.heroes.util.FabricatorHelper;
import com.fiskmods.heroes.util.FabricatorHelper.IMaterialEnergy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

public class BlockCompressedSH extends Block implements IMaterialEnergy
{
    private final Item itemEquivalent;

    public BlockCompressedSH(Item item)
    {
        super(Material.iron);
        itemEquivalent = item;
    }

    public BlockCompressedSH setHarvestLvl(String toolClass, int level)
    {
        setHarvestLevel(toolClass, level);
        return this;
    }

    @Override
    public int getEnergyValue()
    {
        return FabricatorHelper.getEnergy(itemEquivalent, false) * 9;
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return true;
    }
}
