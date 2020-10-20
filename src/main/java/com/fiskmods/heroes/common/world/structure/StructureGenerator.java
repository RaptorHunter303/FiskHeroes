package com.fiskmods.heroes.common.world.structure;

import java.util.Random;

import net.minecraft.world.World;

public class StructureGenerator
{
    public static void generateStructures(World world, Random random, int chunkX, int chunkZ)
    {
        int x = (chunkX << 4) + 8;
        int z = (chunkZ << 4) + 8;

        for (StructureType type : StructureType.values())
        {
            if (canStructureGenerateAtCoords(world, x, z, type))
            {
                try
                {
                    generateStructure(world, x, z, type);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void generateStructure(World world, int x, int z, StructureType type) throws Exception
    {
        Random rand = getRandomForCoords(world, x, z);
        Structure structure = type.construct(world);
        int y = structure.getYGen(rand, x, z);

        structure.setCenter(x, y, z).generate(rand);
    }

    public static boolean canStructureGenerateAtCoords(World world, int x, int z, StructureType type)
    {
        int xOriginal = x;
        int zOriginal = z;

        if (x < 0)
        {
            x -= type.maxDistance - 1;
        }

        if (z < 0)
        {
            z -= type.maxDistance - 1;
        }

        int x2 = x / type.maxDistance;
        int z2 = z / type.maxDistance;

//      Random random = world.setRandomSeed(x2, z2, 235785655);
        Random random = getRandomForCoords(world, x2, z2);

        x2 *= type.maxDistance;
        z2 *= type.maxDistance;
        x2 += random.nextInt(type.maxDistance - type.minDistance); // Add between 0 and diff between min/max dist
        z2 += random.nextInt(type.maxDistance - type.minDistance);

        if (xOriginal == x2 && zOriginal == z2)
        {
//            return structure.biomePredicate.apply(world.getWorldChunkManager().getBiomeGenAt(xOriginal, zOriginal));
            return true;
        }

        return false;
    }

    public static Random getRandomForCoords(World world, int x, int z)
    {
        return new Random(x * 341873128712L + z * 132897987541L + world.getSeed() + 235785655);
    }
}
