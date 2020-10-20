package com.fiskmods.heroes.common.entity.gadget;

import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySmokePellet extends EntityThrowable
{
    public EntitySmokePellet(World world)
    {
        super(world);
        noClip = false;
    }

    public EntitySmokePellet(World world, EntityLivingBase entity)
    {
        super(world, entity);
        noClip = false;
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.025F;
    }

    @Override
    protected float func_70182_d()
    {
        return 1.25F;
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        if (!worldObj.isRemote)
        {
            worldObj.setEntityState(this, (byte) 17);
            setDead();
        }
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
}
