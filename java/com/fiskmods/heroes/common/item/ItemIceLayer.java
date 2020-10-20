package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.tileentity.TileEntityIceLayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemIceLayer extends ItemBlock
{
    public ItemIceLayer(Block block)
    {
        super(block);
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (itemstack.stackSize == 0)
        {
            return false;
        }
        else if (!player.canPlayerEdit(x, y, z, side, itemstack))
        {
            return false;
        }
        else
        {
            Block block = world.getBlock(x, y, z);

            if (block == ModBlocks.iceLayer)
            {
                TileEntityIceLayer tile = (TileEntityIceLayer) world.getTileEntity(x, y, z);

                if (tile != null)
                {
                    if (tile.thickness < 64 && world.checkNoEntityCollision(field_150939_a.getCollisionBoundingBoxFromPool(world, x, y, z)) && !player.isSneaking())
                    {
                        world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, field_150939_a.stepSound.func_150496_b(), (field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, field_150939_a.stepSound.getPitch() * 0.8F);
                        tile.setThickness(tile.thickness + 4);
                        --itemstack.stackSize;
                        return true;
                    }
                }
            }

            if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z))
            {
                if (side == 0)
                {
                    --y;
                }

                if (side == 1)
                {
                    ++y;
                }

                if (side == 2)
                {
                    --z;
                }

                if (side == 3)
                {
                    ++z;
                }

                if (side == 4)
                {
                    --x;
                }

                if (side == 5)
                {
                    ++x;
                }
            }

            if (itemstack.stackSize == 0)
            {
                return false;
            }
            else if (!player.canPlayerEdit(x, y, z, side, itemstack))
            {
                return false;
            }
            else if (y == 255 && field_150939_a.getMaterial().isSolid())
            {
                return false;
            }
            else if (world.canPlaceEntityOnSide(field_150939_a, x, y, z, false, side, player, itemstack))
            {
                int j1 = field_150939_a.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, side);

                if (placeBlockAt(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ, j1))
                {
                    TileEntityIceLayer tile = (TileEntityIceLayer) world.getTileEntity(x, y, z);

                    if (tile != null)
                    {
                        tile.thickness = 4;
                    }

                    world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, field_150939_a.stepSound.func_150496_b(), (field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, field_150939_a.stepSound.getPitch() * 0.8F);
                    --itemstack.stackSize;
                }

                return true;
            }
            else
            {
                return false;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack itemstack)
    {
        Block block = world.getBlock(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);

        if (block == ModBlocks.iceLayer)
        {
            TileEntityIceLayer tile = (TileEntityIceLayer) world.getTileEntity(x, y, z);

            if (tile != null && tile.thickness < 64)
            {
                return true;
            }
        }

        return super.func_150936_a(world, x, y, z, side, player, itemstack);
    }
}
