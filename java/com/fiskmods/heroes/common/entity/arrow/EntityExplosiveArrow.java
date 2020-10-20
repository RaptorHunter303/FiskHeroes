package com.fiskmods.heroes.common.entity.arrow;

import com.fiskmods.heroes.common.config.Rule;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityExplosiveArrow extends EntityTrickArrow
{
    public EntityExplosiveArrow(World world)
    {
        super(world);
    }

    public EntityExplosiveArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityExplosiveArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityExplosiveArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        super.onImpact(mop);
        trigger();
    }

    @Override
    public boolean onCaught(EntityLivingBase entity)
    {
        trigger();
        return true;
    }

    public void trigger()
    {
        if (!worldObj.isRemote)
        {
            worldObj.createExplosion(getShooter(), posX, posY, posZ, Rule.RADIUS_EXPLOSIVEARROW.getHero(getShooter()), false);
            setDead();
        }
    }
}
