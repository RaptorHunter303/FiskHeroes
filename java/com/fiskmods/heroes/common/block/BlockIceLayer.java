package com.fiskmods.heroes.common.block;

import java.util.Random;

import com.fiskmods.heroes.common.tileentity.TileEntityIceLayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIceLayer extends Block implements ITileEntityProvider
{
    public BlockIceLayer()
    {
        super(Material.packedIce);
        slipperiness = 0.98F;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return 0;
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        setBlockBounds(0, 0, 0, 1, 0.0625F, 1);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        float f = 0.0625F;
        int i = world.getBlockMetadata(x, y, z);
        TileEntityIceLayer tile = (TileEntityIceLayer) world.getTileEntity(x, y, z);

        if (tile != null)
        {
            float f1 = (float) tile.thickness / 4;

            if (i == 0)
            {
                setBlockBounds(0, f * (16 - f1), 0, 1, 1, 1);
            }
            else if (i == 1)
            {
                setBlockBounds(0, 0, 0, 1, f1 * f, 1);
            }
            else if (i == 2)
            {
                setBlockBounds(0, 0, f * (16 - f1), 1, 1, 1);
            }
            else if (i == 3)
            {
                setBlockBounds(0, 0, 0, 1, 1, f1 * f);
            }
            else if (i == 4)
            {
                setBlockBounds(f * (16 - f1), 0, 0, 1, 1, 1);
            }
            else if (i == 5)
            {
                setBlockBounds(0, 0, 0, f1 * f, 1, 1);
            }
        }

        super.setBlockBoundsBasedOnState(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
        return side == 1 || super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    public int quantityDropped(Random rand)
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        blockIcon = iconRegister.registerIcon("ice_packed");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityIceLayer();
    }
}
