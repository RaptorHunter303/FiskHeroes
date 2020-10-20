package com.fiskmods.heroes.common.block;

import java.util.Random;

import com.fiskmods.heroes.util.FabricatorHelper;
import com.fiskmods.heroes.util.FabricatorHelper.IMaterialEnergy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockOreSH extends Block implements IMaterialEnergy
{
    private Random rand = new Random();

    private Item itemToDrop;
    private boolean canSilkHarvest = true;
    private boolean canFortuneHarvest = true;
    private int xpDropMin;
    private int xpDropMax;

    private final Item itemEquivalent;

    public BlockOreSH(Item item)
    {
        super(Material.rock);
        itemEquivalent = item;
    }

    public BlockOreSH setHarvestLvl(String toolClass, int level)
    {
        setHarvestLevel(toolClass, level);
        return this;
    }

    public BlockOreSH setItemDrop(Item item)
    {
        itemToDrop = item;
        return this;
    }

    public BlockOreSH setCanSilkHarvest(boolean flag)
    {
        canSilkHarvest = flag;
        return this;
    }

    public BlockOreSH setCanFortuneHarvest(boolean flag)
    {
        canFortuneHarvest = flag;
        return this;
    }

    public BlockOreSH setXpDrop(int min, int max)
    {
        xpDropMin = min;
        xpDropMax = max;
        return this;
    }

    @Override
    public int getEnergyValue()
    {
        return FabricatorHelper.getEnergy(itemEquivalent, false) / 2;
    }

    @Override
    protected boolean canSilkHarvest()
    {
        return canSilkHarvest;
    }

    @Override
    public Item getItemDropped(int metadata, Random rand, int fortune)
    {
        return itemToDrop != null ? itemToDrop : Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random rand)
    {
        if (fortune > 0 && canFortuneHarvest && getItemDropped(0, rand, fortune) != Item.getItemFromBlock(this))
        {
            int i = rand.nextInt(fortune + 2) - 1;

            if (i < 0)
            {
                i = 0;
            }

            return quantityDropped(rand) * (i + 1);
        }
        else
        {
            return quantityDropped(rand);
        }
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune)
    {
        return xpDropMax > 0 ? MathHelper.getRandomIntegerInRange(rand, xpDropMin, xpDropMax) : 0;
    }
}
