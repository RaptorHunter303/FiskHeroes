package com.fiskmods.heroes.common.world.gen.particle;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ParticleShell
{
    protected Block outerShell;
    protected Block innerShell;

    public ParticleShell(Block outer, Block inner)
    {
        outerShell = outer;
        innerShell = inner;
    }

    public ParticleShell(Block shell)
    {
        this(shell, shell);
    }

    public Block getShell(World world, Random rand, int size, double layer)
    {
        if (layer >= size - 1)
        {
            return outerShell;
        }
        else if (layer < size / 4)
        {
            return innerShell;
        }

        return null;
    }
}
