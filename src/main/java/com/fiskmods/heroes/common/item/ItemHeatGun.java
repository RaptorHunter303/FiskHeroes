package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Hero.Permission;
import com.fiskmods.heroes.common.hero.modifier.WeaknessMetalSkin.HeatSource;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemHeatGun extends ItemUntextured
{
    public ItemHeatGun()
    {
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (SHData.AIMING_TIMER.get(player) >= 1 && SHHelper.hasPermission(player, Permission.USE_HEAT_GUN))
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

        if (SHData.AIMING_TIMER.get(player) >= 1 && (hero = SHHelper.getHero(player)) != null && hero.hasPermission(player, Permission.USE_HEAT_GUN))
        {
            double range = Rule.RANGE_HEATGUN.get(player, hero);
            MovingObjectPosition rayTrace = SHHelper.rayTrace(player, range, 0, 1);

            if (rayTrace != null)
            {
                if (rayTrace.typeOfHit == MovingObjectType.BLOCK && !player.worldObj.isRemote)
                {
                    ForgeDirection dir = ForgeDirection.getOrientation(rayTrace.sideHit);
                    int x = rayTrace.blockX + dir.offsetX;
                    int y = rayTrace.blockY + dir.offsetY;
                    int z = rayTrace.blockZ + dir.offsetZ;

                    if (Rule.GRIEF_HEATGUN.get(world, rayTrace.blockX, rayTrace.blockZ))
                    {
                        if (player.canPlayerEdit(x, y, z, rayTrace.sideHit, itemstack) && world.isAirBlock(x, y, z) && world.getBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ).isFlammable(world, rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ, dir))
                        {
                            world.setBlock(x, y, z, Blocks.fire);
                        }
                        else if (FiskServerUtils.canEntityEdit(player, rayTrace, itemstack))
                        {
                            SHHelper.melt(world, rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ, 7);
                        }
                    }
                }
                else if (rayTrace.entityHit != null)
                {
                    if (rayTrace.entityHit.attackEntityFrom(ModDamageSources.BURN.apply(player), Rule.DMG_HEATGUN.get(player, hero)))
                    {
                        SHHelper.ignite(rayTrace.entityHit, SHConstants.IGNITE_HEAT_GUN);
                    }

                    HeatSource.HEAT_GUN.applyHeat(rayTrace.entityHit);
                }
            }

            if (player.worldObj.isRemote)
            {
                double length = rayTrace != null && rayTrace.hitInfo instanceof Double ? (Double) rayTrace.hitInfo : range;
                float spread = 0.2F;
                float motion = 0.05F;

                Vec3 src = VectorHelper.getOffsetCoords(player, -0.375, -0.05, 0.8);
                Vec3 dest = VectorHelper.getOffsetCoords(player, 0, 0, length);
                length = src.distanceTo(dest);

                for (double point = 0; point <= length; point += 0.15)
                {
                    Vec3 vec3 = VectorHelper.interpolate(dest, src, point);
                    SHParticleType.SHORT_FLAME.spawn(vec3.xCoord + (Math.random() - 0.5) * spread, vec3.yCoord + (Math.random() - 0.5) * spread, vec3.zCoord + (Math.random() - 0.5) * spread, (Math.random() - 0.5) * motion, (Math.random() - 0.5) * motion, (Math.random() - 0.5) * motion);
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
