package com.fiskmods.heroes.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySHThickSmokeFX extends EntitySHSmokeFX
{
    public EntitySHThickSmokeFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        particleScale = rand.nextFloat() * rand.nextFloat() * 16.0F + 1.0F;
        particleMaxAge = (int) (140.0 / (rand.nextFloat() * 0.5 + 0.5)) + 2;
        particleRed = particleGreen = particleBlue = rand.nextFloat() * 0.3F + 0.2F;
        noClip = false;
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

        motionY += 0.0001;
        moveEntity(motionX, motionY, motionZ);
        setParticleTextureIndex(7 - particleAge * 8 / particleMaxAge);

        double d = 0.9D;
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
