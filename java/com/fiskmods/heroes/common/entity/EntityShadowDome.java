package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.event.ClientEventHandler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityShadowDome extends EntityThrowable
{
    public EntityShadowDome(World world)
    {
        super(world);
        noClip = true;
    }

    public EntityShadowDome(World world, EntityLivingBase entity)
    {
        super(world, entity);
        noClip = true;
    }

    @Override
    public boolean isBurning()
    {
        return false;
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.0F;
    }

    @Override
    protected float func_70182_d()
    {
        return 0.0F;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (ticksExisted > Rule.TICKS_SHADOWDOME.get(this))
        {
            setDead();
        }
        else if (worldObj.isRemote)
        {
            float radius = Rule.RADIUS_SHADOWDOME.get(this);
            double dist = FiskHeroes.proxy.getPlayer().getDistanceToEntity(this);

            if (dist <= radius)
            {
                ClientEventHandler.INSTANCE.shadowDome = Math.max(1 - dist / radius, ClientEventHandler.INSTANCE.shadowDome);
            }
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
    }
}
