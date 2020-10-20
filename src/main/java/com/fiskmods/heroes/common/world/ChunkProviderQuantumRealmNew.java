//package com.fiskmods.heroes.common.world;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import com.fiskmods.heroes.common.block.ModBlocks;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockFalling;
//import net.minecraft.entity.EnumCreatureType;
//import net.minecraft.init.Blocks;
//import net.minecraft.util.IProgressUpdate;
//import net.minecraft.util.MathHelper;
//import net.minecraft.world.ChunkPosition;
//import net.minecraft.world.World;
//import net.minecraft.world.biome.BiomeGenBase;
//import net.minecraft.world.chunk.Chunk;
//import net.minecraft.world.chunk.IChunkProvider;
//import net.minecraft.world.gen.NoiseGenerator;
//import net.minecraft.world.gen.NoiseGeneratorOctaves;
//import net.minecraft.world.gen.NoiseGeneratorPerlin;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.event.terraingen.PopulateChunkEvent;
//import net.minecraftforge.event.terraingen.TerrainGen;
//
//public class ChunkProviderQuantumRealmNew implements IChunkProvider
//{
//    private World worldObj;
//    private Random random;
//
//    private NoiseGeneratorOctaves noiseGen1;
//    private NoiseGeneratorOctaves noiseGen2;
//    private NoiseGeneratorOctaves noiseGen3;
//    private NoiseGeneratorPerlin noiseGen4;
//    public NoiseGeneratorOctaves noiseGen5;
//    public NoiseGeneratorOctaves noiseGen6;
//    public NoiseGeneratorOctaves mobSpawnerNoise;
//
//    private final double[] noiseField;
//    private final float[] parabolicField;
//
//    private BiomeGenBase[] biomesForGeneration;
//    double[] noiseData1;
//    double[] noiseData2;
//    double[] noiseData3;
//    double[] noiseData4;
//
//    public ChunkProviderQuantumRealmNew(World world, long seed)
//    {
//        worldObj = world;
//        random = new Random(seed);
//
//        noiseGen1 = new NoiseGeneratorOctaves(random, 16);
//        noiseGen2 = new NoiseGeneratorOctaves(random, 16);
//        noiseGen3 = new NoiseGeneratorOctaves(random, 8);
//        noiseGen4 = new NoiseGeneratorPerlin(random, 4);
//        noiseGen5 = new NoiseGeneratorOctaves(random, 10);
//        noiseGen6 = new NoiseGeneratorOctaves(random, 16);
//        mobSpawnerNoise = new NoiseGeneratorOctaves(random, 8);
//        noiseField = new double[825];
//        parabolicField = new float[25];
//
//        for (int j = -2; j <= 2; ++j)
//        {
//            for (int k = -2; k <= 2; ++k)
//            {
//                float f = 10.0F / MathHelper.sqrt_float(j * j + k * k + 0.2F);
//                parabolicField[j + 2 + (k + 2) * 5] = f;
//            }
//        }
//
//        NoiseGenerator[] noiseGens = {noiseGen1, noiseGen2, noiseGen3, noiseGen4, noiseGen5, noiseGen6, mobSpawnerNoise};
//        noiseGens = TerrainGen.getModdedNoiseGenerators(world, random, noiseGens);
//        noiseGen1 = (NoiseGeneratorOctaves) noiseGens[0];
//        noiseGen2 = (NoiseGeneratorOctaves) noiseGens[1];
//        noiseGen3 = (NoiseGeneratorOctaves) noiseGens[2];
//        noiseGen4 = (NoiseGeneratorPerlin) noiseGens[3];
//        noiseGen5 = (NoiseGeneratorOctaves) noiseGens[4];
//        noiseGen6 = (NoiseGeneratorOctaves) noiseGens[5];
//        mobSpawnerNoise = (NoiseGeneratorOctaves) noiseGens[6];
//    }
//
//    public void generateBlocks(int chunkX, int chunkZ, Block[] data)
//    {
//        biomesForGeneration = worldObj.getWorldChunkManager().getBiomesForGeneration(biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
//        initializeNoiseField(chunkX * 4, 0, chunkZ * 4);
//
//        short height = 256;
//        byte waterHeight = 63;
//        double d0 = 0.125D * 1;
//        double d9 = 0.25D * 1;
//        double d14 = 0.25D * 1;
//
//        for (int i = 0; i < 4; ++i)
//        {
//            for (int j = 0; j < 4; ++j)
//            {
//                int i1 = ((i + 0) * 5 + j + 0) * 33;
//                int j1 = ((i + 0) * 5 + j + 1) * 33;
//                int k1 = ((i + 1) * 5 + j + 0) * 33;
//                int l1 = ((i + 1) * 5 + j + 1) * 33;
//
//                for (int k = 0; k < 32; ++k)
//                {
//                    double d1 = noiseField[i1 + k + 0];
//                    double d2 = noiseField[j1 + k + 0];
//                    double d3 = noiseField[k1 + k + 0];
//                    double d4 = noiseField[l1 + k + 0];
//                    double d5 = (noiseField[i1 + k + 1] - d1) * d0;
//                    double d6 = (noiseField[j1 + k + 1] - d2) * d0;
//                    double d7 = (noiseField[k1 + k + 1] - d3) * d0;
//                    double d8 = (noiseField[l1 + k + 1] - d4) * d0;
//
//                    for (int l = 0; l < 8; ++l)
//                    {
//                        double d10 = d1;
//                        double d11 = d2;
//                        double d12 = (d3 - d1) * d9;
//                        double d13 = (d4 - d2) * d9;
//
//                        for (int m = 0; m < 4; ++m)
//                        {
//                            int index = m + i * 4 << 12 | 0 + j * 4 << 8 | k * 8 + l;
//                            index -= height;
//
//                            double d16 = (d11 - d10) * d14;
//                            double d15 = d10 - d16;
//
//                            for (int n = 0; n < 4; ++n)
//                            {
//                                d15 += d16;
//                                index += height;
//
//                                if (d15 < -0.7)
//                                {
//                                    data[index] = Blocks.glowstone;
//                                }
//                                else if (d15 < -0.5/* || k * 8 + l == 0*/)
//                                {
//                                    data[index] = ModBlocks.quantumMatter;
//                                }
////                                else if (d15 > 0.5)
////                                {
////                                    data[index += height] = ModBlocks.tachyonicParticleShell;
////                                }
////                                else if (k * 8 + l < waterHeight)
////                                {
////                                    data[index += height] = Blocks.water;
////                                }
//                                else
//                                {
//                                    data[index] = null;
//                                }
//                            }
//
//                            d10 += d12;
//                            d11 += d13;
//                        }
//
//                        d1 += d5;
//                        d2 += d6;
//                        d3 += d7;
//                        d4 += d8;
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public Chunk provideChunk(int chunkX, int chunkZ)
//    {
//        random.setSeed(chunkX * 0x4F9939F508L + chunkZ * 0x1EF1565BD5L);
//
//        Block[] data = new Block[65536];
//        byte[] meta = new byte[data.length];
//        BiomeGenBase[] biomes = worldObj.getWorldChunkManager().loadBlockGeneratorData(null, chunkX * 16, chunkZ * 16, 16, 16); // Forge Move up to allow for passing to replaceBiomeBlocks
//
//        generateBlocks(chunkX, chunkZ, data);
////        replaceBiomeBlocks(chunkX, chunkZ, data, meta, biomes);
////        netherCaveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, data);
////        genNetherBridge.func_151539_a(this, worldObj, chunkX, chunkZ, data);
//
//        Chunk chunk = new Chunk(worldObj, data, meta, chunkX, chunkZ);
//        byte[] bytes = chunk.getBiomeArray();
//
//        for (int i = 0; i < bytes.length; ++i)
//        {
//            bytes[i] = (byte) biomes[i].biomeID;
//        }
//
//        chunk.resetRelightChecks();
//        return chunk;
//    }
//
//    private void initializeNoiseField(int x, int y, int z)
//    {
//        double d0 = 684.412D;
//        double d1 = 684.412D;
//        double d2 = 512.0D;
//        double d3 = 512.0D;
//        noiseData4 = noiseGen6.generateNoiseOctaves(noiseData4, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
//        noiseData1 = noiseGen3.generateNoiseOctaves(noiseData1, x, y, z, 5, 33, 5, 8.55515D, 4.277575D, 8.55515D);
//        noiseData2 = noiseGen1.generateNoiseOctaves(noiseData2, x, y, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
//        noiseData3 = noiseGen2.generateNoiseOctaves(noiseData3, x, y, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
//        boolean flag1 = false;
//        boolean flag = false;
//        int l = 0;
//        int i1 = 0;
//        double d4 = 8.5D;
//        byte b0 = 2;
//
//        for (int j1 = 0; j1 < 5; ++j1)
//        {
//            for (int k1 = 0; k1 < 5; ++k1)
//            {
//                float f = 0.0F;
//                float f1 = 0.0F;
//                float f2 = 0.0F;
//                BiomeGenBase biome1 = biomesForGeneration[j1 + 2 + (k1 + 2) * 10];
//
//                for (int l1 = -b0; l1 <= b0; ++l1)
//                {
//                    for (int i2 = -b0; i2 <= b0; ++i2)
//                    {
//                        BiomeGenBase biome2 = biomesForGeneration[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
//                        float f3 = biome2.rootHeight;
//                        float f4 = biome2.heightVariation;
//
////                        if (worldType == WorldType.AMPLIFIED && f3 > 0.0F)
////                        {
////                            f3 = 1.0F + f3 * 2.0F;
////                            f4 = 1.0F + f4 * 4.0F;
////                        }
//
//                        float f5 = MathHelper.sin(parabolicField[l1 + 2 + (i2 + 2) * 5] / (f3 + 2.0F) * 10) * 2;
//
//                        if (biome2.rootHeight > biome1.rootHeight)
//                        {
//                            f5 /= 2.0F;
//                        }
//
//                        f += f4 * f5;
//                        f1 += f3 * f5;
//                        f2 += f5;
//                    }
//                }
//
//                f /= f2;
//                f1 /= f2;
//                f = f * 0.9F + 0.1F;
//                f1 = (f1 * 4.0F - 1.0F) / 8.0F;
//                double d12 = noiseData4[i1] / 8000.0D;
//
//                if (d12 < 0.0D)
//                {
//                    d12 = -d12 * 0.3D;
//                }
//
//                d12 = d12 * 3.0D - 2.0D;
//
//                if (d12 < 0.0D)
//                {
//                    d12 /= 2.0D;
//
//                    if (d12 < -1.0D)
//                    {
//                        d12 = -1.0D;
//                    }
//
//                    d12 /= 1.4D;
//                    d12 /= 2.0D;
//                }
//                else
//                {
//                    if (d12 > 1.0D)
//                    {
//                        d12 = 1.0D;
//                    }
//
//                    d12 /= 8.0D;
//                }
//
//                ++i1;
//                double d13 = f1;
//                double d14 = f;
//                d13 += d12 * 0.2D;
//                d13 = d13 * 8.5D / 8.0D;
//                double d5 = 8.5D + d13 * 4.0D;
//
//                for (int j2 = 0; j2 < 33; ++j2)
//                {
//                    double d6 = (j2 - d5) * 12.0D * 128.0D / 256.0D / d14;
//
//                    if (d6 < 0.0D)
//                    {
//                        d6 *= 4.0D;
//                    }
//
//                    double d7 = noiseData2[l] / 512.0D;
//                    double d8 = noiseData3[l] / 512.0D;
//                    double d9 = (noiseData1[l] / 10.0D + 1.0D) / 2.0D;
//                    double d10 = MathHelper.denormalizeClamp(d7, d8, d9) - d6;
//
//                    if (j2 > 29)
//                    {
//                        double d11 = (j2 - 29) / 3.0F;
//                        d10 = d10 * (1.0D - d11) + -10.0D * d11;
//                    }
//
//                    noiseField[l] = Math.cos(d10 * Math.PI);
//                    ++l;
//                }
//            }
//        }
//    }
//
//    @Override
//    public Chunk loadChunk(int x, int z)
//    {
//        return provideChunk(x, z);
//    }
//
//    @Override
//    public boolean chunkExists(int x, int z)
//    {
//        return true;
//    }
//
//    @Override
//    public void populate(IChunkProvider provider, int chunkX, int chunkZ)
//    {
//        BlockFalling.fallInstantly = true;
//        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(provider, worldObj, worldObj.rand, chunkX, chunkZ, false));
//
//        int x = chunkX * 16;
//        int z = chunkZ * 16;
//        BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + 16, z + 16);
//        biome.decorate(worldObj, worldObj.rand, x, z);
//
//        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(provider, worldObj, worldObj.rand, chunkX, chunkZ, false));
//        BlockFalling.fallInstantly = false;
//    }
//
//    @Override
//    public boolean saveChunks(boolean b, IProgressUpdate progressUpdate)
//    {
//        return true;
//    }
//
//    @Override
//    public void saveExtraData()
//    {
//    }
//
//    @Override
//    public boolean unloadQueuedChunks()
//    {
//        return false;
//    }
//
//    @Override
//    public boolean canSave()
//    {
//        return true;
//    }
//
//    @Override
//    public String makeString()
//    {
//        return "RandomLevelSource";
//    }
//
//    @Override
//    public List getPossibleCreatures(EnumCreatureType type, int x, int y, int z)
//    {
//        return new ArrayList<>();
//    }
//
//    @Override
//    public ChunkPosition func_147416_a(World world, String s, int x, int y, int z)
//    {
//        return null;
//    }
//
//    @Override
//    public int getLoadedChunkCount()
//    {
//        return 0;
//    }
//
//    @Override
//    public void recreateStructures(int x, int z)
//    {
//    }
//}
