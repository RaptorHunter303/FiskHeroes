package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectEnergyBolt;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityLaserBolt extends EntityThrowable implements IEntityAdditionalSpawnData
{
    private EntityLivingBase thrower;
    public boolean scoped;

    @SideOnly(Side.CLIENT)
    public Integer boltColor;
    public HeroIteration shooterSuit;

    public EntityLaserBolt(World world)
    {
        super(world);
    }

    public EntityLaserBolt(World world, EntityLivingBase entity, Type type, HeroIteration iter, boolean scopeDamage)
    {
        super(world, entity);
        ignoreFrustumCheck = true;
        shooterSuit = iter;
        thrower = entity;
        setType(type);

        float f1 = SHData.SCOPE_TIMER.get(entity);

        if (scopeDamage && f1 > 0)
        {
            scoped = true;
        }

        f1 = 1 - f1;

        if (f1 < 1)
        {
            setLocationAndAngles(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, entity.rotationYaw, entity.rotationPitch);
            posX -= MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F * f1;
            posY -= 0.1;
            posZ -= MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F * f1;
            setPosition(posX, posY, posZ);
            float f = 0.4F;
            motionX = -MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * f;
            motionZ = MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * f;
            motionY = -MathHelper.sin((rotationPitch + func_70183_g()) / 180.0F * (float) Math.PI) * f;

            setThrowableHeading(motionX, motionY, motionZ, func_70182_d(), f1);
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (ticksExisted > 120)
        {
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.001F;
    }

    @Override
    protected float func_70182_d()
    {
        return 4.0F;
    }

    @Override
    protected void onImpact(MovingObjectPosition mop)
    {
        if (mop.entityHit != null && getThrower() != null)
        {
            float damage = Rule.DMG_LASERBOLT.getHero(getThrower()) * getType().damage;

            if (scoped)
            {
                damage *= 1.6F;
            }

            mop.entityHit.hurtResistantTime = 0;
            mop.entityHit.attackEntityFrom(ModDamageSources.causeLaserDamage(this, getThrower()), damage);
        }

        if (!getType().explosion)
        {
            Vec3 v = mop.hitVec;
            float f = 0.05F;

            for (int i = 0; i < 8; ++i)
            {
                worldObj.spawnParticle("smoke", v.xCoord, v.yCoord, v.zCoord, (Math.random() * 2 - 1) * f, (Math.random() * 2 - 1) * f, (Math.random() * 2 - 1) * f);
            }
        }
        else if (!worldObj.isRemote)
        {
            worldObj.createExplosion(getThrower(), posX, posY, posZ, 1.25F, false);
        }

        setDead();
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(2, (byte) Type.REVOLVER.ordinal());
    }

    public Type getType()
    {
        return Type.values()[dataWatcher.getWatchableObjectByte(2) % Type.values().length];
    }

    public void setType(Type type)
    {
        dataWatcher.updateObject(2, (byte) type.ordinal());
    }

    @SideOnly(Side.CLIENT)
    public int getColor()
    {
        if (shooterSuit != null)
        {
            if (boltColor == null)
            {
                HeroRenderer renderer = HeroRenderer.get(shooterSuit);
                HeroEffectEnergyBolt effect = renderer.getEffect(HeroEffectEnergyBolt.class, getThrower());

                if (effect != null)
                {
                    boltColor = effect.getColor();
                }
            }

            if (boltColor == null)
            {
                return 0xFF0000;
            }

            return boltColor;
        }

        return 0xFF0000;
    }

    @Override
    public EntityLivingBase getThrower()
    {
        return thrower;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound nbttagcompound)
    {
        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
    }

    @Override
    public void readSpawnData(ByteBuf buf)
    {
        Entity entity = worldObj.getEntityByID(buf.readInt());

        if (entity instanceof EntityLivingBase)
        {
            thrower = (EntityLivingBase) entity;
        }

        String s = ByteBufUtils.readUTF8String(buf);
        shooterSuit = s.isEmpty() ? null : HeroIteration.get(s);
    }

    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        buf.writeInt(thrower != null ? thrower.getEntityId() : -1);
        ByteBufUtils.writeUTF8String(buf, shooterSuit != null ? shooterSuit.getName() : "");
    }

    public enum Type
    {
        REVOLVER(1, false),
        RIFLE(1.5F, true),
        SUIT(1.75F, true),
        SNIPER(2.5F, false);

        public final float damage;
        public final boolean explosion;

        Type(float damage, boolean explosion)
        {
            this.damage = damage;
            this.explosion = explosion;
        }
    }
}
