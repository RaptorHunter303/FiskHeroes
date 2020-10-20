package com.fiskmods.heroes.common.entity.arrow;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntitySlimeArrow extends EntityTrickArrow
{
    public EntitySlimeArrow(World world)
    {
        super(world);
    }

    public EntitySlimeArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntitySlimeArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntitySlimeArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected String getParticleName()
    {
        return "iconcrack_" + Item.getIdFromItem(Items.slime_ball);
    }

    @Override
    protected void spawnTrailingParticles()
    {
        float f = 0.1F;

        if (rand.nextFloat() < MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ))
        {
            worldObj.spawnParticle(getParticleName(), posX, posY, posZ, (rand.nextDouble() * 2 - 1) * f, (rand.nextDouble() * 2 - 1) * f, (rand.nextDouble() * 2 - 1) * f);
        }
    }

    @Override
    protected void handlePostDamageEffects(EntityLivingBase entityHit)
    {
        super.handlePostDamageEffects(entityHit);

        if (rand.nextFloat() < 0.75)
        {
            entityHit.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 0));
        }
    }

    @Override
    protected void onImpactBlock(MovingObjectPosition mop)
    {
        float velocity = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        boolean flag = true;

        if (velocity > 0.25F)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
            Block block = worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);

            if (block.getMaterial() != Material.air)
            {
                block.onEntityCollidedWithBlock(worldObj, xTile, yTile, zTile, this);
            }

            if (block.getCollisionBoundingBoxFromPool(worldObj, mop.blockX, mop.blockY, mop.blockZ) != null && worldObj.isAirBlock(mop.blockX + dir.offsetX, mop.blockY + dir.offsetY, mop.blockZ + dir.offsetZ))
            {
                float f = 0.6F;
                motionX *= (1 - Math.abs(dir.offsetX) * 2) * f;
                motionY *= (1 - Math.abs(dir.offsetY) * 2) * f;
                motionZ *= (1 - Math.abs(dir.offsetZ) * 2) * f;
                arrowShake = 7;

                flag = false;
                float f1 = velocity * 0.125F;
                float f2 = velocity * 0.1F;

                for (int i = 0; i < 20; ++i)
                {
                    worldObj.spawnParticle(getParticleName(), mop.hitVec.xCoord + (rand.nextDouble() * 2 - 1) * f1, mop.hitVec.yCoord + (rand.nextDouble() * 2 - 1) * f1, mop.hitVec.zCoord + (rand.nextDouble() * 2 - 1) * f1, (rand.nextDouble() * 2 - 1) * f2, (rand.nextDouble() * 2 - 1) * f2, (rand.nextDouble() * 2 - 1) * f2);
                }
            }
        }

        playSound("mob.slime.small", 1, 1);

        if (flag)
        {
            super.onImpactBlock(mop);
        }
    }
}
