package com.fiskmods.heroes.common.block;

import java.util.Random;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.tileentity.TileEntityRuleHandler;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRuleHandler extends BlockContainer
{
    public BlockRuleHandler()
    {
        super(Material.iron);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote && player.canPlayerEdit(x, y, z, side, player.getHeldItem()))
        {
            player.openGui(FiskHeroes.MODID, 7, world, x, y, z);
        }

        return true;
    }

    @Override
    public int quantityDropped(Random rand)
    {
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityRuleHandler();
    }
}
