package com.fiskmods.heroes.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockParticleShell extends Block
{
    public BlockParticleShell()
    {
        super(Material.rock);
    }

    @Override
    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z)
    {
        return 0xF000F0;
    }
}
