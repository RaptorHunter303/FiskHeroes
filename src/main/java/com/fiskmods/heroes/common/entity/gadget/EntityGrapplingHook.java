package com.fiskmods.heroes.common.entity.gadget;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityGrapplingHook extends EntityProjectile
{
    public float grappleTimer;
    public float prevGrappleTimer;

    public EntityGrapplingHook(World world)
    {
        super(world);
    }

    public EntityGrapplingHook(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityGrapplingHook(World world, EntityLivingBase shooter)
    {
        super(world, shooter);
    }

    @Override
    protected void init(EntityLivingBase shooter, boolean horizontalBow)
    {
        super.init(shooter, horizontalBow);
        ignoreFrustumCheck = true;
        renderDistanceWeight = 20D;
        setDamage(0);
    }

    @Override
    public float getVelocityFactor()
    {
        return 3.0F;
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.03F;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        prevGrappleTimer = grappleTimer;

        if (getShooter() != null && !getShooter().isEntityAlive())
        {
            setDead();
        }

        for (Entity entity : (List<Entity>) worldObj.loadedEntityList)
        {
            if (entity instanceof EntityGrapplingHook)
            {
                EntityGrapplingHook hook = (EntityGrapplingHook) entity;

                if (hook != this && hook.isEntityAlive() && hook.getShooter() == getShooter())
                {
                    if (hook.ticksExisted > ticksExisted)
                    {
                        hook.setDead();
                    }
                    else
                    {
                        setDead();
                    }
                }
            }
        }

        if (inGround && grappleTimer < 1)
        {
            grappleTimer = Math.min(grappleTimer + 1F / 5, 1);
        }
    }

    @Override
    public float getBrightness(float f)
    {
        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(boundingBox.minY);
        int z = MathHelper.floor_double(posZ);

        if (getShooter() != null)
        {
            x = MathHelper.floor_double(getShooter().posX);
            y = MathHelper.floor_double(getShooter().boundingBox.minY);
            z = MathHelper.floor_double(getShooter().posZ);
        }

        if (worldObj.blockExists(x, 0, z))
        {
            return worldObj.getLightBrightness(x, y, z);
        }
        else
        {
            return 0.0F;
        }
    }

    @Override
    protected void onImpactEntity(MovingObjectPosition mop)
    {
    }
}
