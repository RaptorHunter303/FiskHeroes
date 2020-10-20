package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFireBlast extends EntityThrowable
{
    public EntityFireBlast(World world)
    {
        super(world);
    }

    public EntityFireBlast(World world, EntityLivingBase entity)
    {
        super(world, entity);
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.01F;
    }

    @Override
    protected float func_70182_d()
    {
        return 1.5F;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (ticksExisted > 1200)
        {
            setDead();
        }

        float spread = 0.2F;

        for (int i = 0; i < 5; ++i)
        {
            SHParticleType.SHORT_FLAME.spawn(posX, posY, posZ, (rand.nextFloat() - 0.5F) * spread, (rand.nextFloat() - 0.5F) * spread, (rand.nextFloat() - 0.5F) * spread);
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        Vec3 v = mop.hitVec;
        float spread = 1.5F;
        float spread1 = 0.5F;

        if (v == null)
        {
            v = VectorHelper.centerOf(this);
        }

        for (int i = 0; i < 200; ++i)
        {
            SHParticleType.SHORT_FLAME.spawn(v.xCoord + (rand.nextFloat() - 0.5F) * spread, v.yCoord + (rand.nextFloat() - 0.5F) * spread, v.zCoord + (rand.nextFloat() - 0.5F) * spread, (rand.nextFloat() - 0.5F) * spread1, (rand.nextFloat() - 0.5F) * spread1, (rand.nextFloat() - 0.5F) * spread1);
        }

        if (!worldObj.isRemote)
        {
            Hero hero = getThrower() != null ? SHHelper.getHero(getThrower()) : null;
            float dmg = Rule.DMG_FIREBALL.get(getThrower(), hero);
            float radius = Rule.RADIUS_FIREBALL.get(getThrower(), hero);
            
            if (mop.entityHit != null && mop.entityHit.attackEntityFrom(ModDamageSources.causeFireballDamage(this, getThrower()), dmg))
            {
                SHHelper.ignite(mop.entityHit, SHConstants.IGNITE_FIREBALL);
            }

            for (Entity entity : VectorHelper.getEntitiesNear(Entity.class, worldObj, posX, posY, posZ, radius))
            {
                if (entity != getThrower() && entity != mop.entityHit)
                {
                    float f = FiskMath.getScaledDistance(v, VectorHelper.centerOf(entity), radius);

                    if (entity.attackEntityFrom(ModDamageSources.causeFireballDamage(this, getThrower()), Math.max(dmg * f, 1)))
                    {
                        SHHelper.ignite(entity, MathHelper.ceiling_float_int(SHConstants.IGNITE_FIREBALL * f));
                    }
                }
            }
        }

        playSound(SHSounds.ENTITY_FIREBALL_EXPLODE.toString(), 1.5F, 0.9F);
        setDead();
    }
}
