package com.fiskmods.heroes.common.world;

import com.fiskmods.heroes.common.world.biome.ModBiomes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderQuantumRealm extends WorldProvider
{
    @Override
    public void registerWorldChunkManager()
    {
        worldChunkMgr = new WorldChunkManagerQuantumRealm(ModBiomes.QUANTUM_REALM, 0.0F);
        dimensionId = ModDimensions.QUANTUM_REALM_ID;
        hasNoSky = true;
    }

    @Override
    public IChunkProvider createChunkGenerator()
    {
        return new ChunkProviderQuantumRealm(worldObj, worldObj.getSeed());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_)
    {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3 getFogColor(float p_76562_1_, float p_76562_2_)
    {
        return Vec3.createVectorHelper(0, 0.1D, 0.1D);
    }

    @Override
    public double getMovementFactor()
    {
        return 1.0D;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isSkyColored()
    {
        return true;
    }

    @Override
    public boolean canRespawnHere()
    {
        return false;
    }

    @Override
    public boolean isSurfaceWorld()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getCloudHeight()
    {
        return 8.0F;
    }

    @Override
    public boolean canMineBlock(EntityPlayer player, int x, int y, int z)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
    {
        return Vec3.createVectorHelper(0, 0.1D, 0.1D);
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return worldObj.getTopBlock(x, z).getMaterial().blocksMovement();
    }

    @Override
    public int getAverageGroundLevel()
    {
        return 128;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z)
    {
        return false;
    }

    @Override
    public String getDimensionName()
    {
        return "Quantum Realm";
    }
}
