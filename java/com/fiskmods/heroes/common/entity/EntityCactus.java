package com.fiskmods.heroes.common.entity;

import java.util.List;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCactus extends EntityGolem implements IEntitySelector
{
    public int idlingTick = 0;

    public EntityCactus(World world)
    {
        super(world);
        getNavigator().setCanSwim(true);
        tasks.addTask(1, new EntityAIAttackOnCollide(this, 1, true));
        tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9, 32));
        tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1));
        tasks.addTask(6, new EntityAIWander(this, 0.6));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, false, true, this));
        setCactusSize(1 + rand.nextInt(3));
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, 0);
        dataWatcher.addObject(17, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1);
    }

    @Override
    public boolean isEntityApplicable(Entity entity)
    {
        if (!(entity instanceof EntityLivingBase))
        {
            return false;
        }
        else if (entity instanceof EntityCactus || entity.isEntityInvulnerable())
        {
            return false;
        }
        else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)
        {
            return false;
        }
        else if (SHHelper.hasEnabledModifier((EntityLivingBase) entity, Ability.CACTUS_PHYSIOLOGY))
        {
            return false;
        }

        return entity instanceof IMob || !(entity instanceof EntityAnimal || entity instanceof EntityGolem);
    }

    @Override
    public boolean isAIEnabled()
    {
        return true;
    }

    @Override
    protected int decreaseAirSupply(int air)
    {
        return air;
    }

    @Override
    protected void collideWithEntity(Entity entity)
    {
        if (entity instanceof EntityLivingBase && isEntityApplicable(entity) && rand.nextInt(20) == 0)
        {
            setAttackTarget((EntityLivingBase) entity);
        }

        super.collideWithEntity(entity);
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        return entity.boundingBox;
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }

    @Override
    public boolean canBePushed()
    {
        return true;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }

    protected float getAttackStrength()
    {
        return Math.min(getCactusSize(), 3) * 2;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        setSize(1, getCactusSize());

        if (!worldObj.isRemote)
        {
            if (isWet() && getHealth() < getMaxHealth())
            {
                if (ticksExisted % Rule.TICKS_CACTUSHEALRATE.get(this) == 0)
                {
                    heal(1);
                }
            }

            for (EntityLivingBase entity : (List<EntityLivingBase>) worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, getBoundingBox().expand(0.3, 0.3, 0.3), this))
            {
                entity.attackEntityFrom(ModDamageSources.SPIKE.apply(this), getAttackStrength());
            }

            if (getEntityToAttack() == null)
            {
                if (++idlingTick > Rule.TICKS_CACTUSLIFESPAN.get(this))
                {
                    if (!worldObj.isRemote)
                    {
                        int x = MathHelper.floor_double(posX);
                        int y = MathHelper.floor_double(posY);
                        int z = MathHelper.floor_double(posZ);

                        if (Blocks.cactus.canBlockStay(worldObj, x, y, z))
                        {
                            for (int i = 0; i < getCactusSize(); ++i)
                            {
                                worldObj.setBlock(x, y + i, z, Blocks.cactus);
                            }

                            setDead();
                        }
                        else
                        {
                            setHealth(0);
                        }
                    }
                }
            }
            else
            {
                idlingTick = 0;
            }
        }

        if (motionX * motionX + motionZ * motionZ > 2.5E-7 && rand.nextInt(5) == 0)
        {
            int i = MathHelper.floor_double(posX);
            int j = MathHelper.floor_double(posY - 0.2 - yOffset);
            int k = MathHelper.floor_double(posZ);
            Block block = worldObj.getBlock(i, j, k);

            if (block.getMaterial() != Material.air)
            {
                worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_" + worldObj.getBlockMetadata(i, j, k), posX + (rand.nextFloat() - 0.5) * width, boundingBox.minY + 0.1, posZ + (rand.nextFloat() - 0.5) * width, 4 * (rand.nextFloat() - 0.5), 0.5, (rand.nextFloat() - 0.5) * 4);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setInteger("Size", getCactusSize());
        nbttagcompound.setBoolean("Donator", isDonatorSummoned());
        nbttagcompound.setInteger("Idle", idlingTick);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        setCactusSize(nbttagcompound.getInteger("Size"));
        setDonatorSummoned(nbttagcompound.getBoolean("Donator"));
        idlingTick = nbttagcompound.getInteger("Idle");
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    protected String getHurtSound()
    {
        return null;
    }

    @Override
    protected String getDeathSound()
    {
        return null;
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block)
    {
        playSound(SHSounds.MOB_CACTUS_STEP.toString(), 1, 1);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting)
    {
        if (getCactusSize() == 1)
        {
            dropItem(Item.getItemFromBlock(Blocks.cactus), getCactusSize());
        }
        else if (getCactusSize() > 3)
        {
            dropItem(Item.getItemFromBlock(Blocks.cactus), getCactusSize() - 3);
        }
    }

    @Override
    public void setDead()
    {
        int i = getCactusSize();

        if (!worldObj.isRemote && i > 1 && getHealth() <= 0)
        {
            for (int j = 0; j < Math.min(i, 3); ++j)
            {
                float f = j % 2 - 0.5F;
                float f1 = j / 2 - 0.5F;
                EntityCactus entity = new EntityCactus(worldObj);
                entity.setCactusSize(1);
                entity.setDonatorSummoned(isDonatorSummoned());
                entity.setLocationAndAngles(posX + f, posY + 0.5, posZ + f1, rand.nextFloat() * 360, 0);
                worldObj.spawnEntityInWorld(entity);
            }
        }

        super.setDead();
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return false;
    }

    public int getCactusSize()
    {
        return dataWatcher.getWatchableObjectInt(16);
    }

    public void setCactusSize(int size)
    {
        dataWatcher.updateObject(16, size);
        setSize(1, size);
        setPosition(posX, posY, posZ);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Math.min(size, 3) * 5);
        setHealth(getMaxHealth());
        experienceValue = Math.min(size, 3);
    }

    public boolean isDonatorSummoned()
    {
        return dataWatcher.getWatchableObjectByte(17) != 0;
    }

    public void setDonatorSummoned(boolean flag)
    {
        dataWatcher.updateObject(17, (byte) (flag ? 1 : 0));
    }
}
