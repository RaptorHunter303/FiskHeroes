package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityCactusSpike extends EntityThrowable implements IPiercingProjectile
{
    public EntityCactusSpike(World world)
    {
        super(world);
        setSize(0.1F, 0.1F);
    }

    public EntityCactusSpike(World world, EntityLivingBase entity)
    {
        super(world, entity);
        setSize(0.1F, 0.1F);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (ticksExisted > 200)
        {
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.0175F;
    }

    @Override
    protected float func_70182_d()
    {
        return 4.5F;
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        Entity entity = mop.entityHit;

        if (entity != null)
        {
            if (rand.nextBoolean())
            {
                entity.hurtResistantTime = 0;
            }

            double[] motion = {entity.motionX, entity.motionY, entity.motionZ};

            if (entity.attackEntityFrom(ModDamageSources.SPIKE.apply(getThrower()).setProjectile(), Rule.DMG_CACTUSSPIKE.getHero(getThrower())))
            {
                float f = 0.7F;
                entity.motionX -= (entity.motionX - motion[0]) * f;
                entity.motionY -= (entity.motionY - motion[1]) * f;
                entity.motionZ -= (entity.motionZ - motion[2]) * f;
            }
            else
            {
                return;
            }
        }

        setDead();
    }

    @Override
    public boolean canPierceDurability(EntityLivingBase entity)
    {
        return false;
    }
}
