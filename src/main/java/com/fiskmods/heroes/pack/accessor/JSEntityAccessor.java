package com.fiskmods.heroes.pack.accessor;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.SHDataInterp;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.entity.EntityBookPlayer;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;

import net.minecraft.entity.EntityLivingBase;

public class JSEntityAccessor implements JSAccessor<JSEntityAccessor>
{
    public static final JSEntityAccessor NULL = new Null();

    private final EntityLivingBase entity;

    private JSEntityAccessor(EntityLivingBase entity)
    {
        this.entity = entity;
    }

    @Override
    public boolean matches(JSEntityAccessor t)
    {
        return getUUID().equals(t.getUUID());
    }

    public static JSEntityAccessor wrap(EntityLivingBase entity)
    {
        return entity != null ? new JSEntityAccessor(entity) : NULL;
    }

    public JSWorldAccessor world()
    {
        return JSWorldAccessor.wrap(entity.worldObj);
    }

    public JSItemAccessor getEquipmentInSlot(int slot)
    {
        return JSItemAccessor.wrap(entity.getEquipmentInSlot(slot));
    }

    public JSItemAccessor getHeldItem()
    {
        return JSItemAccessor.wrap(entity.getHeldItem());
    }

    public boolean hasPotionEffect(int id)
    {
        return entity.isPotionActive(id);
    }

    public boolean hasStatusEffect(String key)
    {
        return StatusEffect.has(entity, StatEffect.getEffectFromName(key));
    }

    public boolean hasAchievement(String key)
    {
        return StatusEffect.has(entity, StatEffect.getEffectFromName(key));
    }

    public double getHealth()
    {
        return entity.getHealth();
    }

    public double getMaxHealth()
    {
        return entity.getMaxHealth();
    }

    public boolean isSneaking()
    {
        return entity.isSneaking();
    }

    public boolean isSprinting()
    {
        return entity.isSprinting();
    }

    public boolean isBurning()
    {
        return entity.isBurning();
    }

    public boolean isOnGround()
    {
        return entity.onGround;
    }

    public boolean isAlive()
    {
        return entity.isEntityAlive();
    }

    public boolean isInvulnerable()
    {
        return entity.isEntityInvulnerable();
    }

    public boolean isInWater()
    {
        return entity.isInWater();
    }

    public boolean isDisplayStand()
    {
        return entity instanceof EntityDisplayMannequin;
    }

    public boolean isBookPlayer()
    {
        return entity instanceof EntityBookPlayer;
    }

    public String getName()
    {
        return entity.getCommandSenderName();
    }

    public String getUUID()
    {
        return entity.getUniqueID().toString();
    }

    public double posX()
    {
        return entity.posX;
    }

    public double posY()
    {
        return entity.boundingBox.minY;
    }

    public double posZ()
    {
        return entity.posX;
    }

    public JSVectorAccessor pos()
    {
        return JSVectorAccessor.wrap(posX(), posY(), posZ());
    }

    public Object getData(String key)
    {
        return SHData.getJSWrappedDataValue(key, entity, d -> d.get(entity));
    }

    public Object getInterpolatedData(String key)
    {
        return SHData.getJSWrappedDataValue(key, entity, d -> d instanceof SHDataInterp ? ((SHDataInterp) d).interpolate(entity) : d.get(entity));
    }

    public void playSound(String sound, double volume, double pitch)
    {
        entity.playSound(sound, (float) volume, (float) pitch);
    }

    private static class Null extends JSEntityAccessor
    {
        public Null()
        {
            super(null);
        }

        @Override
        public JSWorldAccessor world()
        {
            return JSWorldAccessor.NULL;
        }

        @Override
        public JSItemAccessor getEquipmentInSlot(int slot)
        {
            return JSItemAccessor.EMPTY;
        }

        @Override
        public JSItemAccessor getHeldItem()
        {
            return JSItemAccessor.EMPTY;
        }

        @Override
        public boolean hasPotionEffect(int id)
        {
            return false;
        }

        @Override
        public boolean hasStatusEffect(String key)
        {
            return false;
        }

        @Override
        public double getHealth()
        {
            return 0;
        }

        @Override
        public double getMaxHealth()
        {
            return 0;
        }

        @Override
        public boolean isSneaking()
        {
            return false;
        }

        @Override
        public boolean isSprinting()
        {
            return false;
        }

        @Override
        public boolean isBurning()
        {
            return false;
        }

        @Override
        public boolean isOnGround()
        {
            return false;
        }

        @Override
        public boolean isAlive()
        {
            return false;
        }

        @Override
        public boolean isInvulnerable()
        {
            return false;
        }

        @Override
        public boolean isInWater()
        {
            return false;
        }

        @Override
        public boolean isDisplayStand()
        {
            return false;
        }

        @Override
        public boolean isBookPlayer()
        {
            return false;
        }

        @Override
        public String getName()
        {
            return "null";
        }

        @Override
        public String getUUID()
        {
            return "null";
        }

        @Override
        public double posX()
        {
            return 0;
        }

        @Override
        public double posY()
        {
            return 0;
        }

        @Override
        public double posZ()
        {
            return 0;
        }

        @Override
        public void playSound(String sound, double volume, double pitch)
        {
        }
    }
}
