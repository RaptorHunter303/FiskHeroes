package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.SHConstants;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityLightningCast extends Entity implements IEntityAdditionalSpawnData
{
    public EntityLivingBase casterEntity;
    public Entity anchorEntity;

    private int doExplosion;

    public EntityLightningCast(World world)
    {
        super(world);
        noClip = true;
        renderDistanceWeight = 64;
        ignoreFrustumCheck = true;
        preventEntitySpawning = false;
        setSize(0.1F, 0.1F);
    }

    public EntityLightningCast(World world, EntityLivingBase caster, Entity entity, Vec3 vec3, int explode)
    {
        this(world);
        casterEntity = caster;
        anchorEntity = entity;
        doExplosion = explode;
        setLocationAndAngles(vec3.xCoord, vec3.yCoord, vec3.zCoord, 0, 0);
    }

    @Override
    public void onUpdate()
    {
        if (doExplosion > 0)
        {
            if (worldObj.isRemote)
            {
                if ((doExplosion & 1) == 1)
                {
                    worldObj.spawnParticle("largeexplode", posX, posY, posZ, 0, 0, 0);
                }
            }
            else if ((doExplosion & 2) == 2)
            {
                playSound("random.explode", 6, 0.6F + rand.nextFloat() * 0.2F);
            }

            doExplosion = 0;
        }

        if (++ticksExisted > SHConstants.TICKS_LIGHTNING_CAST)
        {
            setDead();
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        buf.writeInt(casterEntity != null ? casterEntity.getEntityId() : -1);
        buf.writeInt(anchorEntity != null ? anchorEntity.getEntityId() : -1);
        buf.writeByte(doExplosion);
    }

    @Override
    public void readSpawnData(ByteBuf buf)
    {
        Entity entity = worldObj.getEntityByID(buf.readInt());

        if (entity instanceof EntityLivingBase)
        {
            casterEntity = (EntityLivingBase) entity;
        }

        anchorEntity = worldObj.getEntityByID(buf.readInt());
        doExplosion = buf.readByte();
    }
}
