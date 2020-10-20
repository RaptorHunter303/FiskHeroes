package com.fiskmods.heroes.common.world.gen.particle;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ParticleShellObsidian extends ParticleShell
{
    public ParticleShellObsidian(Block outer, Block inner)
    {
        super(outer, inner);
    }

    public ParticleShellObsidian(Block shell)
    {
        super(shell);
    }

    @Override
    public Block getShell(World world, Random rand, int size, double layer)
    {
        if (layer >= size - 1)
        {
            return outerShell;
        }
        else if (layer < size / 4 || rand.nextInt(50) == 0)
        {
            if (layer < size / 4 - 2)
            {
                return rand.nextInt(10) == 0 ? Blocks.glowstone : null;
            }
            else if (layer < size / 4 - 1)
            {
                return Blocks.netherrack;
            }

            return innerShell;
        }

        return null;
    }
}
