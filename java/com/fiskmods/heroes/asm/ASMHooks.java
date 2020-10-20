package com.fiskmods.heroes.asm;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.common.Pair;
import com.fiskmods.heroes.common.Tickrate;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.world.SHMapData;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.ItemHeroArmor;
import com.fiskmods.heroes.common.hero.LegacyHeroManager;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.world.ModDimensions;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class ASMHooks
{
    public static long getMiliSecondsPerTick()
    {
        return Tickrate.MILISECONDS_PER_TICK;
    }

    public static List selectEntitiesWithinAABB(EntityCreature entity, Class clazz, AxisAlignedBB aabb, IEntitySelector entitySelector)
    {
        World world = entity.worldObj;
        List<Entity> list = world.selectEntitiesWithinAABB(clazz, aabb, entitySelector);

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity1 = list.get(i);

            if (entity1 instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) entity1;
                Hero hero = SHHelper.getHero(player);

                if (hero != null && hero.hasEnabledModifier(player, Ability.INVISIBILITY))
                {
                    if (SHData.INVISIBLE.get(player) && player.getDistanceToEntity(entity) > 2)
                    {
                        list.remove(i);
                    }
                }
            }
        }

        return list;
    }

    public static List getAllowedPressureEntitiesWithinAABB(World world, Class clazz, AxisAlignedBB aabb)
    {
        List<Entity> list = world.getEntitiesWithinAABB(clazz, aabb);
        List<Entity> list1 = world.getEntitiesWithinAABBExcludingEntity(null, aabb);

        for (Entity entity : list1)
        {
            if (entity instanceof EntityTrickArrow)
            {
                list.add(entity);
            }
        }

        return list;
    }

    public static EntityPlayer getClosestVulnerablePlayer(Entity entity, double x, double y, double z, double radius)
    {
        World world = entity.worldObj;
        double d = -1.0D;
        EntityPlayer entityplayer = null;

        for (Object element : world.playerEntities)
        {
            EntityPlayer player = (EntityPlayer) element;

            if (!player.capabilities.disableDamage && player.isEntityAlive())
            {
                double d1 = player.getDistanceSq(x, y, z);
                double d2 = radius;

                if (player.isSneaking())
                {
                    d2 = radius * 0.800000011920929D;
                }

                if (player.isInvisible())
                {
                    float f = player.getArmorVisibility();

                    if (f < 0.1F)
                    {
                        f = 0.1F;
                    }

                    d2 *= 0.7F * f;
                }

                Hero hero = SHHelper.getHero(player);

                if (hero != null && hero.hasEnabledModifier(player, Ability.INVISIBILITY))
                {
                    if (SHData.INVISIBLE.get(player) && (player.getDistanceToEntity(entity) > 2 || SHData.INTANGIBLE.get(player) && (hero.hasEnabledModifier(player, Ability.INTANGIBILITY) && Ability.INTANGIBILITY.isActive(player) || hero.hasEnabledModifier(player, Ability.ABSOLUTE_INTANGIBILITY) && Ability.ABSOLUTE_INTANGIBILITY.isActive(player))))
                    {
                        continue;
                    }
                }

                if ((radius < 0.0D || d1 < d2 * d2) && (d == -1.0D || d1 < d))
                {
                    d = d1;
                    entityplayer = player;
                }
            }
        }

        return entityplayer;
    }

    public static float getDrawbackTime(EntityPlayer player)
    {
        return SHAttributes.BOW_DRAWBACK.get(player, 20);
    }

    public static int getBowIconTime(EntityPlayer player, int time)
    {
        return (int) (getDrawbackTime(player) * 20F / time);
    }

    public static float getEntityScale(Entity entity) // Convenience method for ASM
    {
        return SHData.SCALE.get(entity);
    }

    public static float getStrengthScale(Entity entity)
    {
        if (SHData.isTracking(entity))
        {
            float scale = SHData.SCALE.get(entity);
            return scale > 1 ? (float) Math.pow(scale, 1.0 / 3) : 1;
        }

        return 1;
    }

    public static float getModifiedEntityScale(Entity entity)
    {
        return Math.max(getEntityScale(entity), 1);
    }

    public static float getStepDistance(Entity entity, int dist)
    {
        return dist * getModifiedEntityScale(entity);
    }

    public static boolean shouldProduceSprintParticles(Entity entity)
    {
        return getEntityScale(entity) > 0.2F;
    }

    public static String getPotionParticleName(Entity entity, String baseName)
    {
        return baseName + "_" + SHData.SCALE.get(entity);
    }

    public static float getArmorVisibilityMultiplier(EntityPlayer player)
    {
        Hero hero = SHHelper.getHero(player);

        if (hero != null && hero.hasEnabledModifier(player, Ability.INVISIBILITY) && SHData.INVISIBLE.get(player))
        {
            return 0.0F;
        }

        return 1.0F;
    }

    public static int getArmSwingAnimationEnd(int result, EntityLivingBase entity)
    {
        if (SHData.isTracking(entity))
        {
            result *= getModifiedEntityScale(entity) / getStrengthScale(entity);
        }

        if (entity instanceof EntityPlayer && SpeedsterHelper.canPlayerRun((EntityPlayer) entity))
        {
            result /= 2;
        }

        return result;
    }

    public static int getHealRate(EntityPlayer player, int rate)
    {
        Hero hero = SHHelper.getHero(player);

        if (hero != null && hero.hasEnabledModifier(player, Ability.CELLULAR_REGENERATION))
        {
            return rate / 4;
        }

        return rate;
    }

    public static void addLiquidCollisionBoxesToList(BlockLiquid block, World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
    {
        AxisAlignedBB aabb1 = block.getCollisionBoundingBoxFromPool(world, x, y, z);

        if (aabb1 == null && block == Blocks.water && !(world.getBlock(x, y + 1, z) instanceof BlockLiquid) && entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;

            if (SpeedsterHelper.canPlayerRunOnWater(player))
            {
                double maxY = 1 - BlockLiquid.getLiquidHeightPercent(world.getBlockMetadata(x, y, z));
                aabb1 = AxisAlignedBB.getBoundingBox(x + block.getBlockBoundsMinX(), y + maxY, z + block.getBlockBoundsMinZ(), x + block.getBlockBoundsMaxX(), y + maxY, z + block.getBlockBoundsMaxZ());
            }
        }

        if (aabb1 != null && aabb.intersectsWith(aabb1))
        {
            list.add(aabb1);
        }
    }

    public static boolean ignorePaneCollisionBox(BlockPane block, Entity entity)
    {
        return block == Blocks.iron_bars && SHData.isTracking(entity) && SHData.SHADOWFORM.get(entity);
    }

    public static void noClipCollide(Entity entity, double offsetX, double offsetY, double offsetZ)
    {
        boolean flag = false;

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            Hero hero = SHHelper.getHero(player);

            if (hero != null && SHData.INTANGIBLE.get(player) && (hero.hasEnabledModifier(player, Ability.INTANGIBILITY) && Ability.INTANGIBILITY.isActive(player) || hero.hasEnabledModifier(player, Ability.ABSOLUTE_INTANGIBILITY) && Ability.ABSOLUTE_INTANGIBILITY.isActive(player)))
            {
                flag = true;
            }
        }

        if (!flag)
        {
            entity.boundingBox.offset(offsetX, offsetY, offsetZ);
            entity.posX = (entity.boundingBox.minX + entity.boundingBox.maxX) / 2.0D;
            entity.posY = entity.boundingBox.minY + entity.yOffset - entity.ySize;
            entity.posZ = (entity.boundingBox.minZ + entity.boundingBox.maxZ) / 2.0D;
            return;
        }

        int nextStepDistance = SHReflection.nextStepDistanceField.get(entity);

        entity.worldObj.theProfiler.startSection("move");
        entity.ySize *= 0.4F;
        double d3 = entity.posX;
        double d4 = entity.posY;
        double d5 = entity.posZ;
        double d6 = offsetX;
        double d7 = offsetY;
        double d8 = offsetZ;
        AxisAlignedBB axisalignedbb = entity.boundingBox.copy();
        boolean flag1 = entity.onGround && entity.isSneaking() && entity instanceof EntityPlayer;

        if (flag1)
        {
            double d9;

            for (d9 = 0.05D; offsetX != 0.0D && getCollidingBoundingBoxes(entity, entity.boundingBox.getOffsetBoundingBox(offsetX, -1.0D, 0.0D)).isEmpty(); d6 = offsetX)
            {
                if (offsetX < d9 && offsetX >= -d9)
                {
                    offsetX = 0.0D;
                }
                else if (offsetX > 0.0D)
                {
                    offsetX -= d9;
                }
                else
                {
                    offsetX += d9;
                }
            }

            for (; offsetZ != 0.0D && getCollidingBoundingBoxes(entity, entity.boundingBox.getOffsetBoundingBox(0.0D, -1.0D, offsetZ)).isEmpty(); d8 = offsetZ)
            {
                if (offsetZ < d9 && offsetZ >= -d9)
                {
                    offsetZ = 0.0D;
                }
                else if (offsetZ > 0.0D)
                {
                    offsetZ -= d9;
                }
                else
                {
                    offsetZ += d9;
                }
            }

            while (offsetX != 0.0D && offsetZ != 0.0D && getCollidingBoundingBoxes(entity, entity.boundingBox.getOffsetBoundingBox(offsetX, -1.0D, offsetZ)).isEmpty())
            {
                if (offsetX < d9 && offsetX >= -d9)
                {
                    offsetX = 0.0D;
                }
                else if (offsetX > 0.0D)
                {
                    offsetX -= d9;
                }
                else
                {
                    offsetX += d9;
                }

                if (offsetZ < d9 && offsetZ >= -d9)
                {
                    offsetZ = 0.0D;
                }
                else if (offsetZ > 0.0D)
                {
                    offsetZ -= d9;
                }
                else
                {
                    offsetZ += d9;
                }

                d6 = offsetX;
                d8 = offsetZ;
            }
        }

        List list = getCollidingBoundingBoxes(entity, entity.boundingBox.addCoord(offsetX, offsetY, offsetZ));

        for (Object aList : list)
        {
            offsetY = ((AxisAlignedBB) aList).calculateYOffset(entity.boundingBox, offsetY);
        }

        entity.boundingBox.offset(0.0D, offsetY, 0.0D);

        if (!entity.field_70135_K && d7 != offsetY)
        {
            offsetZ = 0.0D;
            offsetY = 0.0D;
            offsetX = 0.0D;
        }

        boolean flag2 = entity.onGround || d7 != offsetY && d7 < 0.0D;
        int j;

        for (j = 0; j < list.size(); ++j)
        {
            offsetX = ((AxisAlignedBB) list.get(j)).calculateXOffset(entity.boundingBox, offsetX);
        }

        entity.boundingBox.offset(offsetX, 0.0D, 0.0D);

        if (!entity.field_70135_K && d6 != offsetX)
        {
            offsetZ = 0.0D;
            offsetY = 0.0D;
            offsetX = 0.0D;
        }

        for (j = 0; j < list.size(); ++j)
        {
            offsetZ = ((AxisAlignedBB) list.get(j)).calculateZOffset(entity.boundingBox, offsetZ);
        }

        entity.boundingBox.offset(0.0D, 0.0D, offsetZ);

        if (!entity.field_70135_K && d8 != offsetZ)
        {
            offsetZ = 0.0D;
            offsetY = 0.0D;
            offsetX = 0.0D;
        }

        double d10;
        double d11;
        int k;
        double d12;

        if (entity.stepHeight > 0.0F && flag2 && (flag1 || entity.ySize < 0.05F) && (d6 != offsetX || d8 != offsetZ))
        {
            d12 = offsetX;
            d10 = offsetY;
            d11 = offsetZ;
            offsetX = d6;
            offsetY = entity.stepHeight;
            offsetZ = d8;
            AxisAlignedBB axisalignedbb1 = entity.boundingBox.copy();
            entity.boundingBox.setBB(axisalignedbb);
            list = getCollidingBoundingBoxes(entity, entity.boundingBox.addCoord(d6, offsetY, d8));

            for (k = 0; k < list.size(); ++k)
            {
                offsetY = ((AxisAlignedBB) list.get(k)).calculateYOffset(entity.boundingBox, offsetY);
            }

            entity.boundingBox.offset(0.0D, offsetY, 0.0D);

            if (!entity.field_70135_K && d7 != offsetY)
            {
                offsetZ = 0.0D;
                offsetY = 0.0D;
                offsetX = 0.0D;
            }

            for (k = 0; k < list.size(); ++k)
            {
                offsetX = ((AxisAlignedBB) list.get(k)).calculateXOffset(entity.boundingBox, offsetX);
            }

            entity.boundingBox.offset(offsetX, 0.0D, 0.0D);

            if (!entity.field_70135_K && d6 != offsetX)
            {
                offsetZ = 0.0D;
                offsetY = 0.0D;
                offsetX = 0.0D;
            }

            for (k = 0; k < list.size(); ++k)
            {
                offsetZ = ((AxisAlignedBB) list.get(k)).calculateZOffset(entity.boundingBox, offsetZ);
            }

            entity.boundingBox.offset(0.0D, 0.0D, offsetZ);

            if (!entity.field_70135_K && d8 != offsetZ)
            {
                offsetZ = 0.0D;
                offsetY = 0.0D;
                offsetX = 0.0D;
            }

            if (!entity.field_70135_K && d7 != offsetY)
            {
                offsetZ = 0.0D;
                offsetY = 0.0D;
                offsetX = 0.0D;
            }
            else
            {
                offsetY = -entity.stepHeight;

                for (k = 0; k < list.size(); ++k)
                {
                    offsetY = ((AxisAlignedBB) list.get(k)).calculateYOffset(entity.boundingBox, offsetY);
                }

                entity.boundingBox.offset(0.0D, offsetY, 0.0D);
            }

            if (d12 * d12 + d11 * d11 >= offsetX * offsetX + offsetZ * offsetZ)
            {
                offsetX = d12;
                offsetY = d10;
                offsetZ = d11;
                entity.boundingBox.setBB(axisalignedbb1);
            }
        }

        entity.worldObj.theProfiler.endSection();
        entity.worldObj.theProfiler.startSection("rest");
        entity.posX = (entity.boundingBox.minX + entity.boundingBox.maxX) / 2.0D;
        entity.posY = entity.boundingBox.minY + entity.yOffset - entity.ySize;
        entity.posZ = (entity.boundingBox.minZ + entity.boundingBox.maxZ) / 2.0D;
        entity.isCollidedHorizontally = d6 != offsetX || d8 != offsetZ;
        entity.isCollidedVertically = d7 != offsetY;
        entity.onGround = d7 != offsetY && d7 < 0.0D;
        entity.isCollided = entity.isCollidedHorizontally || entity.isCollidedVertically;
        // SHReflection.invokeMethod(SHReflection.updateFallStateMethod, entity, offsetY, entity.onGround);

        if (d6 != offsetX)
        {
            entity.motionX = 0.0D;
        }

        if (d7 != offsetY)
        {
            entity.motionY = 0.0D;
        }

        if (d8 != offsetZ)
        {
            entity.motionZ = 0.0D;
        }

        d12 = entity.posX - d3;
        d10 = entity.posY - d4;
        d11 = entity.posZ - d5;

        if (SHReflection.canTriggerWalking(entity) && !flag1 && entity.ridingEntity == null)
        {
            int j1 = MathHelper.floor_double(entity.posX);
            k = MathHelper.floor_double(entity.posY - 0.20000000298023224D - entity.yOffset);
            int l = MathHelper.floor_double(entity.posZ);
            Block block = entity.worldObj.getBlock(j1, k, l);
            int i1 = entity.worldObj.getBlock(j1, k - 1, l).getRenderType();

            if (i1 == 11 || i1 == 32 || i1 == 21)
            {
                block = entity.worldObj.getBlock(j1, k - 1, l);
            }

            if (block != Blocks.ladder)
            {
                d10 = 0.0D;
            }

            entity.distanceWalkedModified = (float) (entity.distanceWalkedModified + MathHelper.sqrt_double(d12 * d12 + d11 * d11) * 0.6D);
            entity.distanceWalkedOnStepModified = (float) (entity.distanceWalkedOnStepModified + MathHelper.sqrt_double(d12 * d12 + d10 * d10 + d11 * d11) * 0.6D);

            if (entity.distanceWalkedOnStepModified > nextStepDistance && block.getMaterial() != Material.air)
            {
                nextStepDistance = (int) entity.distanceWalkedOnStepModified + 1;
            }
        }

        entity.worldObj.theProfiler.endSection();
        SHReflection.nextStepDistanceField.set(entity, nextStepDistance);
    }

    private static List getCollidingBoundingBoxes(Entity entity, AxisAlignedBB aabb)
    {
        World world = entity.worldObj;
        List<AxisAlignedBB> collidingBoundingBoxes = new ArrayList<>();

        int i = MathHelper.floor_double(aabb.minX);
        int j = MathHelper.floor_double(aabb.maxX + 1.0D);
        int k = MathHelper.floor_double(aabb.minY);
        int l = MathHelper.floor_double(aabb.maxY + 1.0D);
        int i1 = MathHelper.floor_double(aabb.minZ);
        int j1 = MathHelper.floor_double(aabb.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = i1; l1 < j1; ++l1)
            {
                if (world.blockExists(k1, 64, l1))
                {
                    for (int i2 = k - 1; i2 < l; ++i2)
                    {
                        Block block;

                        if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000)
                        {
                            block = world.getBlock(k1, i2, l1);
                        }
                        else
                        {
                            block = Blocks.stone;
                        }

                        if (!SHConfig.get().canPhase(block))
                        {
                            block.addCollisionBoxesToList(world, k1, i2, l1, aabb, collidingBoundingBoxes, entity);
                        }
                    }
                }
            }
        }

        return collidingBoundingBoxes;
    }

    public static boolean moveEntityWithHeading(EntityLivingBase entity, float strafe, float forward)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;

            if (player.isClientWorld() && !player.isInWater() && !player.handleLavaMovement() && player.worldObj.provider.dimensionId != ModDimensions.QUANTUM_REALM_ID && SHData.GLIDING.get(player))
            {
                Hero hero = SHHelper.getHero(player);
                boolean flight = hero != null && hero.hasEnabledModifier(entity, Ability.GLIDING_FLIGHT);

                if (player.motionY > -0.5D)
                {
                    player.fallDistance = 1.0F;
                }

                Vec3 vec3d = player.getLookVec();
                float f = player.rotationPitch * 0.01745329F;
                float boost = 1 + forward * Rule.BOOST_GLIDEFLIGHT.get(entity, hero);
                double d6 = Math.sqrt(vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord);
                double d8 = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ) * (flight ? Math.max(1, boost) : 1);
                double d1 = vec3d.lengthVector();
                float f4 = MathHelper.cos(f);
                f4 = (float) (f4 * f4 * Math.min(1.0D, d1 / 0.4D));
                player.motionY += -0.08D + f4 * 0.06D;

                d8 = Math.min(d8, Math.max(2, boost) * 1.25);

                if (player.motionY < 0.0D && d6 > 0.0D)
                {
                    double d2 = player.motionY * -0.1D * f4;
                    player.motionY += d2;
                    player.motionX += vec3d.xCoord * d2 / d6;
                    player.motionZ += vec3d.zCoord * d2 / d6;
                }

                if (f < 0.0F)
                {
                    double d9 = d8 * -MathHelper.sin(f) * 0.04D;
                    player.motionY += d9 * 3.2D;
                    player.motionX -= vec3d.xCoord * d9 / d6;
                    player.motionZ -= vec3d.zCoord * d9 / d6;
                }

                if (d6 > 0.0D)
                {
                    player.motionX += (vec3d.xCoord / d6 * d8 - player.motionX) * 0.1D;
                    player.motionZ += (vec3d.zCoord / d6 * d8 - player.motionZ) * 0.1D;
                }

                player.motionX *= 0.9900000095367432D;
                player.motionY *= 0.9800000190734863D;
                player.motionZ *= 0.9900000095367432D;
                player.moveEntity(player.motionX, player.motionY, player.motionZ);

                if (player.isCollidedHorizontally && !player.worldObj.isRemote)
                {
                    double d10 = Math.sqrt(player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ);
                    double d3 = d8 - d10;
                    float f5 = (float) (d3 * 10.0D - 3.0D);

                    if (f5 > 0.0F)
                    {
                        player.playSound((int) f5 > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small", 1.0F, 1.0F);
                        player.attackEntityFrom(ModDamageSources.FLY_INTO_WALL, f5);
                    }
                }

                if (flight)
                {
                    AxisAlignedBB aabb = player.boundingBox.copy().expand(1, 0, 1);
                    List<EntityLivingBase> list = player.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, aabb, IEntitySelector.selectAnything);

                    if (!player.worldObj.isRemote)
                    {
                        for (EntityLivingBase entity1 : list)
                        {
                            if (entity1 != player)
                            {
                                double d10 = Math.sqrt(player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ);
                                float f5 = (float) (d10 * 10.0D);

                                if (f5 > 2.0F)
                                {
                                    entity1.attackEntityFrom(ModDamageSources.COLLISION.apply(player), f5 - 2);
                                }
                            }
                        }
                    }
                }

                player.prevLimbSwingAmount = player.limbSwingAmount;
                double d9 = player.posX - player.prevPosX;
                double d10 = player.posZ - player.prevPosZ;
                float f6 = MathHelper.sqrt_double(d9 * d9 + d10 * d10) * 4.0F;

                if (f6 > 1.0F)
                {
                    f6 = 1.0F;
                }

                player.limbSwingAmount += (f6 - player.limbSwingAmount) * 0.4F;
                player.limbSwing += player.limbSwingAmount;

                return true;
            }
        }

        return false;
    }

    public static double getScaledSneakOffset(Entity entity, double d)
    {
        return d * SHData.SCALE.get(entity);
    }

    public static int getIndirectPowerLevelTo(int result, World world, Block block, int x, int y, int z)
    {
        return result == 15 ? result : Math.max(result, SHMapData.get(world).getPower(world, x, y, z));
    }

    public static boolean isBlockIndirectlyGettingPowered(boolean result, World world, int x, int y, int z)
    {
        return result || SHMapData.get(world).getPower(world, x, y, z) > 0;
    }

    public static void readFromNBT(ItemStack stack, NBTTagCompound nbt)
    {
        if (stack.getItem() == null)
        {
            Pair<String, Integer> p = LegacyHeroManager.fromId(nbt.getShort("id"));

            if (p != null)
            {
                if (p.getKey().equals("fiskheroes:spodermen"))
                {
                    if (p.getValue() == 1)
                    {
                        stack.func_150996_a(ModItems.exclusivityToken);
                        stack.setItemDamage(0);
                    }

                    return;
                }

                stack.func_150996_a(ModItems.heroArmor[p.getValue()]);
                nbt.setShort("id", (short) Item.getIdFromItem(stack.getItem()));

                if (!stack.hasTagCompound())
                {
                    stack.setTagCompound(new NBTTagCompound());
                }

                if (!stack.getTagCompound().hasKey(ItemHeroArmor.TAG_HERO, NBT.TAG_STRING))
                {
                    stack.getTagCompound().setString(ItemHeroArmor.TAG_HERO, p.getKey());
                }
            }
        }
        else if (stack.getItem() instanceof ItemHeroArmor && stack.hasTagCompound() && stack.getTagCompound().hasKey(ItemHeroArmor.TAG_HERO, NBT.TAG_STRING))
        {
            String s = stack.getTagCompound().getString(ItemHeroArmor.TAG_HERO);
            String s1 = Hero.REGISTRY.getRemap(s);

            if (!s.equals(s1) && !StringUtils.isNullOrEmpty(s1))
            {
                stack.getTagCompound().setString(ItemHeroArmor.TAG_HERO, s1);
            }
        }
    }

    public static double getReachDistanceSq(NetHandlerPlayServer handler, double dist)
    {
        float f = SHHelper.getReachDistance(handler.playerEntity, MathHelper.sqrt_double(dist));
        return dist * f * f;
    }
}
