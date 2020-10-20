package com.fiskmods.heroes.common.entity.arrow;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.item.ItemTrickArrow;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class EntityFireworkArrow extends EntityTrickArrow
{
    private int fireworkAge;
    private int lifetime;
    private int strength;
    private float radius;

    public EntityFireworkArrow(World world)
    {
        super(world);
    }

    public EntityFireworkArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityFireworkArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityFireworkArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        ++fireworkAge;

        if (fireworkAge > lifetime)
        {
            trigger(null);
        }
    }

    @Override
    public EntityTrickArrow setArrowItem(ItemStack itemstack)
    {
        boolean flag = itemstack != null && !ItemStack.areItemStackTagsEqual(itemstack, getArrowItem());
        super.setArrowItem(itemstack);

        if (flag)
        {
            NBTTagCompound firework = getFireworksTag();
            List<Integer> types = new ArrayList<>();
            boolean flicker = false;
            boolean trail = false;
            int flight = 1;

            if (firework != null)
            {
                flight += firework.getByte("Flight");

                if (firework.hasKey("Explosions", NBT.TAG_LIST))
                {
                    NBTTagList explosions = firework.getTagList("Explosions", NBT.TAG_COMPOUND);

                    for (int i = 0; i < explosions.tagCount(); ++i)
                    {
                        NBTTagCompound tag = explosions.getCompoundTagAt(i);
                        flicker |= tag.getBoolean("Flicker");
                        trail |= tag.getBoolean("Trail");
                        types.add((int) tag.getByte("Type"));
                    }
                }
            }

            lifetime = (10 * flight + rand.nextInt(6) + rand.nextInt(7)) / 2;
            strength = 120;

            if (flicker)
            {
                strength += 20;
            }

            if (trail)
            {
                strength += 30;
            }

            for (Integer type : types)
            {
                switch (type)
                {
                case 1:
                    radius = Math.max(radius, 8);
                    break;
                case 2:
                    radius = Math.max(radius, 5);
                    break;
                case 4:
                    strength += 20;
                    radius = Math.max(radius, 3);
                    break;
                default:
                    radius = Math.max(radius, 4);
                    break;
                }
            }
        }

        return this;
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        motionX = 0;
        motionY = 0;
        motionZ = 0;

        if (mop.entityHit != null && mop.hitVec != null)
        {
            double x = mop.hitVec.xCoord;
            double y = mop.hitVec.yCoord + mop.entityHit.height / 2;
            double z = mop.hitVec.zCoord;
            float f = 0.5F;

            posX = posX + (x - posX) * f;
            posY = posY + (y - posY) * f;
            posZ = posZ + (z - posZ) * f;
        }

        trigger(mop.entityHit);
    }

    @Override
    public boolean onCaught(EntityLivingBase entity)
    {
        trigger(entity);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte b)
    {
        if (b == 17 && worldObj.isRemote && getArrowItem() != null)
        {
            NBTTagCompound firework = getFireworksTag();

            if (firework != null)
            {
                worldObj.makeFireworks(posX, posY, posZ, motionX, motionY, motionZ, firework);
            }
        }

        super.handleHealthUpdate(b);
    }

    public void trigger(Entity entityHit)
    {
        if (!worldObj.isRemote)
        {
            worldObj.setEntityState(this, (byte) 17);
            setDead();
        }

        if (radius > 0)
        {
            AxisAlignedBB aabb = boundingBox.expand(radius, radius, radius).contract(width / 2, height / 2, width / 2);
            List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
            EntityLivingBase shooter = null;

            if (getShooter() instanceof EntityLivingBase)
            {
                shooter = (EntityLivingBase) getShooter();
            }

            for (EntityLivingBase entity : list)
            {
                if (entity instanceof EntityCreeper)
                {
                    ((EntityCreeper) entity).func_146079_cb();
                }
                else
                {
                    float amount = entity == entityHit ? 1 : FiskMath.getScaledDistance(VectorHelper.centerOf(this), VectorHelper.centerOf(entity), radius);
                    StatusEffect.add(entity, StatEffect.FLASHBANG, MathHelper.ceiling_float_int(amount * strength), 0);
                }
            }
        }
    }

    public NBTTagCompound getFireworksTag()
    {
        NBTTagCompound nbttagcompound = null;
        ItemStack itemstack = ItemTrickArrow.getItem(getArrowItem());

        if (itemstack == null)
        {
            itemstack = new ItemStack(Items.fireworks);
            itemstack.setTagCompound(new NBTTagCompound());
        }

        if (itemstack != null && itemstack.hasTagCompound())
        {
            nbttagcompound = itemstack.getTagCompound().getCompoundTag("Fireworks");
        }

        return nbttagcompound;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("Life", fireworkAge);
        compound.setInteger("LifeTime", lifetime);
        compound.setInteger("Strength", strength);
        compound.setFloat("Radius", radius);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        fireworkAge = compound.hasKey("Life", NBT.TAG_INT) ? compound.getInteger("Life") : fireworkAge;
        lifetime = compound.hasKey("LifeTime", NBT.TAG_INT) ? compound.getInteger("LifeTime") : lifetime;
        strength = compound.hasKey("Strength", NBT.TAG_INT) ? compound.getInteger("Strength") : strength;
        radius = compound.hasKey("Radius", NBT.TAG_FLOAT) ? compound.getFloat("Radius") : radius;
    }
}
