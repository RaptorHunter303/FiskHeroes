package com.fiskmods.heroes.common.world.structure;

import java.util.Random;

import com.fiskmods.heroes.common.block.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNexus extends Structure
{
    private Random random;

    public StructureNexus(World world)
    {
        super(world);
    }

    @Override
    public int getYGen(Random rand, int x, int z)
    {
        return rand.nextInt(32);
    }

    @Override
    public void generate(Random rand)
    {
        int height = 7 + rand.nextInt(2);
        int xR = 7 + rand.nextInt(3);
        int zR = 7 + rand.nextInt(3);
        int minX = -xR - 1;
        int minZ = -zR - 1;
        int maxX = xR + 1;
        int maxZ = zR + 1;
        int wall = 5;

        random = rand;

        for (int x = minX - wall; x <= maxX + wall; ++x)
        {
            for (int y = -1 - wall; y <= height + wall; ++y)
            {
                for (int z1 = minZ - wall; z1 <= maxZ + wall; ++z1)
                {
                    boolean flag = x < minX || y < -1 || z1 < minZ || x > maxX || y > height || z1 > maxZ;

                    if (x == minX || y == -1 || z1 == minZ || x == maxX || y == height || z1 == maxZ)
                    {
                        if (!flag)
                        {
                            setBlock(x, y, z1, ModBlocks.eterniumStone, 0);
                        }
                    }
                    else if (flag)
                    {
                        int i = Math.max(minX - x, 0);
                        i = Math.max(-1 - y, i);
                        i = Math.max(minZ - z1, i);
                        i = Math.max(x - maxX, i);
                        i = Math.max(y - height, i);
                        i = Math.max(z1 - maxZ, i);

                        if (i <= 0 + rand.nextInt(wall))
                        {
                            setBlock(x, y, z1, ModBlocks.eterniumStone, 0);
                        }
                    }
                    else
                    {
                        setBlock(x, y, z1, Blocks.air, 0);
                    }
                }
            }
        }

        for (int i = 0; i <= height - 7; ++i)
        {
            setBlock(0, 3 + i, 0, ModBlocks.superchargedEternium, 0);
        }

        for (int x = minX + 1; x < maxX; ++x)
        {
            for (int z = minZ + 1; z < maxZ; ++z)
            {
                setBlock(x, 0, z, ModBlocks.nexusSoil, 0);
                setBlock(x, height - 1, z, ModBlocks.nexusBrickSlab, 8);

                if (x == 0 || z == 0)
                {
                    setBlock(x, 1, z, ModBlocks.nexusBricks, 0);
                    setBlock(x, height - 1, z, ModBlocks.nexusBricks, 0);

                    if (x == 0)
                    {
                        mirror(true, false);
                        setBlock(x + 1, 1, z, ModBlocks.nexusBrickStairs, 1);
                    }
                    else
                    {
                        mirror(false, true);
                        setBlock(x, 1, z + 1, ModBlocks.nexusBrickStairs, 3);
                    }

                    mirror(false, false);
                }
            }
        }

        for (int i = 0; i < 5; ++i)
        {
            for (int j = 0; j < 5; ++j)
            {
                setBlock(i - 2, 1, j - 2, ModBlocks.nexusBricks, 0);
                setBlock(i - 2, height - 1, j - 2, ModBlocks.nexusBricks, 0);
            }
        }

        // Start middle platform
        setBlock(0, 2, 0, ModBlocks.nexusBricks, 0);
        mirror(true, true);
        setBlock(1, 2, 0, ModBlocks.nexusBricks, 0);
        setBlock(1, 2, 1, ModBlocks.nexusBrickSlab, 0);
        setBlock(2, 2, 0, ModBlocks.nexusBrickStairs, 1);

        // Start inner ring
        setBlock(4, 1, 4, ModBlocks.nexusBrickSlab, 0);
        setBlock(4, 1, 3, ModBlocks.nexusBrickStairs, 0);
        setBlock(3, 1, 4, ModBlocks.nexusBrickStairs, 2);
        setBlock(6, 1, 3, ModBlocks.nexusBrickStairs, 1);
        setBlock(3, 1, 6, ModBlocks.nexusBrickStairs, 3);

        for (int i = 1; i < height; ++i)
        {
            setBlock(5, i, 3, ModBlocks.nexusBricks, 0);
            setBlock(3, i, 5, ModBlocks.nexusBricks, 0);
        }

        mirror(true, false);

        // Start lava corners
        for (int i = 1; i < height; ++i)
        {
            setBlock(xR, height - i, zR, Blocks.lava, 0);
            setBlock(xR, height - i, -zR, Blocks.lava, 0);
        }

        for (int i = 0; i <= 4; i += 4)
        {
            int y = i > 0 ? height - 1 : 1;
            setBlock(xR - 1, y, zR, ModBlocks.nexusBrickStairs, i);
            setBlock(xR, y, zR - 1, ModBlocks.nexusBrickStairs, i + 2);

            setBlock(xR - 1, y, -zR, ModBlocks.nexusBrickStairs, i);
            setBlock(xR, y, -zR + 1, ModBlocks.nexusBrickStairs, i + 3);
        }
    }

    @Override
    public void placeBlock(int x, int y, int z, Block block, int metadata, int flags)
    {
        float f = 0.15F;

        if (block == ModBlocks.nexusBricks && metadata == 0 && random.nextFloat() < f)
        {
            if (random.nextFloat() < 0.25F)
            {
                block = ModBlocks.nexusBrickSlab;
                metadata = random.nextBoolean() ? 0 : 8;
            }
            else
            {
                block = ModBlocks.nexusBrickStairs;
                metadata = random.nextInt(8);
            }
        }
        else if (block == ModBlocks.nexusBrickStairs && random.nextFloat() < f)
        {
            block = ModBlocks.nexusBrickSlab;
            metadata = (metadata & 4) == 4 ? 8 : 0;
        }

        super.placeBlock(x, y, z, block, metadata, flags);
    }
}
