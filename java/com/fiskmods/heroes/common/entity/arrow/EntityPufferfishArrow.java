package com.fiskmods.heroes.common.entity.arrow;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityPufferfishArrow extends EntityTrickArrow
{
    public EntityPufferfishArrow(World world)
    {
        super(world);
    }

    public EntityPufferfishArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityPufferfishArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityPufferfishArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected DamageSource getDamageSource(Entity entity)
    {
        return ModDamageSources.causePufferfishDamage(this, entity);
    }

    @Override
    protected void handlePostDamageEffects(EntityLivingBase entityHit)
    {
        super.handlePostDamageEffects(entityHit);

        if (rand.nextFloat() < 0.33)
        {
            entityHit.addPotionEffect(new PotionEffect(Potion.poison.id, 80, 0));
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
                float f = 0.5F;
                motionX *= (1 - Math.abs(dir.offsetX) * 2) * f;
                motionY *= (1 - Math.abs(dir.offsetY) * 2) * f;
                motionZ *= (1 - Math.abs(dir.offsetZ) * 2) * f;
                arrowShake = 7;

                flag = false;
                float f1 = velocity * 0.125F;
                float f2 = velocity * 0.1F;

                for (int i = 0; i < 20; ++i)
                {
                    worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(Items.fish) + "_3", mop.hitVec.xCoord + (rand.nextDouble() * 2 - 1) * f1, mop.hitVec.yCoord + (rand.nextDouble() * 2 - 1) * f1, mop.hitVec.zCoord + (rand.nextDouble() * 2 - 1) * f1, (rand.nextDouble() * 2 - 1) * f2, (rand.nextDouble() * 2 - 1) * f2, (rand.nextDouble() * 2 - 1) * f2);
                }
            }
        }

        playSound(SHSounds.ENTITY_ARROW_PUFFERFISH_FLOP.toString(), 1, 1);

        if (flag)
        {
            super.onImpactBlock(mop);
        }
    }
}
