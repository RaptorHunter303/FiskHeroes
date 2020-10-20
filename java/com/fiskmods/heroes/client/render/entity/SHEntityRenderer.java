package com.fiskmods.heroes.client.render.entity;

import java.util.List;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class SHEntityRenderer extends EntityRenderer
{
    private final Minecraft mc;
    private Entity pointedEntity;

    public SHEntityRenderer(Minecraft mc)
    {
        super(mc, mc.getResourceManager());
        this.mc = mc;
    }

    @Override
    public void updateCameraAndRender(float partialTicks)
    {
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.isPlayerSleeping())
        {
            super.updateCameraAndRender(partialTicks);
            return;
        }

        int face = SHHelper.getSideStandingOn(player);
        float f = SHData.SCALE.interpolate(player);

        if (SHData.GLIDING.get(player))
        {
            float f1 = MathHelper.clamp_float(SHData.TICKS_GLIDING.get(player) + partialTicks, 0, 4) / 4;
            f *= 0.6F / 1.8F * f1 + (1 - f1);
        }
        else
        {
            float f1 = SHData.SHADOWFORM_TIMER.interpolate(player);
            f *= 0.2F / 1.8F * f1 + (1 - f1);
        }

        if (face != 1)
        {
            float eyeHeight = 0.15F;
            player.yOffset = 1.62F + (1.62F - eyeHeight);
        }
        else
        {
            player.yOffset = (2 - f) * 1.62F;
        }

        super.updateCameraAndRender(partialTicks);
        player.yOffset = 1.62F;
    }

    @Override
    public void getMouseOver(float partialTicks)
    {
        if (mc.renderViewEntity != null)
        {
            if (mc.theWorld != null)
            {
                mc.pointedEntity = null;
                double dist = SHHelper.getReachDistance(mc.thePlayer, mc.playerController.getBlockReachDistance());
                mc.objectMouseOver = mc.renderViewEntity.rayTrace(dist, partialTicks);
                double d1 = dist;
                Vec3 vec3 = mc.renderViewEntity.getPosition(partialTicks);

                if (mc.playerController.extendedReach())
                {
                    dist = SHHelper.getReachDistance(mc.thePlayer, 6.0D);
                    d1 = SHHelper.getReachDistance(mc.thePlayer, 6.0D);
                }
                else
                {
                    if (dist > SHHelper.getReachDistance(mc.thePlayer, 3.0D))
                    {
                        d1 = SHHelper.getReachDistance(mc.thePlayer, 3.0D);
                    }

                    dist = d1;
                }

                if (mc.objectMouseOver != null)
                {
                    d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vec3 vec31 = mc.renderViewEntity.getLook(partialTicks);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist);
                pointedEntity = null;
                Vec3 vec33 = null;
                float f1 = 1.0F;
                double d2 = d1;

                List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist).expand(f1, f1, f1));

                for (Entity entity : list)
                {
                    if (entity.canBeCollidedWith())
                    {
                        float f2 = entity.getCollisionBorderSize();
                        AxisAlignedBB aabb = entity.boundingBox.expand(f2, f2, f2);
                        MovingObjectPosition mop = aabb.calculateIntercept(vec3, vec32);

                        if (aabb.isVecInside(vec3))
                        {
                            if (0.0D < d2 || d2 == 0.0D)
                            {
                                pointedEntity = entity;
                                vec33 = mop == null ? vec3 : mop.hitVec;
                                d2 = 0.0D;
                            }
                        }
                        else if (mop != null)
                        {
                            double d3 = vec3.distanceTo(mop.hitVec);

                            if (d3 < d2 || d2 == 0.0D)
                            {
                                if (entity == mc.renderViewEntity.ridingEntity && !entity.canRiderInteract())
                                {
                                    if (d2 == 0.0D)
                                    {
                                        pointedEntity = entity;
                                        vec33 = mop.hitVec;
                                    }
                                }
                                else
                                {
                                    pointedEntity = entity;
                                    vec33 = mop.hitVec;
                                    d2 = d3;
                                }
                            }
                        }
                    }
                }

                if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null))
                {
                    mc.objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);

                    if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
                    {
                        mc.pointedEntity = pointedEntity;
                    }
                }
            }
        }
    }
}
