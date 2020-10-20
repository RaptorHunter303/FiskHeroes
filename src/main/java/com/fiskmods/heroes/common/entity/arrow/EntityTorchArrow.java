package com.fiskmods.heroes.common.entity.arrow;

import com.fiskmods.heroes.util.FiskServerUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityTorchArrow extends EntityTrickArrow
{
    public EntityTorchArrow(World world)
    {
        super(world);
    }

    public EntityTorchArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityTorchArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityTorchArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected String getParticleName()
    {
        return "smoke";
    }

    @Override
    protected void spawnTrailingParticles()
    {
        float f = 0.03F;

        if (rand.nextFloat() < 0.5)
        {
            worldObj.spawnParticle(getParticleName(), posX, posY, posZ, (rand.nextDouble() * 2 - 1) * f, (rand.nextDouble() * 2 - 1) * f, (rand.nextDouble() * 2 - 1) * f);
        }
        else
        {
            worldObj.spawnParticle("flame", posX, posY, posZ, (rand.nextDouble() * 2 - 1) * f, (rand.nextDouble() * 2 - 1) * f, (rand.nextDouble() * 2 - 1) * f);
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

        if (getArrowId() > 0)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
            boolean flag = false;
            boolean flag1 = false;
            int x = mop.blockX;
            int y = mop.blockY;
            int z = mop.blockZ;

            if (worldObj.getBlock(x, y, z) == Blocks.pumpkin)
            {
                flag = true;
                flag1 = true;
            }
            else if (!worldObj.isAirBlock(x, y, z))
            {
                Block block = worldObj.getBlock(x, y, z);

                if (block == Blocks.snow_layer && (worldObj.getBlockMetadata(x, y, z) & 7) < 1 || block.isReplaceable(worldObj, x, y, z))
                {
                    flag = true;
                }
                else
                {
                    x += dir.offsetX;
                    y += dir.offsetY;
                    z += dir.offsetZ;
                    block = worldObj.getBlock(x, y, z);
                    flag = worldObj.isAirBlock(x, y, z) || block == Blocks.snow_layer && (worldObj.getBlockMetadata(x, y, z) & 7) < 1 || block.isReplaceable(worldObj, x, y, z);
                }
            }

            if (flag && FiskServerUtils.canEntityEdit(getShooter(), x, y, z, mop.sideHit, getArrowItem()))
            {
                if (!worldObj.isRemote)
                {
                    Block block = Blocks.torch;
                    int metadata = 0;

                    if (flag1)
                    {
                        block = Blocks.lit_pumpkin;
                        metadata = worldObj.getBlockMetadata(x, y, z);
                    }

                    worldObj.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.func_150496_b(), (block.stepSound.getVolume() + 1) / 2, block.stepSound.getPitch() * 0.8F);
                    worldObj.setBlock(x, y, z, block, metadata, 2);
                }

                setArrowId(0);
            }
        }
    }
}
