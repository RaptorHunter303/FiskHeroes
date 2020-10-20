package com.fiskmods.heroes.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntitySHQuantumParticleFX extends EntityFX
{
    public EntitySHQuantumParticleFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionX = this.motionX * 0.009999999776482582D + motionX;
        this.motionY = this.motionY * 0.009999999776482582D + motionY;
        this.motionZ = this.motionZ * 0.009999999776482582D + motionZ;
        particleRed = 0;
        particleBlue = 0.8F;
        particleGreen = 0.8F;
        particleMaxAge = 100;
        noClip = true;
        setParticleTextureIndex(1);

        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    @Override
    public void renderParticle(Tessellator tesselator, float partialTicks, float f, float f1, float f2, float f3, float f4)
    {
        // float age = particleAge + partialTicks;
        // float f5 = (float) (age >= particleMaxAge - 10 ? particleMaxAge - age : 10) / 10;
        // particleScale = 10 * f5;
        float f5 = (float) (particleAge >= particleMaxAge - 10 ? particleMaxAge - particleAge : 10) / 10;
        particleScale = 5 * f5;
        super.renderParticle(tesselator, partialTicks, f, f1, f2, f3, f4);
    }

    @Override
    public int getBrightnessForRender(float partialTicks)
    {
        return 15728880;
    }

    @Override
    public float getBrightness(float partialTicks)
    {
        return 1.0F;
    }
}
