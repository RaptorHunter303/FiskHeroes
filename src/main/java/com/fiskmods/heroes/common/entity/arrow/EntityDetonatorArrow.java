package com.fiskmods.heroes.common.entity.arrow;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityDetonatorArrow extends EntityTrickArrow
{
    public float grappleTimer;
    public float prevGrappleTimer;

    public EntityDetonatorArrow(World world)
    {
        super(world);
    }

    public EntityDetonatorArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityDetonatorArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityDetonatorArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        prevGrappleTimer = grappleTimer;

        if (inGround)
        {
            if (grappleTimer < 1)
            {
                grappleTimer = Math.min(grappleTimer + 1F / 5, 1);
            }

            if (inTile != null && worldObj.isBlockIndirectlyGettingPowered(xTile, yTile, zTile))
            {
                setDead();

                if (!worldObj.isRemote)
                {
                    worldObj.createExplosion(this, posX, posY, posZ, 4, true);
                }
            }
        }
    }

    @Override
    protected int getLifespan()
    {
        return 6000;
    }

    @Override
    protected void onImpactEntity(MovingObjectPosition mop)
    {
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player)
    {
        if (player == getShooter())
        {
            super.onCollideWithPlayer(player);
        }
    }
}
