package com.fiskmods.heroes.common.entity.arrow;

import com.fiskmods.heroes.client.sound.SHSounds;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class EntityExplPufferfArrow extends EntityPufferfishArrow
{
    private int fuse = 30;

    public EntityExplPufferfArrow(World world)
    {
        super(world);
    }

    public EntityExplPufferfArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityExplPufferfArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityExplPufferfArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        update();
    }

    @Override
    public void inEntityUpdate(EntityLivingBase living)
    {
        super.inEntityUpdate(living);
        update();
    }

    protected void update()
    {
        if (ticksInGround == 1)
        {
            playSound(SHSounds.ENTITY_ARROW_PUFFERFISH_PRIMED.toString(), 1, 1);
        }
        else if (ticksInGround > fuse)
        {
            if (!worldObj.isRemote)
            {
                worldObj.createExplosion(getShooter(), posX, posY, posZ, 1.5F, false);
                setDead();
            }
        }
        else if (ticksInGround > 1)
        {
            worldObj.spawnParticle("smoke", posX, posY, posZ, 0, 0, 0);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("Fuse", NBT.TAG_ANY_NUMERIC))
        {
            fuse = compound.getShort("Fuse");
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setShort("Fuse", (short) fuse);
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player)
    {
        if (player == getShooter())
        {
            super.onCollideWithPlayer(player);
        }
    }
}
