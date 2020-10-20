package com.fiskmods.heroes.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityGrapplingHookCable extends Entity
{
    public EntityLivingBase entity;
    public EntityPlayer player;

    public int primaryColor = 0x7F664C;
    public int secondaryColor = 0x5A4736;

    public EntityGrapplingHookCable(World world)
    {
        super(world);
        noClip = true;
        renderDistanceWeight = 10;
        ignoreFrustumCheck = true;
        preventEntitySpawning = false;
        setSize(0.1F, 0.1F);
    }

    public EntityGrapplingHookCable(World world, EntityLivingBase entity, EntityPlayer player)
    {
        this(world);
        this.entity = entity;
        this.player = player;
        setLocationAndAngles(entity.posX, entity.posY + entity.height / 2, entity.posZ, entity.rotationYaw, entity.rotationPitch);
    }

    public EntityGrapplingHookCable setColor(int primary, int secondary)
    {
        primaryColor = primary;
        secondaryColor = secondary;
        return this;
    }

    @Override
    public void onUpdate()
    {
        if (++ticksExisted > 2)
        {
            setDead();
        }
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
}
