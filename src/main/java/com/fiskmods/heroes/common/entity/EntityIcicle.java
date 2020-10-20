package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityIcicle extends EntityThrowable implements IPiercingProjectile
{
    public EntityIcicle(World world)
    {
        super(world);
    }

    public EntityIcicle(World world, EntityLivingBase entity)
    {
        super(world, entity);
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
        return 0.025F;
    }

    @Override
    protected float func_70182_d()
    {
        return 4.0F;
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        if (mop.entityHit == null || getThrower() == null || mop.entityHit.attackEntityFrom(ModDamageSources.ICICLE.apply(getThrower()), Rule.DMG_ICICLE.getHero(getThrower())))
        {
            shatter();
        }
    }

    public void shatter()
    {
        for (int i = 0; i < 8; ++i)
        {
            SHParticleType.ICICLE_BREAK.spawn(posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        }

        playSound(SHSounds.ENTITY_ICICLE_IMPACT.toString(), 1, 0.9F + rand.nextFloat() * 0.1F);
        setDead();
    }

    @Override
    public boolean canPierceDurability(EntityLivingBase entity)
    {
        return false;
    }
}
