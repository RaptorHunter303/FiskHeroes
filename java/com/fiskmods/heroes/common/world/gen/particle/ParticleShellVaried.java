package com.fiskmods.heroes.common.world.gen.particle;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ParticleShellVaried extends ParticleShell
{
    private Block[] outerShells;
    private int variation;

    public ParticleShellVaried(Block[] outer, Block inner, int var)
    {
        super(outer[0], inner);
        variation = var;
        outerShells = outer;
    }

    @Override
    public Block getShell(World world, Random rand, int size, double layer)
    {
        if (layer >= size - 1)
        {
            if (rand.nextInt(variation) == 0)
            {
                return outerShells[rand.nextInt(outerShells.length)];
            }

            return outerShell;
        }
        else if (layer < size / 4)
        {
            return innerShell;
        }

        return null;
    }
}
