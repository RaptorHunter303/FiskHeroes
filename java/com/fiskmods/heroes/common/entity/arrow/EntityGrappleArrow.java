package com.fiskmods.heroes.common.entity.arrow;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.entity.EntityGrapplingHookCable;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityGrappleArrow extends EntityTrickArrow
{
    public float fireSpread;

    public EntityGrappleArrow(World world)
    {
        super(world);
    }

    public EntityGrappleArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityGrappleArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityGrappleArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected void init(EntityLivingBase shooter, boolean horizontalBow)
    {
        super.init(shooter, horizontalBow);
        ignoreFrustumCheck = true;
        renderDistanceWeight = 20;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player)
    {
        if (getIsCableCut())
        {
            super.onCollideWithPlayer(player);
        }
    }

    @Override
    protected void onImpactEntity(MovingObjectPosition mop)
    {
        if (mop.entityHit != null && mop.entityHit != getShooter())
        {
            shootingEntity = getShooter();

            if (isBurning() && canTargetEntity(mop.entityHit))
            {
                SHHelper.ignite(mop.entityHit, 5);
            }

            if (mop.entityHit instanceof EntityLivingBase && !(mop.entityHit instanceof EntityEnderman))
            {
                handlePostDamageEffects((EntityLivingBase) mop.entityHit);

                if (shootingEntity instanceof EntityPlayerMP && mop.entityHit != shootingEntity && mop.entityHit instanceof EntityPlayer)
                {
                    ((EntityPlayerMP) shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                }
            }

            playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));

            if (canTargetEntity(mop.entityHit))
            {
                setDead();
            }
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        // if (getShooter() instanceof EntityPlayer)
        // {
        // EntityPlayer shooter = (EntityPlayer) getShooter();
        // double side = 0.3D;
        // double forward = 0.3D;
        // double d = MathHelper.sin(shooter.renderYawOffset * (float) Math.PI / 180);
        // double d1 = MathHelper.cos(shooter.renderYawOffset * (float) Math.PI / 180);
        //
        // Vec3 src = Vec3.createVectorHelper(posX, boundingBox.minY + height / 2, posZ);
        // Vec3 dst = Vec3.createVectorHelper(shooter.posX - d1 * side - d * forward, shooter.boundingBox.minY + shooter.height / 2, shooter.posZ - d * side + d1 * forward);
        // double dist = src.distanceTo(dst);
        //
        // for (double d2 = 0; d2 < dist; d2 += 1D / 2)
        // {
        // double d3 = d2 / dist;
        // Vec3 vec3 = Vec3.createVectorHelper(SHHelper.getProgress(src.xCoord, dst.xCoord, d3), SHHelper.getProgress(src.yCoord, dst.yCoord, d3), SHHelper.getProgress(src.zCoord, dst.zCoord, d3));
        //
        // SHParticles.FLAME.spawnParticle(vec3.xCoord, vec3.yCoord, vec3.zCoord, 0, 0, 0);
        // }
        // }

        if (!getIsCableCut() && (getShooter() == null || !getShooter().isEntityAlive()))
        {
            setIsCableCut(true);
        }

        if (isBurning())
        {
            if (fireSpread < 1)
            {
                fireSpread += 1F / 40;
                fireSpread = MathHelper.clamp_float(fireSpread, 0, 1);
            }
            else if (!getIsCableCut())
            {
                setIsCableCut(true);
            }
        }
    }

    @Override
    public boolean isBurning()
    {
        return fireSpread > 0 && !getIsCableCut() || super.isBurning();
    }

    @Override
    public void inEntityUpdate(EntityLivingBase living)
    {
        super.inEntityUpdate(living);

        if (getShooter() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) getShooter();

            if (!getIsCableCut())
            {
                if (player.getHeldItem() == null || player.getHeldItem().getItem() != ModItems.compoundBow || player.swingProgressInt == 1)
                {
                    setIsCableCut(true);
                    player.worldObj.playSoundAtEntity(player, SHSounds.ENTITY_ARROW_GRAPPLE_DISCONNECT.toString(), 1.0F, 0.8F);
                }

                if (prevPosX == posX && prevPosY == posY && prevPosZ == posZ)
                {
                    player.fallDistance = 0;
                    player.motionX += (posX - player.posX) / 60;
                    player.motionY += (posY - player.posY) / 60;
                    player.motionZ += (posZ - player.posZ) / 60;

                    if (player.getDistanceToEntity(living) > 10)
                    {
                        living.fallDistance = 0;
                        living.motionX += (player.posX - posX) / 100;
                        living.motionY += (player.posY - posY) / 100;
                        living.motionZ += (player.posZ - posZ) / 100;
                    }
                }

                worldObj.spawnEntityInWorld(makeCable(living, player));
            }
        }
    }

    public EntityGrapplingHookCable makeCable(EntityLivingBase living, EntityPlayer player)
    {
        return new EntityGrapplingHookCable(worldObj, living, player);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(19, (byte) 0);
    }

    public boolean getIsCableCut()
    {
        return dataWatcher.getWatchableObjectByte(19) > 0;
    }

    public void setIsCableCut(boolean flag)
    {
        dataWatcher.updateObject(19, (byte) (flag ? 1 : 0));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        setIsCableCut(nbt.getBoolean("IsCableCut"));
        fireSpread = nbt.getFloat("FireSpread");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("IsCableCut", getIsCableCut());
        nbt.setFloat("FireSpread", fireSpread);
    }
}
