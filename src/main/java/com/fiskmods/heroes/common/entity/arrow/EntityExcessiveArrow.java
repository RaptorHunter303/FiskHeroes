package com.fiskmods.heroes.common.entity.arrow;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityExcessiveArrow extends EntityTrickArrow
{
    public EntityExcessiveArrow(World world)
    {
        super(world);
    }

    public EntityExcessiveArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityExcessiveArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityExcessiveArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    public boolean onCaught(EntityLivingBase entity)
    {
        return false;
    }
}
