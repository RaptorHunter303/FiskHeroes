package com.fiskmods.heroes.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public abstract class EntitySHSmokeFX extends EntityFX
{
    public EntitySHSmokeFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionX = motionX + (float) (Math.random() * 2.0 - 1.0) * 0.05F;
        this.motionY = motionY + (float) (Math.random() * 2.0 - 1.0) * 0.05F;
        this.motionZ = motionZ + (float) (Math.random() * 2.0 - 1.0) * 0.05F;
        particleRed = particleGreen = particleBlue = rand.nextFloat() * 0.3F + 0.7F;
        particleScale = rand.nextFloat() * rand.nextFloat() * 6.0F + 1.0F;
        particleMaxAge = (int) (16.0 / (rand.nextFloat() * 0.8 + 0.2)) + 2;

        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (++particleAge >= particleMaxAge)
        {
            setDead();
        }

        motionY += 0.004;
        moveEntity(motionX, motionY, motionZ);
        setParticleTextureIndex(7 - particleAge * 8 / particleMaxAge);

        double d = 0.9;
        motionX *= d;
        motionY *= d;
        motionZ *= d;

        if (onGround)
        {
            motionX *= 0.7;
            motionZ *= 0.7;
        }
    }
}
