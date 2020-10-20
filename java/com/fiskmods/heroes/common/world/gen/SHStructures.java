package com.fiskmods.heroes.common.world.gen;

import java.util.Random;

import com.fiskmods.heroes.common.world.structure.StructureGenerator;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;

public enum SHStructures implements IWorldGenerator
{
    INSTANCE;

    public static void register()
    {
        GameRegistry.registerWorldGenerator(INSTANCE, 0);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        WorldInfo info = world.getWorldInfo();

        if (world.provider.isSurfaceWorld() && info.getTerrainType() != WorldType.FLAT && info.isMapFeaturesEnabled())
        {
            StructureGenerator.generateStructures(world, random, chunkX, chunkZ);
        }
    }
}
