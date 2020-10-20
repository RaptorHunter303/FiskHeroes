package com.fiskmods.heroes.common.interaction;

import java.util.List;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityLightningCast;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class InteractionLightning extends InteractionBase
{
    public InteractionLightning()
    {
        requireModifier(Ability.ELECTROKINESIS);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return !SHData.AIMING.get(player) && player.getHeldItem() == null && !player.isSneaking();
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Cooldown.LIGHTNING.available(player);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        Hero hero = SHHelper.getHero(sender);
        float range = Rule.RANGE_LIGHTNINGCAST.get(sender, hero);
        
        MovingObjectPosition rayTrace = SHHelper.rayTrace(sender, range, 2, 1);

        if (rayTrace == null)
        {
            rayTrace = new MovingObjectPosition(0, 0, 0, 0, VectorHelper.getOffsetCoords(sender, 0, 0, range), false);
        }

        if (rayTrace != null && rayTrace.hitVec != null)
        {
            if (side.isServer())
            {
                Vec3 v = rayTrace.hitVec;
                Entity prev = sender;
                Float dmg = null;

                float r = Rule.RADIUS_LIGHTNINGCHAIN.get(sender, hero);
                AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0).offset(v.xCoord, v.yCoord, v.zCoord).expand(r, r, r);
                List<Entity> list = sender.worldObj.getEntitiesWithinAABBExcludingEntity(sender, aabb);
                list.removeIf(e -> !(e instanceof EntityLivingBase));

                if (rayTrace.entityHit != null)
                {
                    rayTrace.entityHit.attackEntityFrom(ModDamageSources.LIGHTNING.apply(sender), dmg = Rule.DMG_LIGHTNINGCAST.get(sender, hero));
                    prev = rayTrace.entityHit;
                }

                boolean flag = false;

                if (rayTrace.entityHit != null || list.isEmpty())
                {
                    sender.worldObj.spawnEntityInWorld(new EntityLightningCast(sender.worldObj, sender, sender, v, rayTrace.typeOfHit == MovingObjectType.MISS ? 2 : 3));
                }
                else
                {
                    flag = true;
                }

                if (dmg == null)
                {
                    dmg = Rule.DMG_LIGHTNINGCAST.get(sender, hero);
                }
                
                for (Entity entity : list)
                {
                    if (entity != rayTrace.entityHit)
                    {
                        float damage = dmg;

                        if (damage > 0)
                        {
                            damage = Math.max(damage / list.size(), 1);
                        }

                        entity.hurtResistantTime = 0;
                        entity.worldObj.spawnEntityInWorld(new EntityLightningCast(sender.worldObj, sender, prev, VectorHelper.centerOf(entity), 3));
                        entity.attackEntityFrom(ModDamageSources.LIGHTNING.apply(sender), damage);
                        prev = entity;
                        flag = false;
                    }
                }
            }
            else if (sender == clientPlayer)
            {
                sender.swingItem();
                Cooldown.LIGHTNING.set(sender);
            }
        }
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
