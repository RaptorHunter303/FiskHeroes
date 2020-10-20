package com.fiskmods.heroes.common.entity;

import java.util.List;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.enchantment.SHEnchantments;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityThrownShield extends EntityThrowable implements IEntityAdditionalSpawnData
{
    private EntityLivingBase thrower;
    private String throwerName;

    private ItemStack shieldItem;
    private boolean electroMagnetic;

    public float spinTime, prevSpinTime;

    public EntityThrownShield(World world)
    {
        super(world);
        setSize(0.95F, 0.09375F);
    }

    public EntityThrownShield(World world, EntityLivingBase entity, ItemStack itemstack)
    {
        super(world, entity);
        thrower = entity;
        noClip = false;
        setSize(0.95F, 0.09375F);
        setShieldItem(itemstack);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(2, (byte) 0);
    }

    public boolean getShouldReturn()
    {
        return dataWatcher.getWatchableObjectByte(2) > 0;
    }

    public void setShouldReturn(boolean flag)
    {
        dataWatcher.updateObject(2, (byte) (flag ? 1 : 0));
    }

    public ItemStack getShieldItem()
    {
        return shieldItem;
    }

    public void setShieldItem(ItemStack itemstack)
    {
        shieldItem = itemstack;
        electroMagnetic = EnchantmentHelper.getEnchantmentLevel(SHEnchantments.electroMagnet.effectId, shieldItem) > 0;
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.04F;
    }

    @Override
    protected float func_70182_d()
    {
        return 2.5F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("ShouldReturn", getShouldReturn());

        if (shieldItem != null)
        {
            nbt.setTag("ShieldItem", shieldItem.writeToNBT(new NBTTagCompound()));
        }

        if ((throwerName == null || throwerName.length() == 0) && thrower != null && thrower instanceof EntityPlayer)
        {
            throwerName = thrower.getCommandSenderName();
        }

        nbt.setString("ownerName", throwerName == null ? "" : throwerName);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        setShouldReturn(nbt.getBoolean("ShouldReturn"));
        electroMagnetic = nbt.getBoolean("ElectroMagnetic");

        if (nbt.hasKey("ShieldItem", NBT.TAG_COMPOUND))
        {
            setShieldItem(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("ShieldItem")));
        }

        throwerName = nbt.getString("ownerName");

        if (throwerName != null && throwerName.length() == 0)
        {
            throwerName = null;
        }
    }

    @Override
    public void onUpdate()
    {
        if (!worldObj.isRemote && (getThrower() == null || !getThrower().isEntityAlive()))
        {
            setDead();
        }

        if (isEntityAlive() && getShouldReturn() && getThrower() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) getThrower();
            double dist = getDistanceToEntity(player);

            if (electroMagnetic && ticksExisted > 50)
            {
                Vec3 vec3 = Vec3.createVectorHelper(player.posX - posX, player.boundingBox.minY - posY + player.height * 0.6, player.posZ - posZ).normalize();
                double d = 0.2 + ticksExisted / 15F;

                motionX = vec3.xCoord * d;
                motionY = vec3.yCoord * d;
                motionZ = vec3.zCoord * d;
                noClip = true;
            }

            float velocity = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);

            if (!worldObj.isRemote && dist <= MathHelper.clamp_double(velocity * 3, 1, 5))
            {
                if (player.inventory.getCurrentItem() == null)
                {
                    player.setCurrentItemOrArmor(0, shieldItem);
                    shieldItem = null;
                }
                else if (player.inventory.addItemStackToInventory(shieldItem))
                {
                    shieldItem = null;
                }

                setDead();
            }
        }

        prevSpinTime = spinTime;

        if (spinTime == 0)
        {
            spinTime += rand.nextFloat() * 100;
            prevSpinTime = spinTime;
        }

        spinTime += MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);

        if (!getShouldReturn())
        {
            super.onUpdate();
        }
        else
        {
            super.onEntityUpdate();

            Vec3 pos = Vec3.createVectorHelper(posX, posY, posZ);
            Vec3 nextPos = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
            MovingObjectPosition mop = worldObj.rayTraceBlocks(pos, nextPos);

            pos = Vec3.createVectorHelper(posX, posY, posZ);
            nextPos = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);

            if (mop != null)
            {
                nextPos = Vec3.createVectorHelper(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
            }

            if (!worldObj.isRemote)
            {
                Entity entity = null;
                List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1, 1, 1));
                double minDist = 0;

                for (Entity entity1 : list)
                {
                    if (entity1.canBeCollidedWith() && (entity1 != getThrower() || ticksExisted >= 5))
                    {
                        AxisAlignedBB aabb = entity1.boundingBox.expand(0.3F, 0.3F, 0.3F);
                        MovingObjectPosition mop1 = aabb.calculateIntercept(pos, nextPos);

                        if (mop1 != null)
                        {
                            double dist = pos.distanceTo(mop1.hitVec);

                            if (dist < minDist || minDist == 0)
                            {
                                entity = entity1;
                                minDist = dist;
                            }
                        }
                    }
                }

                if (entity != null)
                {
                    mop = new MovingObjectPosition(entity);
                }
            }

            if (mop != null)
            {
                if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ) == Blocks.portal)
                {
                    setInPortal();
                }
                else
                {
                    onImpact(mop);
                }
            }

            float motion = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180 / Math.PI);

            for (rotationPitch = (float) (Math.atan2(motionY, motion) * 180 / Math.PI); rotationPitch - prevRotationPitch < -180; prevRotationPitch -= 360)
            {
                ;
            }

            while (rotationPitch - prevRotationPitch >= 180)
            {
                prevRotationPitch += 360;
            }

            while (rotationYaw - prevRotationYaw < -180)
            {
                prevRotationYaw -= 360;
            }

            while (rotationYaw - prevRotationYaw >= 180)
            {
                prevRotationYaw += 360;
            }

            rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
            rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            float f = 0.99F;
            float f1 = getGravityVelocity();

            if (isInWater())
            {
                for (int i = 0; i < 4; ++i)
                {
                    float f2 = 0.25F;
                    worldObj.spawnParticle("bubble", posX - motionX * f2, posY - motionY * f2, posZ - motionZ * f2, motionX, motionY, motionZ);
                }

                f = 0.8F;
            }

            motionX *= f;
            motionY *= f;
            motionZ *= f;
            motionY -= f1;
            setPosition(posX, posY, posZ);
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        if (getThrower() != null && mop.entityHit != getThrower())
        {
            float velocity = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            float f = 0.75F;

            if (mop.entityHit != null)
            {
                float damage = SHConstants.DMG_CPT_AMERICA_SHIELD + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, shieldItem) * 1.25F;

                if (mop.entityHit.attackEntityFrom(ModDamageSources.causeShieldDamage(this, getThrower()), damage * velocity))
                {
                    SHHelper.ignite(mop.entityHit, EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, shieldItem) * 4);
                }

                if (!worldObj.isRemote && shieldItem != null)
                {
                    shieldItem.damageItem(1, getThrower());
                }

                Vec3 motion = VectorHelper.multiply(VectorHelper.centerOf(this).subtract(VectorHelper.centerOf(getThrower())), velocity * f / 10);
                motionX = motion.xCoord;
                motionY = motion.yCoord + 0.2;
                motionZ = motion.zCoord;
            }
            else
            {
                int x = MathHelper.floor_double(posX);
                int y = MathHelper.floor_double(posY + getEyeHeight());
                int z = MathHelper.floor_double(posZ);

                if (velocity < 0.75F && electroMagnetic && ticksExisted < 50)
                {
                    ticksExisted = 50;
                }
                else if (!worldObj.isAirBlock(x, y, z) && worldObj.getBlock(x, y, z).isOpaqueCube())
                {
                    if (electroMagnetic)
                    {
                        if (ticksExisted < 50)
                        {
                            ticksExisted = 50;
                        }
                    }
                    else
                    {
                        setDead();
                    }
                }
                else if (velocity > 0.5F)
                {
                    ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
                    Block block = worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);

                    if (block.getMaterial() != Material.air)
                    {
                        block.onEntityCollidedWithBlock(worldObj, mop.blockX, mop.blockY, mop.blockZ, this);
                    }

                    if (block.getCollisionBoundingBoxFromPool(worldObj, mop.blockX, mop.blockY, mop.blockZ) != null && worldObj.isAirBlock(mop.blockX + dir.offsetX, mop.blockY + dir.offsetY, mop.blockZ + dir.offsetZ))
                    {
                        motionX *= (1 - Math.abs(dir.offsetX) * 2) * f;
                        motionY *= (1 - Math.abs(dir.offsetY) * 2) * f;
                        motionZ *= (1 - Math.abs(dir.offsetZ) * 2) * f;
                    }
                }
                else if (electroMagnetic)
                {
                    if (ticksExisted < 50)
                    {
                        ticksExisted = 50;
                    }
                }
                else
                {
                    setDead();
                }
            }

            worldObj.playSoundAtEntity(this, SHSounds.ITEM_SHIELD_HIT.toString(), 1.0F, 1.0F + (rand.nextFloat() - 0.5F) * 0.2F);
            setShouldReturn(true);
        }
    }

    @Override
    public void setDead()
    {
        if (!worldObj.isRemote && shieldItem != null && !isDead)
        {
            if (getThrower() instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) getThrower();

                if (getDistanceToEntity(player) <= 5)
                {
                    if (player.inventory.getCurrentItem() == null)
                    {
                        player.setCurrentItemOrArmor(0, shieldItem);
                        shieldItem = null;
                    }
                    else if (player.inventory.addItemStackToInventory(shieldItem))
                    {
                        shieldItem = null;
                    }
                }
            }

            if (shieldItem != null)
            {
                EntityItem entityitem = new EntityItem(worldObj);
                entityitem.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
                entityitem.setEntityItemStack(shieldItem);

                worldObj.spawnEntityInWorld(entityitem);
            }
        }

        super.setDead();
    }

    @Override
    public EntityLivingBase getThrower()
    {
        if (thrower == null && throwerName != null && throwerName.length() > 0)
        {
            thrower = worldObj.getPlayerEntityByName(throwerName);
        }

        return thrower;
    }

    @Override
    public void readSpawnData(ByteBuf buf)
    {
        Entity entity = worldObj.getEntityByID(buf.readInt());

        if (entity instanceof EntityLivingBase)
        {
            thrower = (EntityLivingBase) entity;
        }

        if (buf.readBoolean())
        {
            setShieldItem(ByteBufUtils.readItemStack(buf));
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        buf.writeInt(thrower != null ? thrower.getEntityId() : -1);
        buf.writeBoolean(shieldItem != null);

        if (shieldItem != null)
        {
            ByteBufUtils.writeItemStack(buf, shieldItem);
        }
    }
}
