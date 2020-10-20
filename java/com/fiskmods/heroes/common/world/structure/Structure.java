package com.fiskmods.heroes.common.world.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public abstract class Structure
{
    protected final World worldObj;
    protected int xCoord;
    protected int yCoord;
    protected int zCoord;

    protected boolean simulate = false;
    protected boolean mirrorX;
    protected boolean mirrorZ;
    protected int maxY;

    protected final List<StructurePoint> coverage = new ArrayList<>();

    public Structure(World world)
    {
        worldObj = world;
    }

    public int getYGen(Random rand, int x, int z)
    {
        return Math.max(worldObj.getTopSolidOrLiquidBlock(x, z), worldObj.provider.getAverageGroundLevel());
    }

    public abstract void generate(Random rand);

    public Structure mirror(boolean x, boolean z)
    {
        mirrorX = x;
        mirrorZ = z;
        return this;
    }

    public Structure setCenter(int x, int y, int z)
    {
        xCoord = x;
        yCoord = y;
        zCoord = z;
        return this;
    }

    public void setBlock(int x, int y, int z, Block block, int metadata)
    {
        if (mirrorX && mirrorZ)
        {
            if (x != 0 || z != 0)
            {
                set(xCoord + z, yCoord + y, zCoord - x, block, StructureHelper.mirrorX(block, StructureHelper.rotate(block, metadata)));
                set(xCoord - z, yCoord + y, zCoord + x, block, StructureHelper.mirrorZ(block, StructureHelper.rotate(block, metadata)));
                set(xCoord - x, yCoord + y, zCoord - z, block, StructureHelper.mirrorXZ(block, metadata));
            }
        }
        else
        {
            if (mirrorX && x != 0)
            {
                set(xCoord - x, yCoord + y, zCoord + z, block, StructureHelper.mirrorX(block, metadata));
            }

            if (mirrorZ && z != 0)
            {
                set(xCoord + x, yCoord + y, zCoord - z, block, StructureHelper.mirrorZ(block, metadata));
            }
        }

        set(xCoord + x, yCoord + y, zCoord + z, block, metadata);
    }

    private void set(int x, int y, int z, Block block, int metadata)
    {
        Block block1 = null;

        if (simulate || ((block1 = worldObj.getBlock(x, y, z)) != block || worldObj.getBlockMetadata(x, y, z) != metadata) && block1.getBlockHardness(worldObj, x, y, z) != -1)
        {
            if (simulate)
            {
                maxY = Math.max(maxY, y);
                StructurePoint p = new StructurePoint(x, y, z);

                if (coverage.contains(p))
                {
                    coverage.stream().filter(p::equals).forEach(p1 -> p1.posY = Math.min(p1.posY, y));
                }
                else
                {
                    coverage.add(p);
                }
            }
            else
            {
                placeBlock(x, y, z, block, metadata, 2);
            }
        }
    }

    public void placeBlock(int x, int y, int z, Block block, int metadata, int flags)
    {
        worldObj.setBlock(x, y, z, block, metadata, flags);
    }

    protected boolean generateStructureChestContents(Random random, int x, int y, int z, WeightedRandomChestContent[] chestContent, int itemsToGenerate)
    {
        int i = xCoord + x;
        int j = yCoord + y;
        int k = zCoord + z;

        if (worldObj.getBlock(i, j, k) != Blocks.chest)
        {
            worldObj.setBlock(i, j, k, Blocks.chest, 0, 2);
            TileEntityChest tile = (TileEntityChest) worldObj.getTileEntity(i, j, k);

            if (tile != null)
            {
                WeightedRandomChestContent.generateChestContents(random, chestContent, tile, itemsToGenerate);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    protected boolean fillStructureInventory(Block block, Random random, int x, int y, int z, WeightedRandomChestContent[] chestContent, int itemsToGenerate)
    {
        int i = xCoord + x;
        int j = yCoord + y;
        int k = zCoord + z;

        if (worldObj.getBlock(i, j, k) == block)
        {
            IInventory inventory = (IInventory) worldObj.getTileEntity(i, j, k);

            if (inventory != null)
            {
                WeightedRandomChestContent.generateChestContents(random, chestContent, inventory, itemsToGenerate);
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
