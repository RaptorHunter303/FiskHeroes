package com.fiskmods.heroes.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class EntitySHFX extends EntityFX
{
    protected EntitySHFX(World world, double x, double y, double z)
    {
        super(world, x, y, z);
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    public EntitySHFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    @Override
    public int getFXLayer()
    {
        return SHParticlesClient.LAYER_SH;
    }

    @Override
    public void setParticleTextureIndex(int index)
    {
        particleTextureIndexX = index % 16;
        particleTextureIndexY = index / 16;
    }
}
