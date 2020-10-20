package com.fiskmods.heroes.client.particle;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.json.cloud.ParticleColor;
import com.fiskmods.heroes.client.json.cloud.ParticleColors;
import com.fiskmods.heroes.common.Vec3Container;
import com.fiskmods.heroes.util.FiskMath;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySHBreachFX extends EntityFX
{
    private Vec3Container origin;
    private ParticleColors colors;

    private double destX, destY, destZ;
    private float originScale;

    public EntitySHBreachFX(World world, Vec3Container vec, ParticleColor[] color, double dx, double dy, double dz, float scale)
    {
        super(world, vec.getX(), vec.getY(), vec.getZ(), 0, 0, 0);
        particleMaxAge = SHConstants.TICKS_TELEPORT_DELAY * 2;
        noClip = true;

        origin = vec;
        prevPosX = origin.getX();
        prevPosY = origin.getY();
        prevPosZ = origin.getZ();
        destX = dx + rand.nextFloat() * rand.nextFloat() * 0.3;
        destY = dy + rand.nextFloat() * rand.nextFloat() * 0.3;
        destZ = dz + rand.nextFloat() * rand.nextFloat() * 0.3;
        colors = new ParticleColors(color, rand);
        originScale = scale;
    }

    @Override
    public int getBrightnessForRender(float partialTicks)
    {
        return 240 | 240 << 16;
    }

    @Override
    public float getBrightness(float partialTicks)
    {
        return 1;
    }

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        float f = (float) particleAge / particleMaxAge;
        f = FiskMath.curveCrests(f * 2);

        posX = origin.getX() + destX * f;
        posY = origin.getY() + destY * f;
        posZ = origin.getZ() + destZ * f;
        particleScale = originScale * f * 3;
        particleRed = colors.red(f);
        particleGreen = colors.green(f);
        particleBlue = colors.blue(f);

        setParticleTextureIndex(Math.round(4 + 3 * f));

        if (++particleAge > particleMaxAge + 1)
        {
            setDead();
        }
    }
}
