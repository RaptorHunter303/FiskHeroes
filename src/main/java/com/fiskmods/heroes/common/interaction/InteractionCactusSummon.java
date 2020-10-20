package com.fiskmods.heroes.common.interaction;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityCactus;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.RewardHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class InteractionCactusSummon extends InteractionBase
{
    public InteractionCactusSummon()
    {
        super(InteractionType.RIGHT_CLICK_BLOCK, InteractionType.RIGHT_CLICK_AIR);
        requireModifier(Ability.CACTUS_PHYSIOLOGY);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return (player.worldObj.getBlock(x, y, z) == Blocks.cactus || type != InteractionType.RIGHT_CLICK_BLOCK) && SHData.AIMING.get(player) && !player.isSneaking();
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Cooldown.SUMMON_CACTUS.available(player);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        double range = Rule.RANGE_CACTUSSUMMON.getHero(sender);
        Vec3 src = VectorHelper.getOffsetCoords(sender, -0.3, -0.4, 0.6);
        Vec3 dst = VectorHelper.getOffsetCoords(sender, 0, 0, range);

        MovingObjectPosition mop = sender.worldObj.rayTraceBlocks(VectorHelper.copy(src), VectorHelper.copy(dst));

        if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
        {
            dst = mop.hitVec;
            int x1 = mop.blockX;
            int y1 = mop.blockY;
            int z1 = mop.blockZ;

            if (sender.worldObj.getBlock(x1, y1, z1) == Blocks.cactus)
            {
                if (side.isServer())
                {
                    int start = y1;
                    int end = y1;

                    while (sender.worldObj.getBlock(x1, start - 1, z1) == Blocks.cactus)
                    {
                        --start;
                    }

                    while (sender.worldObj.getBlock(x1, end + 1, z1) == Blocks.cactus)
                    {
                        ++end;
                    }

                    for (int i = end; i >= start; --i)
                    {
                        sender.worldObj.setBlockToAir(x1, i, z1);
                    }

                    EntityCactus entity = new EntityCactus(sender.worldObj);
                    entity.setPosition(x1 + 0.5F, start, z1 + 0.5F);
                    entity.setCactusSize(end - start + 1);
                    entity.setDonatorSummoned(RewardHelper.hasReward(sender));
                    sender.worldObj.spawnEntityInWorld(entity);
                }
                else
                {
                    double distance = src.distanceTo(dst);

                    for (double point = 0; point <= distance; point += 0.25D)
                    {
                        Vec3 particleVec = VectorHelper.interpolate(src, dst, distance - point);
                        double d0 = 1 - MathHelper.sin((float) Math.abs(point / distance * 2 - 1)) / (Math.PI / 2) * 2;
                        double d1 = MathHelper.sin((float) point) / range * distance;
                        double d2 = MathHelper.cos(1 + (float) point) / range * distance;
                        double d3 = MathHelper.sin(2 + (float) point) / range * distance;

                        for (int i = 0; i < 1 + d0 * 3; ++i)
                        {
                            sender.worldObj.spawnParticle("happyVillager", particleVec.xCoord + (d1 + Math.random() * 2 - 1) * d0, particleVec.yCoord + (d2 + Math.random() * 2 - 1) * d0, particleVec.zCoord + (d3 + Math.random() * 2 - 1) * d0, 0, 0, 0);
                        }
                    }
                }
            }
        }

        if (side.isClient() && sender == clientPlayer)
        {
            Cooldown.SUMMON_CACTUS.set(sender);
        }
    }
}
