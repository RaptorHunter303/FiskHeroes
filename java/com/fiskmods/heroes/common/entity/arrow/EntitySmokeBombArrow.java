package com.fiskmods.heroes.common.entity.arrow;

import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySmokeBombArrow extends EntityTrickArrow
{
    public EntitySmokeBombArrow(World world)
    {
        super(world);
    }

    public EntitySmokeBombArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntitySmokeBombArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntitySmokeBombArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        trigger();
        super.onImpact(mop);
    }

    @Override
    public boolean onCaught(EntityLivingBase entity)
    {
        if (super.onCaught(entity))
        {
            trigger();
            return true;
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte b)
    {
        if (b == 17 && worldObj.isRemote)
        {
            SHHelper.doSmokeExplosion(worldObj, posX, posY, posZ);
        }

        super.handleHealthUpdate(b);
    }

    public void trigger()
    {
        if (getArrowId() > 0)
        {
            if (!worldObj.isRemote)
            {
                worldObj.setEntityState(this, (byte) 17);
            }

            setArrowId(0);
        }
    }
}
