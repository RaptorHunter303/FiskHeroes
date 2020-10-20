package com.fiskmods.heroes.common.block;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNexusBrickSlab extends BlockSlab
{
    public BlockNexusBrickSlab(boolean flag)
    {
        super(flag, Material.rock);
        useNeighborBrightness = true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        if (field_150004_a && (metadata & 8) != 0)
        {
            side = 1;
        }

        return ModBlocks.nexusBricks.getBlockTextureFromSide(side);
    }

    @Override
    public Item getItemDropped(int metadata, Random rand, int fortune)
    {
        return Item.getItemFromBlock(ModBlocks.nexusBrickSlab);
    }

    @Override
    protected ItemStack createStackedBlock(int metadata)
    {
        return new ItemStack(Item.getItemFromBlock(ModBlocks.nexusBrickSlab), 2, metadata & 7);
    }

    @Override
    public String func_150002_b(int metadata)
    {
        return super.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        if (item != Item.getItemFromBlock(ModBlocks.nexusBrickDoubleSlab))
        {
            list.add(new ItemStack(item));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z)
    {
        return Item.getItemFromBlock(ModBlocks.nexusBrickSlab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
        if (field_150004_a)
        {
            return super.shouldSideBeRendered(world, x, y, z, side);
        }
        else if (side != 1 && side != 0 && !super.shouldSideBeRendered(world, x, y, z, side))
        {
            return false;
        }
        else
        {
            int x1 = x + Facing.offsetsXForSide[Facing.oppositeSide[side]];
            int y1 = y + Facing.offsetsYForSide[Facing.oppositeSide[side]];
            int z1 = z + Facing.offsetsZForSide[Facing.oppositeSide[side]];
            boolean flag = (world.getBlockMetadata(x1, y1, z1) & 8) != 0;
            return flag ? side == 0 ? true : side == 1 && super.shouldSideBeRendered(world, x, y, z, side) ? true : !func_150003_a(world.getBlock(x, y, z)) || (world.getBlockMetadata(x, y, z) & 8) == 0 : side == 1 ? true : side == 0 && super.shouldSideBeRendered(world, x, y, z, side) ? true : !func_150003_a(world.getBlock(x, y, z)) || (world.getBlockMetadata(x, y, z) & 8) != 0;
        }
    }

    @SideOnly(Side.CLIENT)
    private static boolean func_150003_a(Block block)
    {
        return block == ModBlocks.nexusBrickSlab;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
    }
}
