package com.fiskmods.heroes.common.block;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.tileentity.TileEntitySuitFabricator;
import com.fiskmods.heroes.util.FiskServerUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockSuitFabricator extends BlockContainer
{
    private IIcon iconTop;
    private IIcon iconBottom;

    public BlockSuitFabricator()
    {
        super(Material.iron);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote && player.canPlayerEdit(x, y, z, side, player.getHeldItem()))
        {
            player.openGui(FiskHeroes.MODID, 3, world, x, y, z);
        }

        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata)
    {
        FiskServerUtils.dropItems(world, x, y, z);
        world.func_147453_f(x, y, z, block);

        super.breakBlock(world, x, y, z, block, metadata);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntitySuitFabricator();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return side == 1 ? iconTop : side == 0 ? iconBottom : blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        blockIcon = iconRegister.registerIcon(getTextureName() + "_side");
        iconTop = iconRegister.registerIcon(getTextureName() + "_top");
        iconBottom = iconRegister.registerIcon(getTextureName() + "_bottom");
    }
}
