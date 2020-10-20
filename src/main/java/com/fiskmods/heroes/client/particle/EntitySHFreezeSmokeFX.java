package com.fiskmods.heroes.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySHFreezeSmokeFX extends EntitySHSmokeFX
{
    public EntitySHFreezeSmokeFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        particleScale = rand.nextFloat() * rand.nextFloat() * 16.0F + 1.0F;
        particleMaxAge = (int) (8.0D / (rand.nextFloat() * 0.8D + 0.2D)) + 2;
        particleRed = rand.nextFloat() * 0.3F + 0.7F;
        particleGreen = 1;
        particleBlue = 1;
        noClip = false;
    }
}
