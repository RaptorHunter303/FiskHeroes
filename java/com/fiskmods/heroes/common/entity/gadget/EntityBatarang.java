package com.fiskmods.heroes.common.entity.gadget;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBatarang extends EntityProjectile
{
    public EntityBatarang(World world)
    {
        super(world);
    }

    public EntityBatarang(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityBatarang(World world, EntityLivingBase shooter)
    {
        super(world, shooter);
    }

    @Override
    protected DamageSource getDamageSource(Entity entity)
    {
        return ModDamageSources.causeBatarangDamage(this, getShooter());
    }

    @Override
    public float getVelocityFactor()
    {
        return 3.5F;
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

        return dmg * Rule.DMGMULT_BATARANG.getHero(getShooter());
    }
}
