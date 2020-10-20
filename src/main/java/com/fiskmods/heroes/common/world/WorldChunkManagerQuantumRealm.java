package com.fiskmods.heroes.common.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class WorldChunkManagerQuantumRealm extends WorldChunkManager
{
    private BiomeGenBase biomeGenerator;
    private float rainfall;

    public WorldChunkManagerQuantumRealm(BiomeGenBase biome, float f)
    {
        biomeGenerator = biome;
        rainfall = f;
    }

    @Override
    public BiomeGenBase getBiomeGenAt(int x, int z)
    {
        return biomeGenerator;
    }

    @Override
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int i, int i1, int x, int z)
    {
        if (biomes == null || biomes.length < x * z)
        {
            biomes = new BiomeGenBase[x * z];
        }

        Arrays.fill(biomes, 0, x * z, biomeGenerator);
        return biomes;
    }

    @Override
    public float[] getRainfall(float[] rainfalls, int i, int i1, int x, int z)
    {
        if (rainfalls == null || rainfalls.length < x * z)
        {
            rainfalls = new float[x * z];
        }

        Arrays.fill(rainfalls, 0, x * z, rainfall);
        return rainfalls;
    }

    @Override
    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int i, int i1, int x, int z)
    {
        if (biomes == null || biomes.length < x * z)
        {
            biomes = new BiomeGenBase[x * z];
        }

        Arrays.fill(biomes, 0, x * z, biomeGenerator);
        return biomes;
    }

    @Override
    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomes, int i, int i1, int x, int z, boolean b)
    {
        return loadBlockGeneratorData(biomes, i, i1, x, z);
    }

    @Override
    public ChunkPosition findBiomePosition(int x, int y, int z, List list, Random rand)
    {
        return list.contains(biomeGenerator) ? new ChunkPosition(x - z + rand.nextInt(z * 2 + 1), 0, y - z + rand.nextInt(z * 2 + 1)) : null;
    }

    @Override
    public boolean areBiomesViable(int x, int y, int z, List list)
    {
        return list.contains(biomeGenerator);
    }
}
