package com.fiskmods.heroes.client.particle;

import com.fiskmods.heroes.client.json.cloud.ParticleColor;
import com.fiskmods.heroes.client.json.cloud.ParticleColors;
import com.fiskmods.heroes.util.FiskMath;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySHCloudSmokeFX extends EntitySHSmokeFX
{
    private ParticleColors colors;

    public EntitySHCloudSmokeFX(World world, ParticleColor[] color, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        particleScale = rand.nextFloat() * 4 + 2;
        particleMaxAge = (int) (14.0 / (rand.nextFloat() * 0.5 + 0.5)) + 2;
        colors = new ParticleColors(color, rand);
        noClip = false;
    }

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (++particleAge > particleMaxAge)
        {
            setDead();
        }

        float f = (float) particleAge / particleMaxAge;
        f = FiskMath.curveCrests(f * 2);
        particleRed = colors.red(f);
        particleGreen = colors.green(f);
        particleBlue = colors.blue(f);

        setParticleTextureIndex(7 - particleAge * 8 / particleMaxAge);
        moveEntity(motionX, motionY, motionZ);

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
