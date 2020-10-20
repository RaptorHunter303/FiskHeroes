package com.fiskmods.heroes.common.entity.gadget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.tileentity.TileEntityIceLayer;
import com.fiskmods.heroes.util.TemperatureHelper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class EntityFreezeGrenade extends EntityThrowable
{
    public EntityFreezeGrenade(World world)
    {
        super(world);
        noClip = false;
    }

    public EntityFreezeGrenade(World world, EntityLivingBase entity)
    {
        super(world, entity);
        noClip = false;
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.05F;
    }

    @Override
    protected float func_70182_d()
    {
        return 1.25F;
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        if (mop.typeOfHit == MovingObjectType.BLOCK)
        {
            List<ChunkPosition> affectedBlockPositions = new ArrayList<>();
            HashSet hashset = new HashSet();
            float explosionSize = 3;
            float f = explosionSize;
            int x;
            int y;
            int z;
            double d5;
            double d6;
            double d7;
            int max = 16;

            for (x = 0; x < max; ++x)
            {
                for (y = 0; y < max; ++y)
                {
                    for (z = 0; z < max; ++z)
                    {
                        if (x == 0 || x == max - 1 || y == 0 || y == max - 1 || z == 0 || z == max - 1)
                        {
                            double d0 = x / (max - 1.0F) * 2.0F - 1.0F;
                            double d1 = y / (max - 1.0F) * 2.0F - 1.0F;
                            double d2 = z / (max - 1.0F) * 2.0F - 1.0F;
                            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                            d0 /= d3;
                            d1 /= d3;
                            d2 /= d3;
                            float f1 = explosionSize * (0.7F + rand.nextFloat() * 0.6F);
                            d5 = posX;
                            d6 = posY;
                            d7 = posZ;

                            for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F)
                            {
                                int j1 = MathHelper.floor_double(d5);
                                int k1 = MathHelper.floor_double(d6);
                                int l1 = MathHelper.floor_double(d7);
                                Block block = worldObj.getBlock(j1, k1, l1);

                                if (block.getMaterial() != Material.air && !block.isReplaceable(worldObj, j1, k1, l1) && block.isOpaqueCube() && block != ModBlocks.iceLayer)
                                {
                                    f1 = 0;
                                }

                                if (f1 > 0.0F)
                                {
                                    hashset.add(new ChunkPosition(j1, k1, l1));
                                }

                                d5 += d0 * f2;
                                d6 += d1 * f2;
                                d7 += d2 * f2;
                            }
                        }
                    }
                }
            }

            affectedBlockPositions.addAll(hashset);
            explosionSize *= 2.0F;
            x = MathHelper.floor_double(posX - explosionSize - 1.0D);
            y = MathHelper.floor_double(posX + explosionSize + 1.0D);
            z = MathHelper.floor_double(posY - explosionSize - 1.0D);
            int i2 = MathHelper.floor_double(posY + explosionSize + 1.0D);
            int l = MathHelper.floor_double(posZ - explosionSize - 1.0D);
            int j2 = MathHelper.floor_double(posZ + explosionSize + 1.0D);
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(getThrower(), AxisAlignedBB.getBoundingBox(x, z, l, y, i2, j2));
            Vec3 vec3 = Vec3.createVectorHelper(posX, posY, posZ);

            for (ChunkPosition pos : affectedBlockPositions)
            {
                int x1 = pos.chunkPosX;
                int y1 = pos.chunkPosY;
                int z1 = pos.chunkPosZ;

                tryPlaceIce(x1, y1 + 1, z1, 0);
                tryPlaceIce(x1, y1 - 1, z1, 1);
                tryPlaceIce(x1, y1, z1 + 1, 2);
                tryPlaceIce(x1, y1, z1 - 1, 3);
                tryPlaceIce(x1 + 1, y1, z1, 4);
                tryPlaceIce(x1 - 1, y1, z1, 5);
            }

            for (Object element : list)
            {
                Entity entity = (Entity) element;
                double d4 = entity.getDistance(posX, posY, posZ) / explosionSize;

                if (d4 <= 1.0D)
                {
                    d5 = entity.posX - posX;
                    d6 = entity.posY + entity.getEyeHeight() - posY;
                    d7 = entity.posZ - posZ;
                    double d9 = MathHelper.sqrt_double(d5 * d5 + d6 * d6 + d7 * d7);

                    if (d9 != 0.0D)
                    {
                        d5 /= d9;
                        d6 /= d9;
                        d7 /= d9;
                        double d10 = worldObj.getBlockDensity(vec3, entity.boundingBox);
                        double d11 = (1.0D - d4) * d10;

                        if (!(entity instanceof EntityHanging))
                        {
                            entity.attackEntityFrom(ModDamageSources.FREEZE.apply(getThrower()), (int) ((d11 * d11 + d11) / 2.0D * 8.0D * explosionSize + 1.0D));
                        }

                        if (entity instanceof EntityLivingBase)
                        {
                            float f1 = Math.round((d11 * d11 + d11) / 2.0D * 8.0D * explosionSize + 1.0D);
                            TemperatureHelper.setTemperature((EntityLivingBase) entity, TemperatureHelper.getTemperature((EntityLivingBase) entity) - f1);
                        }

                        double d8 = EnchantmentProtection.func_92092_a(entity, d11);
                        entity.motionX += d5 * d8;
                        entity.motionY += d6 * d8;
                        entity.motionZ += d7 * d8;
                    }
                }
            }

            playSound(SHSounds.ENTITY_FREEZEGRENADE_FREEZE.toString(), 1.5F, 0.9F + rand.nextFloat() * 0.3F);
            playSound("random.explode", 2.0F, 0.8F + rand.nextFloat() * 0.2F);

            explosionSize = f;
            Block block;
            Iterator iterator = affectedBlockPositions.iterator();

            if (worldObj.isRemote)
            {
                while (iterator.hasNext())
                {
                    ChunkPosition pos = (ChunkPosition) iterator.next();
                    x = pos.chunkPosX;
                    y = pos.chunkPosY;
                    z = pos.chunkPosZ;
                    block = worldObj.getBlock(x, y, z);

                    double d0 = x + rand.nextFloat();
                    double d1 = y + rand.nextFloat();
                    double d2 = z + rand.nextFloat();
                    double motX = d0 - posX;
                    double motY = d1 - posY;
                    double motZ = d2 - posZ;
                    double vel = MathHelper.sqrt_double(motX * motX + motY * motY + motZ * motZ);
                    motX /= vel;
                    motY /= vel;
                    motZ /= vel;
                    double motionMultiplier = 0.5D / (vel / explosionSize + 0.1D);
                    motionMultiplier *= rand.nextFloat() * rand.nextFloat() * 1 + 0.3F;
                    motX *= motionMultiplier;
                    motY *= motionMultiplier;
                    motZ *= motionMultiplier;
                    SHParticleType.FREEZE_SMOKE.spawn((d0 + posX) / 2.0D, (d1 + posY) / 2.0D, (d2 + posZ) / 2.0D, motX * 2, motY * 2, motZ * 2);
                }
            }

            setDead();
        }
    }

    public void tryPlaceIce(int x, int y, int z, int metadata)
    {
        int x1 = x;
        int y1 = y;
        int z1 = z;

        if (metadata == 0)
        {
            --y1;
        }
        else if (metadata == 1)
        {
            ++y1;
        }
        else if (metadata == 2)
        {
            --z1;
        }
        else if (metadata == 3)
        {
            ++z1;
        }
        else if (metadata == 4)
        {
            --x1;
        }
        else if (metadata == 5)
        {
            ++x1;
        }

        if (!worldObj.isRemote)
        {
            Block block = worldObj.getBlock(x, y, z);
            Block block1 = worldObj.getBlock(x1, y1, z1);

            boolean canPlaceLayer = true;
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x1, y1, z1, x1 + 1, y1 + 1, z1 + 1);
            List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(null, aabb);

            for (Entity entity : list)
            {
                if (entity instanceof EntityHanging)
                {
                    canPlaceLayer = false;
                    break;
                }
            }

            if (canPlaceLayer)
            {
                if (block1 == Blocks.water || block1 == Blocks.flowing_water)
                {
                    worldObj.setBlock(x1, y1, z1, ModBlocks.frostedIce);
                }
                else if (block1 == ModBlocks.iceLayer)
                {
                    TileEntityIceLayer tile = (TileEntityIceLayer) worldObj.getTileEntity(x1, y1, z1);

                    if (tile != null)
                    {
                        tile.setThickness(MathHelper.clamp_int(tile.thickness + rand.nextInt(8), 1, 64));
                    }
                }
                else if (!worldObj.isAirBlock(x, y, z) && !block1.getMaterial().isLiquid() && block != ModBlocks.iceLayer && block1.isReplaceable(worldObj, x1, y1, z1) && block.getMaterial().blocksMovement())
                {
                    worldObj.setBlock(x1, y1, z1, ModBlocks.iceLayer, metadata, 2);
                    TileEntityIceLayer tile = (TileEntityIceLayer) worldObj.getTileEntity(x1, y1, z1);

                    if (tile != null)
                    {
                        tile.thickness = Math.max(rand.nextInt(8), 1);
                    }
                }
            }
        }
    }
}
