package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Hero.Permission;
import com.fiskmods.heroes.common.tileentity.TileEntityIceLayer;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.TemperatureHelper;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemColdGun extends ItemUntextured
{
    public ItemColdGun()
    {
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (SHData.AIMING_TIMER.get(player) >= 1 && SHHelper.hasPermission(player, Permission.USE_COLD_GUN))
        {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        }

        return stack;
    }

    @Override
    public void onUsingTick(ItemStack itemstack, EntityPlayer player, int count)
    {
        World world = player.worldObj;
        Hero hero;

        if (SHData.AIMING_TIMER.get(player) >= 1 && (hero = SHHelper.getHero(player)) != null && hero.hasPermission(player, Permission.USE_COLD_GUN))
        {
            double range = Rule.RANGE_COLDGUN.get(player, hero);
            MovingObjectPosition rayTrace = SHHelper.rayTrace(player, range, 5, 1);

            if (rayTrace != null)
            {
                if (rayTrace.typeOfHit == MovingObjectType.BLOCK)
                {
                    int x = rayTrace.blockX;
                    int y = rayTrace.blockY;
                    int z = rayTrace.blockZ;
                    int side = rayTrace.sideHit;

                    ForgeDirection dir = ForgeDirection.getOrientation(side);
                    Block block = player.worldObj.getBlock(x, y, z);

                    if (block instanceof BlockLiquid || block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(player.worldObj, x, y, z))
                    {
                        x += dir.offsetX;
                        y += dir.offsetY;
                        z += dir.offsetZ;
                    }
                    else if (block.isReplaceable(player.worldObj, x, y, z) && block != Blocks.vine)
                    {
                        side = 1;
                    }

                    if (Rule.GRIEF_COLDGUN.get(world, rayTrace.blockX, rayTrace.blockZ))
                    {
                        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
                        boolean canPlaceLayer = list.isEmpty();

                        if (canPlaceLayer && FiskServerUtils.canEntityEdit(player, rayTrace, itemstack) && world.getBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ) == ModBlocks.iceLayer)
                        {
                            TileEntityIceLayer tile = (TileEntityIceLayer) world.getTileEntity(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);

                            if (tile != null && tile.thickness < 64)
                            {
                                canPlaceLayer = false;
                                tile.setThickness(++tile.thickness);
                            }
                        }

                        if (canPlaceLayer && !player.worldObj.isRemote && player.canPlayerEdit(x, y, z, side, itemstack) && (world.isAirBlock(x, y, z) || world.getBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ) == ModBlocks.iceLayer && world.getBlockMetadata(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ) != side))
                        {
                            world.setBlock(x, y, z, ModBlocks.iceLayer, side, 3);
                        }
                    }
                }
                else if (rayTrace.entityHit != null)
                {
                    rayTrace.entityHit.attackEntityFrom(ModDamageSources.FREEZE.apply(player), Rule.DMG_COLDGUN.get(player, hero));

                    if (rayTrace.entityHit instanceof EntityLivingBase)
                    {
                        EntityLivingBase entity = (EntityLivingBase) rayTrace.entityHit;

                        if (TemperatureHelper.getCurrentBodyTemperature(entity) > 0)
                        {
                            TemperatureHelper.setTemperature(entity, TemperatureHelper.getTemperature(entity) - 0.1F);
                        }
                    }
                }
            }

            if (player.worldObj.isRemote)
            {
                double length = rayTrace != null && rayTrace.hitInfo instanceof Double ? (Double) rayTrace.hitInfo : range;
                Vec3 src = VectorHelper.getOffsetCoords(player, -0.325, -0.2, 0.8);
                Vec3 dest = VectorHelper.getOffsetCoords(player, 0, 0, length);
                length = src.distanceTo(dest);

                for (double point = 0; point <= length; point += 0.15)
                {
                    Vec3 vec3 = VectorHelper.interpolate(dest, src, point);
                    SHParticleType.FREEZE_RAY.spawn(vec3.xCoord, vec3.yCoord, vec3.zCoord, 0, 0, 0);
                }
            }
        }
        else
        {
            player.stopUsingItem();
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return true;
    }
}
