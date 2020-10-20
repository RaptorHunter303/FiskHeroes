package com.fiskmods.heroes.common.entity.arrow;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityFireChargeArrow extends EntityTrickArrow
{
    public EntityFireChargeArrow(World world)
    {
        super(world);
    }

    public EntityFireChargeArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityFireChargeArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityFireChargeArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected String getParticleName()
    {
        return "flame";
    }

    @Override
    protected void spawnTrailingParticles()
    {
        float f = 0.1F;

        for (int i = 0; i < 2; ++i)
        {
            worldObj.spawnParticle(getParticleName(), posX, posY, posZ, (rand.nextDouble() * 2 - 1) * f, (rand.nextDouble() * 2 - 1) * f, (rand.nextDouble() * 2 - 1) * f);
        }
    }

    @Override
    public boolean isBurning()
    {
        return true;
    }

    @Override
    protected boolean isBurningInternal()
    {
        return false;
    }

    @Override
    protected void handlePostDamageEffects(EntityLivingBase entityHit)
    {
        SHHelper.ignite(entityHit, SHConstants.IGNITE_FIRE_ARROW);
        super.handlePostDamageEffects(entityHit);
    }

    @Override
    protected void onImpactBlock(MovingObjectPosition mop)
    {
        super.onImpactBlock(mop);

        if (getArrowId() > 0)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
            int x = mop.blockX + dir.offsetX;
            int y = mop.blockY + dir.offsetY;
            int z = mop.blockZ + dir.offsetZ;

            if (Rule.GRIEF_FIRECHARGEARROW.get(worldObj, x, z) && worldObj.isAirBlock(x, y, z) && FiskServerUtils.canEntityEdit(getShooter(), x, y, z, mop.sideHit, getArrowItem()))
            {
                if (!worldObj.isRemote)
                {
                    worldObj.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "fire.ignite", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
                    worldObj.setBlock(x, y, z, Blocks.fire);
                }

                setArrowId(0);
            }
        }
    }
}
