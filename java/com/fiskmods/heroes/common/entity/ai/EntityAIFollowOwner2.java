package com.fiskmods.heroes.common.entity.ai;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityIronMan;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowOwner2 extends EntityAIBase
{
    private EntityIronMan theIronMan;
    private EntityLivingBase theOwner;
    World theWorld;
    private double field_75336_f;
    private PathNavigate petPathfinder;
    private int field_75343_h;
    float maxDist;
    float minDist;
    private boolean field_75344_i;

    public EntityAIFollowOwner2(EntityIronMan entity, double d, float min, float max)
    {
        theIronMan = entity;
        theWorld = entity.worldObj;
        field_75336_f = d;
        petPathfinder = entity.getNavigator();
        minDist = min;
        maxDist = max;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = theIronMan.getOwner();

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (theIronMan.get(SHData.SUIT_OPEN))
        {
            return false;
        }
        else if (theIronMan.getDistanceSqToEntity(entitylivingbase) < minDist * minDist)
        {
            return false;
        }
        else
        {
            theOwner = entitylivingbase;
            return true;
        }
    }

    @Override
    public boolean continueExecuting()
    {
        return !petPathfinder.noPath() && theIronMan.getDistanceSqToEntity(theOwner) > maxDist * maxDist && !theIronMan.get(SHData.SUIT_OPEN);
    }

    @Override
    public void startExecuting()
    {
        field_75343_h = 0;
        field_75344_i = theIronMan.getNavigator().getAvoidsWater();
        theIronMan.getNavigator().setAvoidsWater(false);
    }

    @Override
    public void resetTask()
    {
        theOwner = null;
        petPathfinder.clearPathEntity();
        theIronMan.getNavigator().setAvoidsWater(field_75344_i);
    }

    @Override
    public void updateTask()
    {
        theIronMan.getLookHelper().setLookPositionWithEntity(theOwner, 10.0F, theIronMan.getVerticalFaceSpeed());

        if (!theIronMan.get(SHData.SUIT_OPEN))
        {
            if (--field_75343_h <= 0)
            {
                field_75343_h = 10;

                if (!petPathfinder.tryMoveToEntityLiving(theOwner, field_75336_f))
                {
                    if (!theIronMan.getLeashed())
                    {
                        if (theIronMan.getDistanceSqToEntity(theOwner) >= 144.0D)
                        {
                            int i = MathHelper.floor_double(theOwner.posX) - 2;
                            int j = MathHelper.floor_double(theOwner.posZ) - 2;
                            int k = MathHelper.floor_double(theOwner.boundingBox.minY);

                            for (int l = 0; l <= 4; ++l)
                            {
                                for (int i1 = 0; i1 <= 4; ++i1)
                                {
                                    if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && World.doesBlockHaveSolidTopSurface(theWorld, i + l, k - 1, j + i1) && !theWorld.getBlock(i + l, k, j + i1).isNormalCube() && !theWorld.getBlock(i + l, k + 1, j + i1).isNormalCube())
                                    {
                                        theIronMan.setLocationAndAngles(i + l + 0.5F, k, j + i1 + 0.5F, theIronMan.rotationYaw, theIronMan.rotationPitch);
                                        petPathfinder.clearPathEntity();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
