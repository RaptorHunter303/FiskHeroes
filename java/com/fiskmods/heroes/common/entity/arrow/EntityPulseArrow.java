package com.fiskmods.heroes.common.entity.arrow;

import com.fiskmods.heroes.common.data.world.SHMapData;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityPulseArrow extends EntityTrickArrow
{
    public EntityPulseArrow(World world)
    {
        super(world);
    }

    public EntityPulseArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityPulseArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityPulseArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected String getParticleName()
    {
        return "reddust";
    }

    @Override
    protected void spawnTrailingParticles()
    {
        if (rand.nextFloat() < 0.5)
        {
            float f = 0.05F;
            worldObj.spawnParticle(getParticleName(), posX + (rand.nextDouble() * 2 - 1) * f, posY + (rand.nextDouble() * 2 - 1) * f, posZ + (rand.nextDouble() * 2 - 1) * f, 0, 0, 0);
        }
    }

    @Override
    protected void onImpactEntity(MovingObjectPosition mop)
    {
    }

    @Override
    protected void onImpactBlock(MovingObjectPosition mop)
    {
        super.onImpactBlock(mop);

        if (!worldObj.isRemote)
        {
            Block block = worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);

//            if (block.shouldCheckWeakPower(worldObj, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit))
            {
                SHMapData.get(worldObj).power(worldObj, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit);

                worldObj.notifyBlockOfNeighborChange(mop.blockX, mop.blockY, mop.blockZ, block);
                worldObj.markAndNotifyBlock(mop.blockX, mop.blockY, mop.blockZ, null, block, block, 3);
            }
        }
    }
}
