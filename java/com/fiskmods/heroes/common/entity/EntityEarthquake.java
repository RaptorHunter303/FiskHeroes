package com.fiskmods.heroes.common.entity;

import java.util.List;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEarthquake extends EntityThrowable
{
    public EntityEarthquake(World world)
    {
        super(world);
        noClip = true;
    }

    public EntityEarthquake(World world, EntityLivingBase entity)
    {
        super(world, entity);
        noClip = true;
    }

    @Override
    public boolean isBurning()
    {
        return false;
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.0F;
    }

    @Override
    protected float func_70182_d()
    {
        return 0.0F;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (ticksExisted > 100)
        {
            setDead();
        }
        else if (ticksExisted % (rand.nextInt(5) + 1) == 0)
        {
            int radius = 20;
            List<EntityLivingBase> list = worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(posX - radius, posY - radius / 2, posZ - radius, posX + radius, posY + radius / 2, posZ + radius), IEntitySelector.selectAnything);

            for (int i = -radius; i <= radius; ++i)
            {
                for (int j = -radius; j <= radius; ++j)
                {
                    for (int k = -radius; k <= radius; ++k)
                    {
                        int x = MathHelper.floor_double(posX) + i;
                        int y = MathHelper.floor_double(posY) + j;
                        int z = MathHelper.floor_double(posZ) + k;

                        if (!worldObj.isAirBlock(x, y - 1, z) && worldObj.isAirBlock(x, y, z))
                        {
                            if (rand.nextInt(200) == 0)
                            {
                                worldObj.spawnParticle("largeexplode", x + rand.nextDouble(), y + rand.nextDouble(), z + rand.nextDouble(), 0, 0, 0);
                                worldObj.playSound(x + rand.nextDouble(), y + rand.nextDouble(), z + rand.nextDouble(), "random.explode", 0.5F, 1.0F - rand.nextFloat() * 0.5F, false);
                            }
                        }
                    }
                }
            }
            
            if (!list.isEmpty())
            {
                float dmg = Rule.DMG_EARTHQUAKE.getHero(getThrower());

                for (EntityLivingBase entity : list)
                {
                    if (SHHelper.hasEnabledModifier(entity, Ability.GEOKINESIS))
                    {
                        continue;
                    }

                    if (entity.onGround)
                    {
                        double amount = 1.0D;
                        entity.motionX += (Math.random() - 0.5D) * amount;
                        entity.motionY += Math.random() * amount * 0.5D;
                        entity.motionZ += (Math.random() - 0.5D) * amount;

                        entity.hurtResistantTime = 0;
                        entity.attackEntityFrom(ModDamageSources.TREMOR.apply(getThrower()), dmg);

                        // int r = 4;
                        //
                        // for (int i = -r; i <= r; ++i)
                        // {
                        // for (int j = -r; j <= r; ++j)
                        // {
                        // for (int k = -r; k <= r; ++k)
                        // {
                        // if (rand.nextInt(10) == 0)
                        // {
                        // Block block = worldObj.getBlock((int)posX + i, (int)posY + j, (int)posZ + k);
                        // int metadata = worldObj.getBlockMetadata((int)posX + i, (int)posY + j, (int)posZ + k);
                        // worldObj.playAuxSFX(2001, (int)posX + i, (int)posY + j + 1, (int)posZ + k, Block.getIdFromBlock(block) + (metadata << 12));
                        // }
                        // }
                        // }
                        // }
                    }
                }
            }
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
    }
}
