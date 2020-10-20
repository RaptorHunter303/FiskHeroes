package com.fiskmods.heroes.client.render.effect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EffectEntity extends Entity
{
    public Entity anchorEntity;

    public EffectEntity(World world)
    {
        super(world);
        setDead();
    }

    public EffectEntity(World world, Entity anchor)
    {
        super(world);
        setSize(0.1F, 0.1F);
        noClip = true;
        ignoreFrustumCheck = true;
        anchorEntity = anchor;

        onUpdate();
        world.spawnEntityInWorld(this);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        return anchorEntity.isInRangeToRenderDist(distance) || super.isInRangeToRenderDist(distance);
    }

    @Override
    public boolean isBurning()
    {
        return false;
    }

    @Override
    public void onUpdate()
    {
        if (anchorEntity == null || !anchorEntity.isEntityAlive())
        {
            setDead();
        }
        else
        {
            setLocationAndAngles(anchorEntity.posX, anchorEntity.posY + anchorEntity.getYOffset() + anchorEntity.getEyeHeight(), anchorEntity.posZ, anchorEntity.rotationYaw, anchorEntity.rotationPitch);
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound nbttagcompound)
    {
        return false;
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
    }
}
