package com.fiskmods.heroes.common.entity.arrow;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityEnderPearlArrow extends EntityTrickArrow
{
    public EntityEnderPearlArrow(World world)
    {
        super(world);
    }

    public EntityEnderPearlArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityEnderPearlArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityEnderPearlArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected String getParticleName()
    {
        return "portal";
    }

    @Override
    protected void spawnTrailingParticles()
    {
        float f = 0.1F;

        for (int i = 0; i < 5; ++i)
        {
            worldObj.spawnParticle(getParticleName(), posX, posY, posZ, (rand.nextDouble() * 2 - 1) * f, (rand.nextDouble() * 2 - 1) * f, (rand.nextDouble() * 2 - 1) * f);
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        if (getArrowId() > 0)
        {
            if (worldObj.isRemote)
            {
                for (int i = 0; i < 32; ++i)
                {
                    worldObj.spawnParticle("portal", posX, posY + rand.nextDouble() * 2, posZ, rand.nextGaussian(), 0, rand.nextGaussian());
                }
            }
            else
            {
                if (getShooter() instanceof EntityPlayerMP)
                {
                    EntityPlayerMP player = (EntityPlayerMP) getShooter();

                    if (player.playerNetServerHandler.func_147362_b().isChannelOpen() && player.worldObj == worldObj)
                    {
                        double x = posX;
                        double y = posY;
                        double z = posZ;

                        if (mop.hitVec != null)
                        {
                            x = mop.hitVec.xCoord;
                            y = mop.hitVec.yCoord;
                            z = mop.hitVec.zCoord;
                        }

                        if (mop.typeOfHit == MovingObjectType.BLOCK)
                        {
                            ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
                            x += dir.offsetX * 0.5;
                            z += dir.offsetZ * 0.5;

                            if (dir.offsetY < 0)
                            {
                                y -= player.height;
                            }
                        }

                        EnderTeleportEvent event = new EnderTeleportEvent(player, x, y, z, 5.0F);

                        if (!MinecraftForge.EVENT_BUS.post(event))
                        {
                            if (player.isRiding())
                            {
                                player.mountEntity(null);
                            }

                            player.setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
                            player.fallDistance = 0.0F;
                            player.attackEntityFrom(DamageSource.fall, event.attackDamage);
                        }
                    }
                }
            }

            setArrowId(0);
        }

        super.onImpact(mop);
    }
}
