package com.fiskmods.heroes.client.particle;

import com.fiskmods.heroes.client.json.cloud.ParticleColor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySHShadowSmokeFX extends EntitySHCloudSmokeFX
{
    private static final ParticleColor[] COLOR = {new ParticleColor(0.1F, 0.1F), new ParticleColor(0.1F, 0.1F), new ParticleColor(0.12F, 0.12F)};

    public EntitySHShadowSmokeFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, COLOR, x, y, z, motionX, motionY, motionZ);
//      particleRed = particleGreen = particleBlue = rand.nextFloat() * 0.1F + 0.1F;
//      particleBlue *= 1.2F;
        particleScale *= 0.25;
        this.motionX *= 0.25;
        this.motionY *= 0.25;
        this.motionZ *= 0.25;
    }
}
