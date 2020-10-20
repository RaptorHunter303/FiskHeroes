package com.fiskmods.heroes.common.entity.arrow;

import java.util.List;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.entity.EntityGrapplingHookCable;
import com.fiskmods.heroes.common.network.MessageGrappleArrowCut;
import com.fiskmods.heroes.common.network.SHNetworkManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityVineArrow extends EntityGrappleArrow
{
    public EntityVineArrow(World world)
    {
        super(world);
    }

    public EntityVineArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityVineArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityVineArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        update();

        if (getIsSnake())
        {
            for (Entity entity : (List<Entity>) worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox))
            {
                if (entity instanceof EntityLivingBase)
                {
                    AxisAlignedBB aabb = entity.boundingBox.copy();
                    aabb.maxY = aabb.minY + height;

                    if (aabb.intersectsWith(boundingBox))
                    {
                        entity.attackEntityFrom(ModDamageSources.SNAKE, Rule.DMG_SNAKE.get(entity));
                    }
                }
            }
        }
    }

    @Override
    public void inEntityUpdate(EntityLivingBase living)
    {
        if (!getIsSnake())
        {
            super.inEntityUpdate(living);
            boolean prev = getIsCableCut();
            update();

            if (getIsCableCut() && !prev)
            {
                SHNetworkManager.wrapper.sendToDimension(new MessageGrappleArrowCut(living, this), living.dimension);
            }
        }
        else
        {
            ++ticksExisted;

            if (++ticksInGround == 1200)
            {
                setDead();
            }
        }
    }

    protected void update()
    {
        if (!worldObj.isRemote && ticksInGround > 0 && !getIsSnake() && !getIsCableCut() && rand.nextInt(Math.max(100 - ticksInGround, 10)) == 0)
        {
            setIsCableCut(true);
            worldObj.playSoundAtEntity(getShooter(), SHSounds.ENTITY_ARROW_VINE_SNAP.toString(), 1.0F, 0.8F);
        }
    }

    @Override
    public EntityGrapplingHookCable makeCable(EntityLivingBase living, EntityPlayer player)
    {
        return super.makeCable(living, player).setColor(0x364D23, 0x293C1B);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        setIsSnake(nbt.getBoolean("Snake"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("Snake", getIsSnake());
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(20, (byte) 0);
    }

    public boolean getIsSnake()
    {
        return dataWatcher.getWatchableObjectByte(20) > 0;
    }

    public void setIsSnake(boolean flag)
    {
        dataWatcher.updateObject(20, (byte) (flag ? 1 : 0));
    }
}
