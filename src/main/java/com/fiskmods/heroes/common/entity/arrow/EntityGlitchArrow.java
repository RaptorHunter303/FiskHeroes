package com.fiskmods.heroes.common.entity.arrow;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityGlitchArrow extends EntityTrickArrow
{
    public boolean glitching;

    public EntityGlitchArrow(World world)
    {
        super(world);
    }

    public EntityGlitchArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityGlitchArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityGlitchArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected void init(EntityLivingBase shooter, boolean horizontalBow)
    {
        super.init(shooter, horizontalBow);
        noClip = true;
    }

    @Override
    protected void checkInGround()
    {
    }

    @Override
    protected void onImpactBlock(MovingObjectPosition mop)
    {
    }
}
