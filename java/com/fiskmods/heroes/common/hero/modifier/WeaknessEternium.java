package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class WeaknessEternium extends Weakness
{
    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled && !entity.worldObj.isRemote && entity.ticksExisted % 10 == 0)
        {
            boolean flag = false;
            float radius;

            if (entity instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) entity;

                for (ItemStack element : player.inventory.mainInventory)
                {
                    if (element != null && SHHelper.isPoisonEternium(element.getItem()))
                    {
                        flag = true;
                        break;
                    }
                }
            }

            if (!flag && (radius = Rule.RADIUS_ETERNIUMWEAKNESS.get(entity, hero)) > 0)
            {
                AxisAlignedBB aabb = entity.boundingBox.expand(radius, radius, radius);
                int minX = MathHelper.floor_double(aabb.minX);
                int maxX = MathHelper.floor_double(aabb.maxX + 1);
                int minY = MathHelper.floor_double(aabb.minY);
                int maxY = MathHelper.floor_double(aabb.maxY + 1);
                int minZ = MathHelper.floor_double(aabb.minZ);
                int maxZ = MathHelper.floor_double(aabb.maxZ + 1);

                if (entity.worldObj.checkChunksExist(minX, minY, minZ, maxX, maxY, maxZ))
                {
                    for (int x = minX; x < maxX; ++x)
                    {
                        for (int y = minY; y < maxY; ++y)
                        {
                            for (int z = minZ; z < maxZ; ++z)
                            {
                                Block block = entity.worldObj.getBlock(x, y, z);

                                if (SHHelper.isPoisonEternium(block))
                                {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (flag)
            {
                int duration = Rule.TICKS_ETERNIUMWEAKNESS.get(entity, hero);

                if (duration > 0)
                {
                    StatusEffect.add(entity, StatEffect.ETERNIUM_WEAKNESS, duration, 0);
                }
            }
        }
    }

    @Override
    public float damageDealt(EntityLivingBase entity, EntityLivingBase target, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        return StatusEffect.has(entity, StatEffect.ETERNIUM_WEAKNESS) ? amount / 2 : amount;
    }
}
