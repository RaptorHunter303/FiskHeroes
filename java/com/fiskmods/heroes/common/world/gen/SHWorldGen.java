package com.fiskmods.heroes.common.world.gen;

import java.util.Map;
import java.util.Random;

import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.common.BlockStack;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.data.world.SHMapData;
import com.fiskmods.heroes.common.world.ModDimensions;
import com.fiskmods.heroes.common.world.gen.feature.WorldGenParticle;
import com.fiskmods.heroes.common.world.gen.feature.WorldGenTutridium;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public enum SHWorldGen implements IWorldGenerator
{
    INSTANCE;

    private World world;
    private Random rand;
    private int xCoord;
    private int zCoord;

    private boolean gatherOreList;

    public WorldGenerator tutridiumGen;
    public WorldGenerator vibraniumGen;
    public WorldGenerator eterniumGen;
    public WorldGenerator particleGen;

    SHWorldGen()
    {
        tutridiumGen = new WorldGenTutridium(ModBlocks.tutridiumOre);
        vibraniumGen = new WorldGenMinable(ModBlocks.vibraniumOre, 6);
        eterniumGen = new WorldGenMinable(ModBlocks.eterniumOre, 4);
        particleGen = new WorldGenParticle();
    }

    public static void register()
    {
        GameRegistry.registerWorldGenerator(INSTANCE, 0);
        MinecraftForge.ORE_GEN_BUS.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        if (world.provider.dimensionId == ModDimensions.QUANTUM_REALM_ID)
        {
            if (rand.nextInt(48) == 0)
            {
                int x = chunkX * 16 + rand.nextInt(16);
                int y = rand.nextInt(world.getHeight() >> 4) * 16 + rand.nextInt(16);
                int z = chunkZ * 16 + rand.nextInt(16);

                particleGen.generate(world, rand, x, y, z);
            }
        }
        else
        {
            WorldInfo info = world.getWorldInfo();

            if (world.provider.isSurfaceWorld() && info.getTerrainType() != WorldType.FLAT && info.isMapFeaturesEnabled())
            {
                if (rand.nextInt(512) == 0)
                {
                    int x = chunkX * 16 + rand.nextInt(16);
                    int z = chunkZ * 16 + rand.nextInt(16);
                    int y = world.getTopSolidOrLiquidBlock(x, z);

                    world.setBlock(x, y, z, ModBlocks.weaponCrate);
                }
            }
        }
    }

    @SubscribeEvent
    public void onOreGenPre(OreGenEvent.Pre event)
    {
        world = event.world;
        rand = event.rand;
        xCoord = event.worldX;
        zCoord = event.worldZ;

        ChunkCoordinates coords = world.getSpawnPoint();
        gatherOreList = xCoord >> 4 == coords.posX >> 4 && zCoord >> 4 == coords.posZ >> 4 || SHMapData.get(world).ores.isEmpty();
    }

    @SubscribeEvent
    public void onOreGenPost(OreGenEvent.Post event)
    {
        genStandardOre(6, tutridiumGen, 32);
        genStandardOre(3, vibraniumGen, 16);
        genStandardOre(3, eterniumGen, 5);
    }

//    @SubscribeEvent
//    public void onPopulateChunkPost(PopulateChunkEvent.Post event)
//    {
//        boolean doGen = TerrainGen.populate(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkZ, event.hasVillageGenerated, PopulateChunkEvent.Populate.EventType.CUSTOM);
//
//        if (doGen && event.rand.nextInt(256) == 0)
//        {
//            int x1 = event.chunkX * 16 + event.rand.nextInt(16) + 8;
//            int y = event.rand.nextInt(32);
//            int z1 = event.chunkZ * 16 + event.rand.nextInt(16) + 8;
//            nexusGen.generate(event.world, event.rand, x1, y, z1);
//        }
//    }

    @SubscribeEvent
    public void onGenerateOre(OreGenEvent.GenerateMinable event)
    {
        if (event.type == EventType.DIRT || event.type == EventType.GRAVEL)
        {
            return;
        }

        if (gatherOreList && event.generator instanceof WorldGenMinable)
        {
            WorldGenMinable generator = (WorldGenMinable) event.generator;
            BlockStack stack = new BlockStack(SHReflection.genMineableOreField.get(generator), SHReflection.genMineableMetaField.get(generator));

            if (stack.block == ModBlocks.eterniumOre)
            {
                return;
            }

            Map<BlockStack, Integer> size = SHMapData.get(world).getOreGen(SHReflection.genMineableStoneField.get(generator));

            if (!size.containsKey(stack))
            {
                size.put(stack, SHReflection.genMineableNumField.get(generator));
            }
        }
    }

    protected void genStandardOre(int veins, WorldGenerator generator, int maxHeight)
    {
        if (TerrainGen.generateOre(world, rand, generator, xCoord, zCoord, GenerateMinable.EventType.CUSTOM))
        {
            for (int i = 0; i < veins; ++i)
            {
                int x = xCoord + rand.nextInt(16);
                int y = rand.nextInt(maxHeight);
                int z = zCoord + rand.nextInt(16);

                generator.generate(world, rand, x, y, z);
            }
        }
    }
}
