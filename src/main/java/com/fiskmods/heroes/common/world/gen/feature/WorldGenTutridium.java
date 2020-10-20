package com.fiskmods.heroes.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

public class WorldGenTutridium extends WorldGenMinable
{
    private Block ore;

    public WorldGenTutridium(Block block)
    {
        super(block, 1);
        ore = block;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z)
    {
        if (world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, Blocks.stone))
        {
            world.setBlock(x, y, z, ore, 0, 2);
        }

        return true;
    }
}
