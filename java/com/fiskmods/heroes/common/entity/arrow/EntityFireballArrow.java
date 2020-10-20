package com.fiskmods.heroes.common.entity.arrow;

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
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFireballArrow extends EntityTrickArrow
{
    public EntityFireballArrow(World world)
    {
        super(world);
    }

    public EntityFireballArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityFireballArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityFireballArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected boolean shouldSpawnParticles()
    {
        return true;
    }

    @Override
    protected void spawnTrailingParticles()
    {
        float spread = 0.2F;

        for (int i = 0; i < 3; ++i)
        {
            SHParticleType.SHORT_FLAME.spawn(posX, posY, posZ, (rand.nextFloat() - 0.5F) * spread, (rand.nextFloat() - 0.5F) * spread, (rand.nextFloat() - 0.5F) * spread);
        }
    }

    @Override
    protected DamageSource getDamageSource(Entity entity)
    {
        return ModDamageSources.causeFireballDamage(this, getShooter());
    }

    @Override
    protected void handlePostDamageEffects(EntityLivingBase entityHit)
    {
        SHHelper.ignite(entityHit, SHConstants.IGNITE_FIREBALL);
        super.handlePostDamageEffects(entityHit);
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        boolean flag = getArrowId() > 0;
        super.onImpact(mop);

        if (flag)
        {
            Vec3 v = mop.hitVec;

            if (v == null)
            {
                v = VectorHelper.centerOf(this);
            }

            if (worldObj.isRemote)
            {
                float spread = 1.5F;
                float spread1 = 0.5F;

                for (int i = 0; i < 200; ++i)
                {
                    SHParticleType.SHORT_FLAME.spawn(v.xCoord + (rand.nextFloat() - 0.5F) * spread, v.yCoord + (rand.nextFloat() - 0.5F) * spread, v.zCoord + (rand.nextFloat() - 0.5F) * spread, (rand.nextFloat() - 0.5F) * spread1, (rand.nextFloat() - 0.5F) * spread1, (rand.nextFloat() - 0.5F) * spread1);
                }
            }
            else
            {
                Hero hero = SHHelper.getHero(getShooter());
                float radius = Rule.RADIUS_FIREBALLARROW.get(getShooter(), hero);
                float dmg = Rule.DMG_FIREBALLARROW.get(getShooter(), hero);

                for (Entity entity : VectorHelper.getEntitiesNear(Entity.class, worldObj, posX, posY, posZ, radius))
                {
                    if (entity != getShooter() && entity != mop.entityHit)
                    {
                        float f = FiskMath.getScaledDistance(v, VectorHelper.centerOf(entity), radius);

                        if (entity.attackEntityFrom(ModDamageSources.causeFireballDamage(this, getShooter()), Math.max(dmg * f, 1)))
                        {
                            SHHelper.ignite(entity, MathHelper.ceiling_float_int(SHConstants.IGNITE_FIREBALL * f));
                        }
                    }
                }
            }

            playSound(SHSounds.ENTITY_FIREBALL_EXPLODE.toString(), 1.5F, 0.9F);
            setArrowId(0);
        }
    }
}
