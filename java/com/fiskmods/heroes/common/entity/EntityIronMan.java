package com.fiskmods.heroes.common.entity;

import java.util.UUID;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.data.IDataHolder;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.ai.EntityAIFollowOwner2;
import com.fiskmods.heroes.common.entity.ai.EntityAIIronManAttack;
import com.fiskmods.heroes.common.entity.ai.EntityAIIronManWander;
import com.fiskmods.heroes.common.entity.ai.EntityAIOwnerHurtByTarget2;
import com.fiskmods.heroes.common.entity.ai.EntityAIOwnerHurtTarget2;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.interaction.InteractionRepulsor;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class EntityIronMan extends EntityCreature implements IEntityOwnable, IDataHolder, IArmorTrackedEntity, IRangedAttackMob
{
    public float aimingTimer;
    public float prevAimingTimer;
    public int metalHeatCooldown;
    public float prevMetalHeat;

    private Hero prevHero;
    private int aggroTimer;

    public EntityIronMan(World world)
    {
        super(world);
        getNavigator().setAvoidsWater(true);
        entityCollisionReduction = 1;
        isImmuneToFire = true;
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIIronManAttack(this, 1, 30, 40, 30));
        tasks.addTask(3, new EntityAIAttackOnCollide(this, 1, true));
        tasks.addTask(4, new EntityAIFollowOwner2(this, 1, 10, 2));
        tasks.addTask(5, new EntityAIIronManWander(this, 1));
        targetTasks.addTask(1, new EntityAIOwnerHurtByTarget2(this));
        targetTasks.addTask(2, new EntityAIOwnerHurtTarget2(this));
        targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, IMob.class, 0, true));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
    }

    @Override
    public boolean isAIEnabled()
    {
        return true;
    }

    @Override
    protected String getHurtSound()
    {
        return SHSounds.MOB_IRONMAN_HURT.toString();
    }

    @Override
    protected String getDeathSound()
    {
        return SHSounds.MOB_IRONMAN_HURT.toString();
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (!isEntityAlive())
        {
            return;
        }

        EntityLivingBase owner = getOwner();
        Hero hero = SHHelper.getHero(this);

        SHData.onUpdate(this);
        Hero.updateModifiers(this, hero, Phase.START);

        if (!worldObj.isRemote)
        {
            if (getActivePotionEffects().size() > 0)
            {
                clearActivePotions();
            }

            if (get(SHData.SUIT_OPEN))
            {
                double dist = 0.25;
                AxisAlignedBB aabb = boundingBox.expand(dist, dist, dist);

                if (owner instanceof EntityPlayer)
                {
                    EntityPlayer player = (EntityPlayer) owner;

                    if (player.isEntityAlive() && FiskMath.containsAABB(aabb, player.boundingBox))
                    {
                        for (int i = 1; i <= 4; ++i)
                        {
                            if (getEquipmentInSlot(i) != null)
                            {
                                if (player.getEquipmentInSlot(i) != null && !player.inventory.addItemStackToInventory(player.getEquipmentInSlot(i)))
                                {
                                    player.entityDropItem(player.getEquipmentInSlot(i), 0);
                                }

                                player.setCurrentItemOrArmor(i, getEquipmentInSlot(i));
                            }
                        }

                        SHData.SUIT_OPEN_TIMER.set(player, (byte) 5);
                        setDead();
                    }
                }

                if (get(SHData.SUIT_OPEN_TIMER) < 5)
                {
                    incr(SHData.SUIT_OPEN_TIMER, (byte) 1);
                }

                if (owner != null)
                {
                    newRotationYaw = rotationYaw = renderYawOffset = rotationYawHead = owner.renderYawOffset;
                }
            }
            else if (get(SHData.SUIT_OPEN_TIMER) > 0)
            {
                incr(SHData.SUIT_OPEN_TIMER, (byte) -1);
            }

            if (getAttackTarget() != null)
            {
                aggroTimer = 30;
            }
            else if (aggroTimer > 0)
            {
                --aggroTimer;
            }

            set(SHData.AIMING, aggroTimer > 0 && !get(SHData.SUIT_OPEN));

            if (get(SHData.HOVERING) && onGround)
            {
                set(SHData.HOVERING, false);
            }
        }

        if (get(SHData.SUIT_OPEN) && !getNavigator().noPath())
        {
            getNavigator().setPath(null, 0);
        }

        boolean flag = get(SHData.AIMING);
        boolean flag1 = get(SHData.PREV_AIMING);
        float f = get(SHData.AIMING_TIMER);

        if (flag && !flag1)
        {
            playSound(SHSounds.MOB_IRONMAN_CHARGE.toString(), 1, 1);
        }
        else if (!flag && flag1)
        {
            playSound(SHSounds.MOB_IRONMAN_POWERDOWN.toString(), 0.5F, 1);
        }

        if (flag && f < 1)
        {
            incr(SHData.AIMING_TIMER, 1F / 4);
        }
        else if (!flag && f > 0)
        {
            incr(SHData.AIMING_TIMER, -1F / 4);
        }

        clamp(SHData.AIMING_TIMER, 0F, 1F);
        set(SHData.PREV_AIMING, flag);

        Hero.updateModifiers(this, hero, Phase.END);

        if (owner != null)
        {
            if (get(SHData.HOVERING) && get(SHData.SUIT_OPEN))
            {
                double d = 1 - MathHelper.clamp_double(2 - getDistance(owner.posX, owner.boundingBox.minY, owner.posZ) * 0.75, 0, 1);
                motionY *= d;
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (source.isMagicDamage() || source == DamageSource.starve || source == DamageSource.cactus || source == DamageSource.drown)
        {
            return false;
        }

        if (super.attackEntityFrom(source, amount) && SHHelper.canArmorBlock(this, source))
        {
            for (int i = 4; i > 0; --i)
            {
                ItemStack stack = getEquipmentInSlot(i);

                if (stack != null)
                {
                    stack.damageItem((int) Math.max(amount / 4, 1), this);

                    if (stack.stackSize <= 0)
                    {
                        setCurrentItemOrArmor(i, null);

                        hurtResistantTime = 0;
                        super.attackEntityFrom(source, Float.MAX_VALUE);
                        break;
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    protected boolean interact(EntityPlayer player)
    {
        if (isOwner(player))
        {
            if (!player.isSneaking())
            {
                set(SHData.SUIT_OPEN, !get(SHData.SUIT_OPEN));
            }
            else
            {
                if (!get(SHData.HOVERING) && onGround)
                {
                    motionY += 1;
                }

                set(SHData.HOVERING, !get(SHData.HOVERING));
            }
        }

        return true;
    }

    @Override
    public String getCommandSenderName()
    {
        Hero hero;
        return hasCustomNameTag() ? getCustomNameTag() : (hero = SHHelper.getHero(this)) != null ? hero.getLocalizedName() : super.getCommandSenderName();
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        return null;
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
        AxisAlignedBB aabb = boundingBox.copy().contract(width / 5, 0, width / 5);
        aabb.maxY = aabb.minY;

        return aabb;
    }

    @Override
    public void applyEntityCollision(Entity entity)
    {
        if (entity != getOwner())
        {
            super.applyEntityCollision(entity);
        }
    }

    @Override
    protected void collideWithEntity(Entity entity)
    {
        if (entity != getOwner())
        {
            super.collideWithEntity(entity);
        }
    }

    @Override
    public boolean canBePushed()
    {
        return true;
    }

    @Override
    protected boolean canDespawn()
    {
        return false;
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityData)
    {
        setCanPickUpLoot(false);
        set(SHData.SUIT_OPEN_TIMER, (byte) 5);

        for (int i = 0; i <= 4; ++i)
        {
            equipmentDropChances[i] = 0;
        }

        return entityData;
    }

    @Override
    public <T> void set(SHData<T> data, T value)
    {
        if (data == SHData.SUIT_OPEN)
        {
            byte b0 = dataWatcher.getWatchableObjectByte(17);
            dataWatcher.updateObject(17, (byte) ((Boolean) value ? b0 | 8 : b0 & ~8));
        }
        else if (data == SHData.SUIT_OPEN_TIMER)
        {
            byte b0 = dataWatcher.getWatchableObjectByte(17);
            dataWatcher.updateObject(17, (byte) ((Byte) value & 7 | b0 & ~7));
        }
        else if (data == SHData.HOVERING)
        {
            byte b0 = dataWatcher.getWatchableObjectByte(17);
            dataWatcher.updateObject(17, (byte) ((Boolean) value ? b0 | 16 : b0 & ~16));
        }
        else if (data == SHData.AIMING)
        {
            byte b0 = dataWatcher.getWatchableObjectByte(17);
            dataWatcher.updateObject(17, (byte) ((Boolean) value ? b0 | 32 : b0 & ~32));
        }
        else if (data == SHData.PREV_AIMING)
        {
            byte b0 = dataWatcher.getWatchableObjectByte(17);
            dataWatcher.updateObject(17, (byte) ((Boolean) value ? b0 | 64 : b0 & ~64));
        }
        else if (data == SHData.AIMING_TIMER)
        {
            aimingTimer = (Float) value;
        }
        else if (data == SHData.AIMING_TIMER.getPrevData())
        {
            prevAimingTimer = (Float) value;
        }
        else if (data == SHData.METAL_HEAT_COOLDOWN)
        {
            metalHeatCooldown = (Integer) value;
        }
        else if (data == SHData.METAL_HEAT)
        {
            dataWatcher.updateObject(18, value);
        }
        else if (data == SHData.METAL_HEAT.getPrevData())
        {
            prevMetalHeat = (Float) value;
        }
        else if (data == SHData.PREV_HERO)
        {
            prevHero = (Hero) value;
        }
    }

    public <T> boolean incr(SHData<T> data, T value)
    {
        return data.incrWithoutNotify(this, value);
    }

    public <T> boolean clamp(SHData<T> data, T min, T max)
    {
        return data.clampWithoutNotify(this, min, max);
    }

    @Override
    public <T> T get(SHData<T> data)
    {
        if (data == SHData.SUIT_OPEN)
        {
            return (T) Boolean.valueOf((dataWatcher.getWatchableObjectByte(17) & 8) == 8);
        }
        else if (data == SHData.SUIT_OPEN_TIMER)
        {
            return (T) Byte.valueOf((byte) (dataWatcher.getWatchableObjectByte(17) & 7));
        }
        else if (data == SHData.HOVERING)
        {
            return (T) Boolean.valueOf((dataWatcher.getWatchableObjectByte(17) & 16) == 16);
        }
        else if (data == SHData.AIMING)
        {
            return (T) Boolean.valueOf((dataWatcher.getWatchableObjectByte(17) & 32) == 32);
        }
        else if (data == SHData.PREV_AIMING)
        {
            return (T) Boolean.valueOf((dataWatcher.getWatchableObjectByte(17) & 64) == 64);
        }
        else if (data == SHData.AIMING_TIMER)
        {
            return (T) Float.valueOf(aimingTimer);
        }
        else if (data == SHData.AIMING_TIMER.getPrevData())
        {
            return (T) Float.valueOf(prevAimingTimer);
        }
        else if (data == SHData.METAL_HEAT_COOLDOWN)
        {
            return (T) Integer.valueOf(metalHeatCooldown);
        }
        else if (data == SHData.METAL_HEAT)
        {
            return (T) Float.valueOf(dataWatcher.getWatchableObjectFloat(18));
        }
        else if (data == SHData.METAL_HEAT.getPrevData())
        {
            return (T) Float.valueOf(prevMetalHeat);
        }
        else if (data == SHData.PREV_HERO)
        {
            return (T) prevHero;
        }

        return data.getDefault();
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, "");
        dataWatcher.addObject(17, (byte) 5);
        dataWatcher.addObject(18, (float) 0);
    }

    @Override
    public String func_152113_b()
    {
        return dataWatcher.getWatchableObjectString(16);
    }

    public void setOwner(String owner)
    {
        dataWatcher.updateObject(16, owner);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        set(SHData.SUIT_OPEN, nbt.getBoolean("SuitOpen"));
        set(SHData.SUIT_OPEN_TIMER, nbt.getByte("SuitOpenTimer"));
        set(SHData.HOVERING, nbt.getBoolean("Hovering"));
        set(SHData.METAL_HEAT_COOLDOWN, nbt.getInteger("MetalHeatCooldown"));
        set(SHData.METAL_HEAT, nbt.getFloat("MetalHeat"));

        String s = "";

        if (nbt.hasKey("OwnerUUID", NBT.TAG_STRING))
        {
            s = nbt.getString("OwnerUUID");
        }
        else
        {
            s = PreYggdrasilConverter.func_152719_a(nbt.getString("Owner"));
        }

        if (s.length() > 0)
        {
            setOwner(s);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("SuitOpen", get(SHData.SUIT_OPEN));
        nbt.setByte("SuitOpenTimer", get(SHData.SUIT_OPEN_TIMER));
        nbt.setBoolean("Hovering", get(SHData.HOVERING));
        nbt.setInteger("MetalHeatCooldown", get(SHData.METAL_HEAT_COOLDOWN));
        nbt.setFloat("MetalHeat", get(SHData.METAL_HEAT));

        if (func_152113_b() == null)
        {
            nbt.setString("OwnerUUID", "");
        }
        else
        {
            nbt.setString("OwnerUUID", func_152113_b());
        }
    }

    @Override
    public EntityLivingBase getOwner()
    {
        try
        {
            UUID uuid = UUID.fromString(func_152113_b());
            return uuid == null ? null : worldObj.func_152378_a(uuid);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    public boolean isOwner(EntityLivingBase entity)
    {
        return entity == getOwner();
    }

    @Override
    public Team getTeam()
    {
        EntityLivingBase owner = getOwner();

        if (owner != null)
        {
            return owner.getTeam();
        }

        return super.getTeam();
    }

    @Override
    public boolean isOnSameTeam(EntityLivingBase entity)
    {
        EntityLivingBase owner = getOwner();

        if (entity == owner)
        {
            return true;
        }

        if (owner != null)
        {
            return owner.isOnSameTeam(entity);
        }

        return super.isOnSameTeam(entity);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float f)
    {
        double d0 = target.posX - posX;
        double d1 = target.boundingBox.minY + target.height * 0.75F - (posY + getEyeHeight() - 0.1);
        double d2 = target.posZ - posZ;
        double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);

        if (d3 >= 1E-7)
        {
            float f2 = (float) (Math.atan2(d2, d0) * 180 / Math.PI) - 90;
            float f3 = (float) -(Math.atan2(d1, d3) * 180 / Math.PI);
            rotationYaw = f2;
            rotationPitch = f3;
        }

        if (!worldObj.isRemote)
        {
            InteractionRepulsor.shoot(this);
        }
    }
}
