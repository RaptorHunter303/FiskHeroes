package com.fiskmods.heroes.common.block;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.ItemHeroArmor;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockSuitIterator extends Block
{
    private IIcon iconTop;
    private IIcon iconBottom;
    private IIcon iconPowered;

    public BlockSuitIterator()
    {
        super(Material.iron);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote && player.canPlayerEdit(x, y, z, side, player.getHeldItem()))
        {
            player.openGui(FiskHeroes.MODID, 6, world, x, y, z);
        }

        return true;
    }

    @Override
    public int tickRate(World world)
    {
        return 1;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
        boolean prevPowered = world.getBlockMetadata(x, y, z) > 0;

        if (powered && !prevPowered)
        {
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);

            if (!world.isRemote)
            {
                TileEntity tileentity = world.getTileEntity(x, y + 1, z);

                if (tileentity instanceof TileEntityDisplayStand)
                {
                    TileEntityDisplayStand tile = (TileEntityDisplayStand) tileentity;
                    HeroIteration iter = SHHelper.getHeroIter(SHHelper.getEquipment(tile, 0));

                    if (iter != null)
                    {
                        iter = iter.hero.getIteration(iter.getId() + 1);

                        for (int i = 0; i < 4; ++i)
                        {
                            ItemStack stack = tile.getStackInSlot(i);

                            if (stack != null)
                            {
                                stack = ItemHeroArmor.set(stack, iter, false).copy();
                                tile.setInventorySlotContents(i, stack);
                            }
                        }

                        world.playAuxSFX(1000, x, y, z, 0);
                        tile.markDirty();
                    }
                }
            }
        }
        else if (!powered && prevPowered)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return side == 1 ? iconTop : side == 0 ? iconBottom : metadata == 0 ? blockIcon : iconPowered;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        blockIcon = iconRegister.registerIcon(getTextureName() + "_side");
        iconPowered = iconRegister.registerIcon(getTextureName() + "_side_powered");
        iconBottom = iconRegister.registerIcon(getTextureName() + "_bottom");
        iconTop = iconRegister.registerIcon(getTextureName() + "_top");
    }
}
