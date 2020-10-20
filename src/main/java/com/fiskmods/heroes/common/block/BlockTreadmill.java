package com.fiskmods.heroes.common.block;

import java.util.Random;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.tileentity.TileEntityTreadmill;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHTileHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTreadmill extends BlockDirectional implements ITileEntityProvider
{
    public static final int[][] directions = new int[][] {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};

    public BlockTreadmill()
    {
        super(Material.iron);
        setHarvestLevel(null, 0);
        setStepSound(soundTypeMetal);
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta)
    {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
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
        setBlockBounds(0, 0, 0, 1, 0.32F, 1);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        int direction = getDirection(metadata);

        if (isBlockFrontOfTreadmill(metadata))
        {
            if (world.getBlock(x - directions[direction][0], y, z - directions[direction][1]) != this)
            {
                world.setBlockToAir(x, y, z);
            }
        }
        else if (world.getBlock(x + directions[direction][0], y, z + directions[direction][1]) != this)
        {
            world.setBlockToAir(x, y, z);

            if (!world.isRemote)
            {
                dropBlockAsItem(world, x, y, z, metadata, 0);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (world.getTileEntity(x, y, z) instanceof TileEntityTreadmill)
        {
            TileEntityTreadmill tile = SHTileHelper.getTileBase((TileEntityTreadmill) world.getTileEntity(x, y, z));
            Hero hero = SHHelper.getHero(player);

            if (hero != null && hero.hasEnabledModifier(player, Ability.SUPER_SPEED))
            {
                if (tile.playerId == -1)
                {
                    tile.sendToServer(buf ->
                    {
                    });

                    if (world.isRemote)
                    {
                        sendDismountMessageTo(player);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    public void sendDismountMessageTo(EntityPlayer player)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == player)
        {
            mc.ingameGUI.func_110326_a(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(mc.gameSettings.keyBindSneak.getKeyCode())), false);
        }
    }

    @Override
    public Item getItemDropped(int metadata, Random rand, int fortune)
    {
        return isBlockFrontOfTreadmill(metadata) ? Item.getItemById(0) : super.getItemDropped(metadata, rand, fortune);
    }

    public static boolean isBlockFrontOfTreadmill(int metadata)
    {
        return (metadata & 8) != 0;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float dropChance, int fortune)
    {
        if (!isBlockFrontOfTreadmill(metadata))
        {
            super.dropBlockAsItemWithChance(world, x, y, z, metadata, dropChance, 0);
        }
    }

    @Override
    public int getMobilityFlag()
    {
        return 1;
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode && isBlockFrontOfTreadmill(metadata))
        {
            int dir = getDirection(metadata);
            x -= directions[dir][0];
            z -= directions[dir][1];

            if (world.getBlock(x, y, z) == this)
            {
                world.setBlockToAir(x, y, z);
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityTreadmill();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        blockIcon = iconRegister.registerIcon("iron_block");
    }
}
