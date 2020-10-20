package com.fiskmods.heroes.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntitySHSubatomicChargeFX extends EntitySHFX
{
    private float flameScale;

    public EntitySHSubatomicChargeFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionX = this.motionX * 0.009999999776482582D + motionX;
        this.motionY = this.motionY * 0.009999999776482582D + motionY;
        this.motionZ = this.motionZ * 0.009999999776482582D + motionZ;
        flameScale = particleScale;
        particleRed = 1.0F;
        particleGreen = 1.0F;
        particleBlue = 1.0F;
        particleMaxAge = 20;
        noClip = true;
        setParticleTextureIndex(17);
    }

    @Override
    public void renderParticle(Tessellator tesselator, float partialTicks, float f, float f1, float f2, float f3, float f4)
    {
        float f5 = (particleAge + partialTicks) / particleMaxAge;
        particleScale = flameScale * (1.0F - f5 * f5 * 0.5F) * 0.75F;
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

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (particleAge++ >= particleMaxAge)
        {
            setDead();
        }

        moveEntity(motionX, motionY, motionZ);
        motionX *= 0.9599999785423279D;
        motionY *= 0.9599999785423279D;
        motionZ *= 0.9599999785423279D;

        if (onGround)
        {
            setDead();
        }
    }
}
