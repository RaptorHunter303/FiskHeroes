package com.fiskmods.heroes.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySHEterniumAuraFX extends EntityFX
{
    private final double originX, originY, originZ;
    private final int direction;

    public EntitySHEterniumAuraFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        particleRed = 0.5F + rand.nextFloat() * 0.5F;
        particleGreen = rand.nextFloat() * 0.4F;
        particleBlue = 0.9F + rand.nextFloat() * 0.1F;
        originX = x;
        originY = y;
        originZ = z;

        setParticleTextureIndex(0);
        setSize(0.02F, 0.02F);
        particleScale *= rand.nextFloat() * 0.6F + 0.5F;
        particleMaxAge = (int) (20.0D / (Math.random() * 0.8D + 0.2D));
        direction = rand.nextInt(2) * 2 - 1;
        noClip = true;
    }

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        float f = (particleMaxAge - particleAge) / 8F * direction;
        float f1 = 0.75F;
        posX = originX + MathHelper.cos(f) * f1;
        posY = originY + MathHelper.sin(f * 100) * f1 / 2;
        posZ = originZ + MathHelper.sin(f) * f1;

        if (++particleAge > particleMaxAge)
        {
            setDead();
        }
    }
}
