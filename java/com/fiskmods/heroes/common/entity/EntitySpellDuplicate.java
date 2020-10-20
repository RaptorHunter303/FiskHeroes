package com.fiskmods.heroes.common.entity;

import java.util.UUID;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.Vec3Container;
import com.fiskmods.heroes.common.data.IDataHolder;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class EntitySpellDuplicate extends EntityLivingBase implements IEntityOwnable, IDataHolder, IArmorTrackedEntity, IEntityAdditionalSpawnData
{
    private float spreadTimer, prevSpreadTimer;

    private Vec3Container rotationCenter;
    private EntityLivingBase ownerEntity, target;

    public EntitySpellDuplicate(World world)
    {
        super(world);
        entityCollisionReduction = 1;
    }

    public EntitySpellDuplicate(EntityLivingBase owner, EntityLivingBase target, float offset)
    {
        this(owner.worldObj);
        setOwner(owner.getUniqueID().toString());
        setTarget(target);

        setSize(owner.width, owner.height);
        setPositionAndRotation(owner.posX, owner.posY, owner.posZ, owner.rotationYaw, owner.rotationPitch);
        setRotationOffset(offset);
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
    }

    @Override
    public boolean isAIEnabled()
    {
        return true;
    }

    @Override
    public int getMaxInPortalTime()
    {
        return 80;
    }

    @Override
    public int getPortalCooldown()
    {
        return 10;
    }

    @Override
    protected String getSwimSound()
    {
        return "game.player.swim";
    }

    @Override
    protected String getSplashSound()
    {
        return "game.player.swim.splash";
    }

    @Override
    protected String getHurtSound()
    {
        return "game.player.hurt";
    }

    @Override
    protected String getDeathSound()
    {
        return "game.player.die";
    }

    @Override
    public void onUpdate()
    {
        newPosRotationIncrements = 0;
        super.onUpdate();
        float rotation = Math.abs(getRotationOffset());
        prevSpreadTimer = spreadTimer;

        if (spreadTimer < rotation)
        {
            spreadTimer += 10;
        }

        EntityLivingBase owner = getOwner();

        SHData.onUpdate(this);
        fallDistance = 0;

        if (owner != null && rotationCenter != null)
        {
            float offset = getMirroredRotation(1);
            float rot = (float) Math.toRadians(offset);
            float f1 = MathHelper.cos(rot);
            float f2 = MathHelper.sin(rot);
            double cx = rotationCenter.getX(), cz = rotationCenter.getZ();
            double d0 = (owner.posX - cx) * f1 + (owner.posZ - cz) * f2 + cx;
            double d2 = (owner.posZ - cz) * f1 - (owner.posX - cx) * f2 + cz;

            rotationPitch = owner.rotationPitch;
            rotationYaw = owner.rotationYaw - offset;
            rotationYawHead = owner.rotationYawHead - offset;
            renderYawOffset = owner.renderYawOffset - offset;
            isSwingInProgress = owner.isSwingInProgress;
            swingProgress = owner.swingProgress;
            setSneaking(owner.isSneaking());
            setSprinting(owner.isSprinting());
            setPosition(d0, owner.boundingBox.minY, d2);

            if (target == null)
            {
                setDead();
            }
            else if (target.isDead || owner.isDead || getDistanceToEntity(target) > SHConstants.RANGE_DUPLICATION || !SHHelper.hasEnabledModifier(owner, Ability.SPELLCASTING))
            {
                setHealth(0);
            }
        }

        spreadTimer = MathHelper.clamp_float(spreadTimer, 0, rotation);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (source != DamageSource.starve && source != DamageSource.drown && source != DamageSource.inWall && source != DamageSource.lava && source != DamageSource.cactus && source != DamageSource.inFire && source != DamageSource.fall && super.attackEntityFrom(source, amount))
        {
            setHealth(0);
            return true;
        }

        return false;
    }

    @Override
    protected void onDeathUpdate()
    {
        if (++deathTime == 4)
        {
            setDead();
        }
    }

    @Override
    public String getCommandSenderName()
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.getCommandSenderName() : super.getCommandSenderName();
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.getCollisionBox(entity) : super.getCollisionBox(entity);
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.getBoundingBox() : super.getBoundingBox();
    }

    @Override
    public void applyEntityCollision(Entity entity)
    {
        if (!isOwner(entity))
        {
            super.applyEntityCollision(entity);
        }
    }

    @Override
    protected void collideWithEntity(Entity entity)
    {
        if (!isOwner(entity))
        {
            super.collideWithEntity(entity);
        }
    }

    @Override
    public <T> void set(SHData<T> data, T value)
    {
    }

    @Override
    public <T> T get(SHData<T> data)
    {
        EntityLivingBase owner = getOwner();

        if (owner != null)
        {
            return data.get(owner);
        }

        return data.getDefault();
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, "");
        dataWatcher.addObject(17, 0F);
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

    public float getRotationOffset()
    {
        return dataWatcher.getWatchableObjectFloat(17);
    }

    public void setRotationOffset(float offset)
    {
        dataWatcher.updateObject(17, MathHelper.wrapAngleTo180_float(offset));
    }

    public float getMirroredRotation(float partialTicks)
    {
        float rot = getRotationOffset();
        return FiskMath.curve((rot < 0 ? -1 : 1) * FiskServerUtils.interpolate(prevSpreadTimer, spreadTimer, partialTicks) / rot) * rot;
    }

    public void setTarget(EntityLivingBase entity)
    {
        if (entity == null)
        {
            rotationCenter = new Vec3Container(() -> 0.0, () -> 0.0, () -> 0.0);
        }
        else
        {
            rotationCenter = Vec3Container.wrap(entity);
        }

        target = entity;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        setRotationOffset(nbt.getFloat("RotationOffset"));
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

        setTarget(null);
        setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("RotationOffset", getRotationOffset());

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
        if (ownerEntity != null)
        {
            return ownerEntity;
        }

        try
        {
            UUID uuid = UUID.fromString(func_152113_b());
            return ownerEntity = uuid == null ? null : worldObj.func_152378_a(uuid);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    public boolean isOwner(Entity entity)
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
    public ItemStack getHeldItem()
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.getHeldItem() : null;
    }

    @Override
    public ItemStack getEquipmentInSlot(int slot)
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.getEquipmentInSlot(slot) : null;
    }

    @Override
    public void setCurrentItemOrArmor(int slot, ItemStack stack)
    {
    }

    @Override
    public ItemStack[] getLastActiveItems()
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.getLastActiveItems() : new ItemStack[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInvisibleToPlayer(EntityPlayer player)
    {
        EntityLivingBase owner = getOwner();
        return owner != player && (owner != null ? owner.isInvisibleToPlayer(player) : super.isInvisibleToPlayer(player));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean getAlwaysRenderNameTagForRender()
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.getAlwaysRenderNameTagForRender() : super.getAlwaysRenderNameTagForRender();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getItemIcon(ItemStack stack, int ticksUsing)
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.getItemIcon(stack, ticksUsing) : super.getItemIcon(stack, ticksUsing);
    }

    @Override
    public boolean isInvisible()
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.isInvisible() : super.isInvisible();
    }

    @Override
    public boolean isPushedByWater()
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.isPushedByWater() : super.isPushedByWater();
    }

    @Override
    public float getAIMoveSpeed()
    {
        EntityLivingBase owner = getOwner();
        return owner != null ? owner.getAIMoveSpeed() : super.getAIMoveSpeed();
    }

    @Override
    public void readSpawnData(ByteBuf buf)
    {
        Entity entity = worldObj.getEntityByID(buf.readInt());

        if (entity instanceof EntityLivingBase)
        {
            setTarget((EntityLivingBase) entity);
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        buf.writeInt(target != null ? target.getEntityId() : -1);
    }
}
