package com.fiskmods.heroes.common.world.gen.particle;

import java.util.Map;
import java.util.Random;

import com.fiskmods.heroes.common.BlockStack;
import com.fiskmods.heroes.common.data.world.SHMapData;
import com.fiskmods.heroes.util.FiskMath;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ParticleShellOres extends ParticleShell
{
    public ParticleShellOres(Block outer, Block inner)
    {
        super(outer, inner);
    }

    public ParticleShellOres(Block shell)
    {
        super(shell);
    }

    @Override
    public Block getShell(World world, Random rand, int size, double layer)
    {
        Map<BlockStack, Integer> ores = SHMapData.get(world).getOreGen(outerShell);

        if (layer >= size - 1)
        {
            if (!ores.isEmpty() && rand.nextInt(15) == 0)
            {
                Block block = FiskMath.getWeighted(rand, ores).block;

                if (block == null || block == Blocks.air)
                {
                    block = outerShell;
                }

                return block;
            }

            return outerShell;
        }
        else if (layer < size / 4)
        {
            BlockStack stack = FiskMath.getWeighted(rand, ores);

            if (stack == null || stack.block == Blocks.air)
            {
                return innerShell;
            }

            return stack.block;
        }

        return null;
    }
}
