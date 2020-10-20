package com.fiskmods.heroes.common.entity.arrow;

import com.fiskmods.heroes.common.arrowtype.ArrowTypeManager;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityBoxingGloveArrow extends EntityTrickArrow
{
    public EntityBoxingGloveArrow(World world)
    {
        super(world);
    }

    public EntityBoxingGloveArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityBoxingGloveArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityBoxingGloveArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected float getGravityVelocity()
    {
        return (getArrowId() == 0 ? ArrowTypeManager.NORMAL : ArrowTypeManager.BOXING_GLOVE).getGravityFactor();
    }

    @Override
    protected float calculateDamage(Entity entityHit)
    {
        float dmg = super.calculateDamage(entityHit);

        if (getArrowId() == 0)
        {
            dmg /= ArrowTypeManager.BOXING_GLOVE.getDamageMultiplier();
            dmg *= ArrowTypeManager.NORMAL.getDamageMultiplier();
        }

        return dmg;
    }

    @Override
    protected void handlePostDamageEffects(EntityLivingBase entityHit)
    {
        super.handlePostDamageEffects(entityHit);

        if (getArrowId() > 0)
        {
            int k = 2;

            if (k > 0)
            {
                float velocity = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);

                if (velocity > 0.0F)
                {
                    double knockback = k * 0.6000000238418579D / velocity;
                    entityHit.addVelocity(motionX * knockback, motionY * knockback, motionZ * knockback);
                }
            }
        }
    }

    @Override
    protected void onImpactEntity(MovingObjectPosition mop)
    {
        if (getArrowId() == 0)
        {
            super.onImpactEntity(mop);
        }
        else
        {
            if (mop.entityHit != null)
            {
                shootingEntity = getShooter();
                float dmg = calculateDamage(mop.entityHit);

                if (dmg > 0)
                {
                    if (isBurningInternal() && canTargetEntity(mop.entityHit))
                    {
                        SHHelper.ignite(mop.entityHit, 5);
                    }

                    if (mop.entityHit.attackEntityFrom(getDamageSource(mop.entityHit), dmg))
                    {
                        if (mop.entityHit instanceof EntityLivingBase)
                        {
                            handlePostDamageEffects((EntityLivingBase) mop.entityHit);

                            if (shootingEntity instanceof EntityPlayerMP && mop.entityHit != shootingEntity && mop.entityHit instanceof EntityPlayer)
                            {
                                ((EntityPlayerMP) shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                            }
                        }

                        playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
                    }
                }
            }

            motionX *= -0.10000000149011612D;
            motionY *= -0.10000000149011612D;
            motionZ *= -0.10000000149011612D;
            rotationYaw += 180.0F;
            prevRotationYaw += 180.0F;
            ticksInAir = 0;

            setArrowId(0);
            setDamage(0);
            setKnockbackStrength(0);
            setIsCritical(false);
        }
    }
}
