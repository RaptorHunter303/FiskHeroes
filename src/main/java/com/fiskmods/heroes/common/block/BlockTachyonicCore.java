package com.fiskmods.heroes.common.block;

import com.fiskmods.heroes.common.tileentity.TileEntityParticleCore;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTachyonicCore extends Block implements ITileEntityProvider
{
    public BlockTachyonicCore()
    {
        super(Material.rock);
        setHarvestLevel("pickaxe", 3);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityParticleCore();
    }
}
