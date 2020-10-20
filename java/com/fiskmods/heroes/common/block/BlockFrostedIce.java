package com.fiskmods.heroes.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFrostedIce extends BlockIce
{
    private IIcon[] icons = new IIcon[4];

    @Override
    protected boolean canSilkHarvest()
    {
        return false;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        int metadata = world.getBlockMetadata(x, y, z);

        if (rand.nextInt(3) == 0 || getNeighboringIce(world, x, y, z) < 4 && world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 11 - metadata)
        {
            tryMelt(world, x, y, z, rand, true);
        }
        else
        {
            world.scheduleBlockUpdate(x, y, z, this, MathHelper.getRandomIntegerInRange(rand, 20, 40));
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if (block == this)
        {
            int i = getNeighboringIce(world, x, y, z);

            if (i < 2)
            {
                meltIntoWater(world, x, y, z);
            }
        }
    }

    public int getNeighboringIce(World world, int x, int y, int z)
    {
        int i = 0;

        for (ForgeDirection dir : ForgeDirection.values())
        {
            if (world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == this)
            {
                ++i;

                if (i >= 4)
                {
                    return i;
                }
            }
        }

        return i;
    }

    protected void tryMelt(World world, int x, int y, int z, Random rand, boolean cascade)
    {
        int metadata = world.getBlockMetadata(x, y, z);

        if (metadata < 3)
        {
            world.setBlockMetadataWithNotify(x, y, z, metadata + 1, 2);
            world.scheduleBlockUpdate(x, y, z, this, MathHelper.getRandomIntegerInRange(rand, 20, 40));
        }
        else
        {
            meltIntoWater(world, x, y, z);

            if (cascade)
            {
                for (ForgeDirection dir : ForgeDirection.values())
                {
                    if (world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == this)
                    {
                        tryMelt(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, rand, false);
                    }
                }
            }
        }
    }

    protected void meltIntoWater(World world, int x, int y, int z)
    {
        if (world.provider.isHellWorld)
        {
            world.setBlockToAir(x, y, z);
            return;
        }

        dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        world.setBlock(x, y, z, Blocks.water);

        for (ForgeDirection dir : ForgeDirection.values())
        {
            if (world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == ModBlocks.iceLayer && world.getBlockMetadata(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == dir.ordinal())
            {
                world.setBlockToAir(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            }
        }
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        return icons[MathHelper.clamp_int(metadata, 0, icons.length - 1)];
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        for (int i = 0; i < icons.length; ++i)
        {
            icons[i] = iconRegister.registerIcon(getTextureName() + "_" + i);
        }
    }
}
