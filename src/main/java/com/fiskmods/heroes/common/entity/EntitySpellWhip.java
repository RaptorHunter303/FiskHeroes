package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.network.MessageSpellWhip;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySpellWhip extends EntityThrowable implements IEntityAdditionalSpawnData
{
    public EntityLivingBase casterEntity;
    public Entity hookedEntity;

    public float stretch, prevStretch;
    public float whipPull, prevWhipPull;

    public EntitySpellWhip(World world)
    {
        super(world);
        ignoreFrustumCheck = true;
    }

    public EntitySpellWhip(World world, EntityLivingBase caster)
    {
        super(world, caster);
        ignoreFrustumCheck = true;
        casterEntity = caster;
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.025F;
    }

    @Override
    protected float func_70182_d()
    {
        return 2.5F;
    }

    @Override
    public boolean isBurning()
    {
        return false;
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        prevStretch = stretch;
        prevWhipPull = whipPull;

        if (hookedEntity != null && stretch < 1)
        {
            stretch += 1F / SHConstants.TICKS_WHIP_STRETCH;
        }

        if (whipPull >= 1 || casterEntity == null)
        {
            super.setDead();
            return;
        }

        if (whipPull > 0 || casterEntity.swingProgress > 0 && ticksExisted > 10 || ticksExisted > 1200)
        {
            whipPull += 1F / SHConstants.TICKS_WHIP_PULL;
        }

        if (!worldObj.isRemote)
        {
            if (hookedEntity != null)
            {
                if (ticksExisted % 20 == 0)
                {
                    EntityLivingBase caster = SHHelper.filterDuplicate(casterEntity);
                    double x = hookedEntity.motionX;
                    double y = hookedEntity.motionY;
                    double z = hookedEntity.motionZ;

                    hookedEntity.hurtResistantTime = 0;
                    hookedEntity.attackEntityFrom(ModDamageSources.ELDRITCH_WHIP.apply(caster), Rule.DMG_SPELL_WHIPBURN.getHero(caster));
                    hookedEntity.motionX = x;
                    hookedEntity.motionY = y;
                    hookedEntity.motionZ = z;
                }

                double d = 0.5;
                hookedEntity.motionX *= d;
                hookedEntity.motionZ *= d;
            }

            if (casterEntity.swingProgress > 0 && casterEntity.prevSwingProgress == 0)
            {
                if (hookedEntity != null)
                {
                    double d0 = casterEntity.posX - posX;
                    double d1 = casterEntity.posY - posY;
                    double d2 = casterEntity.posZ - posZ;
                    double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
                    hookedEntity.motionX += d0 / d3 * 2;
                    hookedEntity.motionY += d1 / d3 / 2;
                    hookedEntity.motionZ += d2 / d3 * 2;
                }
            }

            if (casterEntity.isDead || !casterEntity.isEntityAlive() || getDistanceSqToEntity(casterEntity) > 1024 || casterEntity.getHeldItem() != null || SHData.SHIELD_BLOCKING.get(casterEntity))
            {
                setDead();
                return;
            }
        }

        if (hookedEntity != null)
        {
            if (!hookedEntity.isDead)
            {
                posX = hookedEntity.posX;
                posY = hookedEntity.boundingBox.minY + hookedEntity.height / 2;
                posZ = hookedEntity.posZ;
                return;
            }

            hookedEntity = null;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        if (hookedEntity == null)
        {
            if (mop.entityHit != null)
            {
                if (mop.entityHit != casterEntity)
                {
                    if (mop.entityHit instanceof EntitySpellDuplicate)
                    {
                        EntitySpellDuplicate entity = (EntitySpellDuplicate) mop.entityHit;

                        if (entity.isOwner(casterEntity))
                        {
                            return;
                        }
                        else if (casterEntity instanceof EntitySpellDuplicate && ((EntitySpellDuplicate) casterEntity).isOwner(entity.getOwner()))
                        {
                            return;
                        }
                    }
                    else if (casterEntity instanceof EntitySpellDuplicate && ((EntitySpellDuplicate) casterEntity).isOwner(mop.entityHit))
                    {
                        return;
                    }

                    EntityLivingBase caster = SHHelper.filterDuplicate(casterEntity);
                    hookedEntity = mop.entityHit;
                    
                    SHNetworkManager.wrapper.sendToDimension(new MessageSpellWhip(this, hookedEntity), dimension);
                    mop.entityHit.attackEntityFrom(ModDamageSources.ELDRITCH_WHIP.apply(caster), Rule.DMG_SPELL_WHIP.getHero(caster));
                }
            }
            else
            {
                motionX = motionY = motionZ = 0;

                if (!worldObj.isRemote)
                {
                    setDead();
                }
            }
        }
    }

    @Override
    public void setDead()
    {
        if (whipPull == 0)
        {
            whipPull += 1F / SHConstants.TICKS_WHIP_PULL;
        }
    }

    @Override
    public void readSpawnData(ByteBuf buf)
    {
        Entity entity = worldObj.getEntityByID(buf.readInt());

        if (entity instanceof EntityLivingBase)
        {
            casterEntity = (EntityLivingBase) entity;
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        buf.writeInt(casterEntity != null ? casterEntity.getEntityId() : -1);
    }
}
