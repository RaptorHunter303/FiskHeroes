package com.fiskmods.heroes.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

public class BlockModStairs extends BlockStairs
{
    public BlockModStairs(Block block, int metadata)
    {
        super(block, metadata);
        useNeighborBrightness = true;
    }
}
