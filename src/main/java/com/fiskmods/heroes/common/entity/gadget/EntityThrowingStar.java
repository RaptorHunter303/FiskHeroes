package com.fiskmods.heroes.common.entity.gadget;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityThrowingStar extends EntityProjectile
{
    public EntityThrowingStar(World world)
    {
        super(world);
        setSize(0.15F, 0.15F);
    }

    public EntityThrowingStar(World world, double x, double y, double z)
    {
        super(world, x, y, z);
        setSize(0.15F, 0.15F);
    }

    public EntityThrowingStar(World world, EntityLivingBase shooter)
    {
        super(world, shooter);
        setSize(0.15F, 0.15F);
    }

    @Override
    protected DamageSource getDamageSource(Entity entity)
    {
        return ModDamageSources.causeThrowingStarDamage(this, getShooter());
    }

    @Override
    public float getVelocityFactor()
    {
        return 2.5F;
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.03F;
    }

    @Override
    protected float calculateDamage(Entity entityHit)
    {
        float velocity = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        float dmg = (float) (velocity * getDamage());

        if (getIsCritical())
        {
            dmg += rand.nextInt(MathHelper.ceiling_double_int(dmg) / 2 + 2);
        }

        return dmg * Rule.DMGMULT_THROWINGSTAR.getHero(getShooter());
    }
}
