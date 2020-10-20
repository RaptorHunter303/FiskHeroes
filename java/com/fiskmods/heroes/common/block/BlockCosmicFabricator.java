package com.fiskmods.heroes.common.block;

import com.fiskmods.heroes.common.tileentity.TileEntityCosmicFabricator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCosmicFabricator extends BlockSuitFabricator
{
    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityCosmicFabricator();
    }
}
