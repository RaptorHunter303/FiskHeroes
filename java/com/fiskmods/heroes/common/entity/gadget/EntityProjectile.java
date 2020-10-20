package com.fiskmods.heroes.common.entity.gadget;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityProjectile extends EntityTrickArrow
{
    public EntityProjectile(World world)
    {
        super(world);
    }

    public EntityProjectile(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityProjectile(World world, EntityLivingBase shooter)
    {
        super(world, shooter, 1);
    }

    @Override
    public ItemStack getArrowItem()
    {
        return null;
    }

    @Override
    public EntityTrickArrow setArrowItem(ItemStack itemstack)
    {
        return this;
    }

    @Override
    public String getHero()
    {
        return "";
    }

    @Override
    public EntityTrickArrow setHero(String s)
    {
        return this;
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void setIsCritical(boolean flag)
    {
    }

    @Override
    public boolean getIsCritical()
    {
        return false;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (ticksInGround >= 100 && getDamage() > 0)
        {
            setDead();
        }
    }

    @Override
    protected void handlePostDamageEffects(EntityLivingBase entityHit)
    {
        int k = getKnockbackStrength();

        if (k > 0)
        {
            float f3 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

            if (f3 > 0.0F)
            {
                double knockback = k * 0.6000000238418579D / f3;
                entityHit.addVelocity(motionX * knockback, 0.1D, motionZ * knockback);
            }
        }

        if (shootingEntity instanceof EntityLivingBase)
        {
            EnchantmentHelper.func_151384_a(entityHit, shootingEntity);
            EnchantmentHelper.func_151385_b((EntityLivingBase) shootingEntity, entityHit);
        }
    }

    @Override
    protected void onImpactEntity(MovingObjectPosition mop)
    {
        if (mop.entityHit != null && !(mop.entityHit instanceof EntityHanging))
        {
            shootingEntity = getShooter();
            float dmg = calculateDamage(mop.entityHit);

            if (dmg > 0)
            {
                if (isBurningInternal() && canTargetEntity(mop.entityHit))
                {
                    SHHelper.ignite(mop.entityHit, 5);
                }

                if (mop.entityHit.attackEntityFrom(getDamageSource(mop.entityHit), dmg))
                {
                    if (mop.entityHit instanceof EntityLivingBase)
                    {
                        handlePostDamageEffects((EntityLivingBase) mop.entityHit);

                        if (shootingEntity instanceof EntityPlayerMP && mop.entityHit != shootingEntity && mop.entityHit instanceof EntityPlayer)
                        {
                            ((EntityPlayerMP) shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                        }
                    }

                    // playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));

                    if (canTargetEntity(mop.entityHit))
                    {
                        setDead();
                    }
                }
                else
                {
                    motionX *= -0.10000000149011612D;
                    motionY *= -0.10000000149011612D;
                    motionZ *= -0.10000000149011612D;
                    rotationYaw += 180.0F;
                    prevRotationYaw += 180.0F;
                    ticksInAir = 0;
                }
            }
        }
    }

    @Override
    protected void onImpactBlock(MovingObjectPosition mop)
    {
        xTile = mop.blockX;
        yTile = mop.blockY;
        zTile = mop.blockZ;
        inTile = worldObj.getBlock(xTile, yTile, zTile);
        inData = worldObj.getBlockMetadata(xTile, yTile, zTile);
        motionX = (float) (mop.hitVec.xCoord - posX);
        motionY = (float) (mop.hitVec.yCoord - posY);
        motionZ = (float) (mop.hitVec.zCoord - posZ);
        float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        posX -= motionX / f2 * 0.05000000074505806D;
        posY -= motionY / f2 * 0.05000000074505806D;
        posZ -= motionZ / f2 * 0.05000000074505806D;
        // playSound("random.bowhit", 1.0F, 0.8F / (rand.nextFloat() * 0.2F + 0.9F));
        inGround = true;
        arrowShake = 7;
        setIsCritical(false);

        if (inTile.getMaterial() != Material.air)
        {
            inTile.onEntityCollidedWithBlock(worldObj, xTile, yTile, zTile, this);
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player)
    {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setShort("xTile", (short) xTile);
        compound.setShort("yTile", (short) yTile);
        compound.setShort("zTile", (short) zTile);
        compound.setShort("life", (short) ticksInGround);
        compound.setInteger("inTile", Block.getIdFromBlock(inTile));
        compound.setByte("inData", (byte) inData);
        compound.setByte("shake", (byte) arrowShake);
        compound.setByte("inGround", (byte) (inGround ? 1 : 0));
        compound.setDouble("damage", getDamage());

        if ((shooterName == null || shooterName.length() == 0) && shootingEntity instanceof EntityPlayer)
        {
            shooterName = shootingEntity.getCommandSenderName();
        }

        compound.setString("shooter", shooterName == null ? "" : shooterName);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        xTile = compound.getShort("xTile");
        yTile = compound.getShort("yTile");
        zTile = compound.getShort("zTile");
        ticksInGround = compound.getShort("life");
        inTile = Block.getBlockById(compound.getInteger("inTile"));
        inData = compound.getByte("inData") & 255;
        arrowShake = compound.getByte("shake") & 255;
        inGround = compound.getByte("inGround") == 1;
        shooterName = compound.getString("shooter");
        setDamage(compound.getDouble("damage"));

        if (shooterName != null && shooterName.length() == 0)
        {
            shooterName = null;
        }
    }
}
