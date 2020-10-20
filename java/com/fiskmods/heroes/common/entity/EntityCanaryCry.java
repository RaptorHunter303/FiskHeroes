package com.fiskmods.heroes.common.entity;

import java.util.List;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityCanaryCry extends EntityThrowable
{
    public EntityCanaryCry(World world)
    {
        super(world);
    }

    public EntityCanaryCry(World world, EntityLivingBase entity)
    {
        super(world, entity);
        setSize(0.25F, 0.25F);
        setLocationAndAngles(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, entity.rotationYaw, entity.rotationPitch);
        posY -= 0.2D;
        ignoreFrustumCheck = true;
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
        float f = 0.4F;
        motionX = -MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * f;
        motionZ = MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * f;
        motionY = -MathHelper.sin((rotationPitch + func_70183_g()) / 180.0F * (float) Math.PI) * f;
        setThrowableHeading(motionX, motionY, motionZ, func_70182_d(), 1.0F);
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
        return 1.0F;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        float radius = getRadius(0);

        if (ticksExisted < getMaxTicksExisted() - 4)
        {
            AxisAlignedBB aabb = boundingBox.expand(radius, radius, radius);
            List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(getThrower(), aabb);

            if (!worldObj.isRemote)
            {
                int minX = MathHelper.floor_double(aabb.minX);
                int maxX = MathHelper.floor_double(aabb.maxX + 1);
                int minY = MathHelper.floor_double(aabb.minY);
                int maxY = MathHelper.floor_double(aabb.maxY + 1);
                int minZ = MathHelper.floor_double(aabb.minZ);
                int maxZ = MathHelper.floor_double(aabb.maxZ + 1);

                if (worldObj.checkChunksExist(minX, minY, minZ, maxX, maxY, maxZ))
                {
                    for (int x = minX; x < maxX; ++x)
                    {
                        for (int y = minY; y < maxY; ++y)
                        {
                            for (int z = minZ; z < maxZ; ++z)
                            {
                                Block block = worldObj.getBlock(x, y, z);

                                if (block.getMaterial() == Material.glass)
                                {
                                    if (getThrower() instanceof EntityPlayer && !((EntityPlayer) getThrower()).canPlayerEdit(x, y, z, 0, null) || !Rule.GRIEF_CANARYCRY.get(worldObj, x, z))
                                    {
                                        continue;
                                    }

                                    if (rand.nextInt(10 * MathHelper.ceiling_double_int(getDistance(x + 0.5, y + 0.5, z + 0.5))) == 0)
                                    {
                                        worldObj.func_147480_a(x, y, z, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (!list.isEmpty())
            {
                for (Entity entity : list)
                {
                    if (entity instanceof EntityThrownShield)
                    {
                        EntityThrownShield shield = (EntityThrownShield) entity;

                        if (!worldObj.isRemote)
                        {
                            EntityItem entityitem = new EntityItem(worldObj);
                            entityitem.setLocationAndAngles(shield.posX, shield.posY, shield.posZ, 0.0F, 0.0F);
                            entityitem.setEntityItemStack(shield.getShieldItem());

                            worldObj.spawnEntityInWorld(entityitem);
                        }

                        shield.setDead();
                    }

                    if (entity instanceof EntityIcicle)
                    {
                        ((EntityIcicle) entity).shatter();
                    }
                }
                
                if (!worldObj.isRemote)
                {
                    Hero hero = getThrower() != null ? SHHelper.getHero(getThrower()) : null;
                    float dmgMin = Rule.DMG_CANARYCRY_MIN.get(getThrower(), hero);
                    float dmgMax = Rule.DMG_CANARYCRY_MAX.get(getThrower(), hero);
                    float knockback = Rule.KNOCKBACK_CANARYCRY.get(getThrower(), hero);

                    for (Entity entity : list)
                    {
                        if (!(entity instanceof EntityCanaryCry))
                        {
                            boolean flag = false;

                            if (entity instanceof EntityLivingBase)
                            {
                                if (entity != getThrower())
                                {
                                    entity.attackEntityFrom(ModDamageSources.SOUND.apply(getThrower()), FiskServerUtils.interpolate(dmgMin, dmgMax, (float) ticksExisted / getMaxTicksExisted()));
                                    flag = true;
                                }
                            }
                            else
                            {
                                flag = true;
                            }

                            if (flag)
                            {
                                float mass = entity.width * entity.width * entity.height;
                                float f = knockback / MathHelper.sqrt_float(mass) * (1 - (float) ticksExisted / getMaxTicksExisted());

                                entity.motionX += (posX - prevPosX) * f;
                                entity.motionY += (posY - prevPosY) * f;
                                entity.motionZ += (posZ - prevPosZ) * f;
                            }
                        }
                    }
                }
            }
        }

        if (ticksExisted >= getMaxTicksExisted())
        {
            setDead();
        }
    }

    public float getRadius(float partialTicks)
    {
        float f = ticksExisted + partialTicks;
        return Math.max(f / (4 + f * f / 20) - 0.25F, 0.125F);
    }

    public float getOpacity(float partialTicks)
    {
        float ticks = ticksExisted + partialTicks;
        int max = getMaxTicksExisted();
        return 0.2F * (1 - ticks / max);
    }

    public int getMaxTicksExisted()
    {
        return 15;
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        if (mop.entityHit == null)
        {
            setDead();
        }
    }
}
