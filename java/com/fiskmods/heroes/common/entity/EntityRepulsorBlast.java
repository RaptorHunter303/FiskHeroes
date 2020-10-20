package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.SHConstants;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityRepulsorBlast extends Entity implements IEntityAdditionalSpawnData
{
    public EntityLivingBase casterEntity;
    public Vec3 targetVec;

    public EntityRepulsorBlast(World world)
    {
        super(world);
        noClip = true;
        renderDistanceWeight = 10;
        ignoreFrustumCheck = true;
        preventEntitySpawning = false;
        setSize(0.1F, 0.1F);
    }

    public EntityRepulsorBlast(World world, EntityLivingBase caster, Vec3 src, Vec3 dst)
    {
        this(world);
        targetVec = dst;
        casterEntity = caster;
        setPosition(src.xCoord, src.yCoord, src.zCoord);
    }

    @Override
    public void onUpdate()
    {
        if (++ticksExisted > SHConstants.TICKS_REPULSOR)
        {
            setDead();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
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
    public boolean writeToNBTOptional(NBTTagCompound nbttagcompound)
    {
        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
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
        buf.writeFloat((float) targetVec.xCoord);
        buf.writeFloat((float) targetVec.yCoord);
        buf.writeFloat((float) targetVec.zCoord);
    }

    @Override
    public void readSpawnData(ByteBuf buf)
    {
        Entity entity = worldObj.getEntityByID(buf.readInt());

        if (entity instanceof EntityLivingBase)
        {
            casterEntity = (EntityLivingBase) entity;
        }

        targetVec = Vec3.createVectorHelper(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
}
