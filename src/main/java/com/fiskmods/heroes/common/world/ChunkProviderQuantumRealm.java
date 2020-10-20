package com.fiskmods.heroes.common.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

public class ChunkProviderQuantumRealm implements IChunkProvider
{
    private World worldObj;
    private Random random;

    public ChunkProviderQuantumRealm(World world, long seed)
    {
        worldObj = world;
        random = new Random(seed);
    }

    @Override
    public Chunk loadChunk(int x, int z)
    {
        return provideChunk(x, z);
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ)
    {
        Chunk chunk = new Chunk(worldObj, chunkX, chunkZ);
        chunk.generateSkylightMap();

        return chunk;

        // random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        // BiomeGenBase[] biomes = worldObj.getWorldChunkManager().loadBlockGeneratorData(null, chunkX * 16, chunkZ * 16, 16, 16); // Forge Move up to allow for passing to replaceBiomeBlocks
        // Block[] blocks = new Block[32768];
        // byte[] meta = new byte[blocks.length];
        //
        // replaceBiomeBlocks(chunkX, chunkZ, blocks, meta, biomes);
        // Chunk chunk = new Chunk(worldObj, blocks, meta, chunkX, chunkZ);
        // byte[] abyte = chunk.getBiomeArray();
        //
        // for (int k = 0; k < abyte.length; ++k)
        // {
        // abyte[k] = (byte) biomes[k].biomeID;
        // }
        //
        // chunk.resetRelightChecks();
        // return chunk;
    }

    // private Block[] blockData;
    // private byte[] blockMeta;
    // private int maxHeight;
    //
    // public void replaceBiomeBlocks(int chunkX, int chunkZ, Block[] blocks, byte[] meta, BiomeGenBase[] biomes)
    // {
    // ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, chunkX, chunkZ, blocks, meta, biomes, worldObj);
    // MinecraftForge.EVENT_BUS.post(event);
    //
    // if (event.getResult() == Result.DENY)
    // {
    // return;
    // }
    //
    // blockData = blocks;
    // blockMeta = meta;
    // maxHeight = blocks.length / 16 / 16;
    //
    //// for (int x = 0; x < 16; ++x)
    //// {
    //// for (int y = 0; y < maxHeight; ++y)
    //// {
    //// for (int z = 0; z < 16; ++z)
    //// {
    //// int index = y + maxHeight * z + maxHeight * 16 * x;
    ////
    //// if (index )
    //// blocks[index] = Blocks.stone;
    //// }
    //// }
    //// }
    //
    // for (int i = 0; i < blocks.length; ++i)
    // {
    // if (i % 8 == 0)
    // {
    // blocks[i >> 2] = Blocks.stone;
    // }
    // }
    // }
    //
    // private void setBlock(int x, int y, int z, Block block, int metadata)
    // {
    // int index = y + maxHeight * z + maxHeight * 16 * x;
    // blockData[index] = block;
    // blockMeta[index] = (byte) metadata;
    // }
    //
    // private void setBlock(int x, int y, int z, Block block)
    // {
    // setBlock(x, y, z, block, 0);
    // }

    @Override
    public boolean chunkExists(int x, int z)
    {
        return true;
    }

    @Override
    public void populate(IChunkProvider provider, int chunkX, int chunkZ)
    {
        BlockFalling.fallInstantly = true;
        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(provider, worldObj, worldObj.rand, chunkX, chunkZ, false));

        int x = chunkX * 16;
        int z = chunkZ * 16;
        BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + 16, z + 16);
        biome.decorate(worldObj, worldObj.rand, x, z);

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(provider, worldObj, worldObj.rand, chunkX, chunkZ, false));
        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean saveChunks(boolean b, IProgressUpdate progressUpdate)
    {
        return true;
    }

    @Override
    public void saveExtraData()
    {
    }

    @Override
    public boolean unloadQueuedChunks()
    {
        return false;
    }

    @Override
    public boolean canSave()
    {
        return true;
    }

    @Override
    public String makeString()
    {
        return "RandomLevelSource";
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType type, int x, int y, int z)
    {
        return new ArrayList<>();
    }

    @Override
    public ChunkPosition func_147416_a(World world, String s, int x, int y, int z)
    {
        return null;
    }

    @Override
    public int getLoadedChunkCount()
    {
        return 0;
    }

    @Override
    public void recreateStructures(int x, int z)
    {
    }
}
