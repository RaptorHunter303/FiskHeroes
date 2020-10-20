package com.fiskmods.heroes.common.world.gen.feature;

import java.util.Random;

import com.fiskmods.heroes.common.block.BlockSubatomicCore.CoreType;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.tileentity.TileEntityParticleCore;
import com.fiskmods.heroes.common.world.gen.particle.GenParticle;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenParticle extends WorldGenerator
{
    @Override
    public boolean generate(World world, Random rand, int x, int y, int z)
    {
        try
        {
            GenParticle particle = GenParticle.getRandom(rand);
            int size = MathHelper.getRandomIntegerInRange(rand, particle.minSize, particle.maxSize);

            y = MathHelper.clamp_int(y, size + 2, world.getHeight() - size - 2);
            Vec3 center = Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5);

            for (int i = -size; i <= size; ++i)
            {
                for (int j = -size; j <= size; ++j)
                {
                    for (int k = -size; k <= size; ++k)
                    {
                        double layer = center.distanceTo(center.addVector(i, j, k));

                        if (layer < size)
                        {
                            Block block = particle.shell.getShell(world, rand, size, layer);

                            if (block != null)
                            {
                                world.setBlock(x + i, y + j, z + k, block, 0, 2);
                            }
                        }

                    }
                }
            }

            boolean flag = false;

            if (particle.core == ModBlocks.subatomicParticleCore && particle != GenParticle.SUBATOMIC)
            {
                flag = world.setBlock(x, y, z, particle.core, CoreType.getRandom(rand).ordinal(), 2);
            }
            else
            {
                flag = world.setBlock(x, y, z, particle.core, 0, 2);
            }

            if (flag && world.getTileEntity(x, y, z) instanceof TileEntityParticleCore)
            {
                TileEntityParticleCore tile = (TileEntityParticleCore) world.getTileEntity(x, y, z);
                tile.setGravity(particle.gravity * tile.getType().gravity);
                tile.setRange(size);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }
}
