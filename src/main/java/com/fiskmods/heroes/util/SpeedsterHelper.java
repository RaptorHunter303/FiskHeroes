package com.fiskmods.heroes.util;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.SHResourceHandler;
import com.fiskmods.heroes.client.json.trail.ITrailAspect;
import com.fiskmods.heroes.client.json.trail.JsonTrail;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectTrail;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.item.ItemTachyonDevice;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.tileentity.TileEntityTreadmill;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class SpeedsterHelper
{
    public static int getSuitBaseSpeed(EntityLivingBase entity, Hero hero)
    {
        return MathHelper.floor_double(SHAttributes.BASE_SPEED_LEVELS.get(entity, hero, -1));
    }

    public static List<SpeedBar> getSpeedSettingList(EntityPlayer player)
    {
        List<SpeedBar> list = new ArrayList<>();
        Hero hero = SHHelper.getHero(player);

        if (hero != null && hero.hasEnabledModifier(player, Ability.SUPER_SPEED))
        {
            int levels = getSuitBaseSpeed(player, hero);

            for (int i = 0; i <= levels; ++i)
            {
                list.add(SpeedBar.SUIT);
            }

            for (int i = 0; i < SHData.SPEED_EXPERIENCE_LEVEL.get(player); ++i)
            {
                list.add(SpeedBar.NATURAL);
            }
        }

        StatusEffect effectVelocity9 = StatusEffect.get(player, StatEffect.VELOCITY_9);
        StatusEffect effectSpeedSickness = StatusEffect.get(player, StatEffect.SPEED_SICKNESS);

        if (effectVelocity9 != null)
        {
            for (int i = 0; i < (effectVelocity9.amplifier + 1) * 3; ++i)
            {
                list.add(SpeedBar.VELOCITY9);
            }
        }

        if (effectSpeedSickness != null)
        {
            for (int i = 0; i <= effectSpeedSickness.amplifier && !list.isEmpty(); ++i)
            {
                list.remove(0);
            }
        }

        if (hero != null && hero.hasModifier(Ability.SUPER_SPEED))
        {
            if (hasTachyonDevice(player))
            {
                int i = MathHelper.floor_double(4 * SHData.TACHYON_CHARGE.get(player));

                for (int j = 0; j < i; ++j)
                {
                    list.add(SpeedBar.TACHYON);
                }
            }

            if (getTachyonCharge(player) > 0)
            {
                float f = getTachyonCharge(player);
                f = f > 0.5F ? 1.0F : f * 2;
                int i = MathHelper.floor_double(f * 3 + 1);

                for (int j = 0; j < i; ++j)
                {
                    list.add(SpeedBar.TACHYON);
                }
            }
        }

        return list;
    }

    public static int getMaxSpeedSetting(EntityPlayer player)
    {
        return getSpeedSettingList(player).size() - 1;
    }

    public static int getMaxSpeedSetting(EntityPlayer player, SpeedBar bar)
    {
        List<SpeedBar> list = getSpeedSettingList(player);
        int size = 0;

        for (SpeedBar element : list)
        {
            if (element == bar)
            {
                ++size;
            }
        }

        return size - 1;
    }

    public static int getFilteredMaxSpeedSetting(EntityPlayer player, SpeedBar bar)
    {
        List<SpeedBar> list = getSpeedSettingList(player);
        int size = 0;

        for (SpeedBar element : list)
        {
            if (element != bar)
            {
                ++size;
            }
        }

        return size - 1;
    }

    public static float getPlayerTopSpeed(EntityPlayer player)
    {
        float f = (SHData.SPEED.get(player) + 1) * 50 * Math.min(TemperatureHelper.getCurrentBodyTemperature(player) / TemperatureHelper.DEFAULT_BODY_TEMPERATURE, 1);
        return Math.max(f, 20);
    }

    public static JsonTrail getJsonTrail(EntityLivingBase entity)
    {
        HeroRenderer renderer = HeroRenderer.get(SHHelper.getHeroIter(entity));

        if (renderer != null)
        {
            HeroEffectTrail effect = renderer.getAnyEffect(HeroEffectTrail.class);

            if (effect != null)
            {
                return effect.getType(entity);
            }
        }

        return SHResourceHandler.getV9Trail();
    }

    public static ITrailAspect getTrailLightning(EntityLivingBase entity)
    {
        return getJsonTrail(entity).getTrailLightning();
    }

    public static boolean canPlayerRun(EntityPlayer player)
    {
        return SHData.SPEEDING.get(player) && hasSuperSpeed(player);
    }

    public static boolean canPlayerRunOnWater(EntityPlayer player)
    {
        return canPlayerRun(player) && SHData.SPEED.get(player) >= 3 && !player.isInWater() && player.isSprinting();
    }

    public static boolean canPlayerRunUpWall(EntityPlayer player)
    {
        return canPlayerRun(player) && SHData.SPEED.get(player) >= 3 && player.isSprinting();
    }

    public static boolean hasSuperSpeed(EntityLivingBase entity)
    {
        return (isSpeedster(entity) || StatusEffect.has(entity, StatEffect.VELOCITY_9)) && Ability.SUPER_SPEED.isActive(entity);
    }

    public static boolean isSpeedster(EntityLivingBase entity)
    {
        return SHHelper.hasEnabledModifier(entity, Ability.SUPER_SPEED);
    }

    public static boolean areAllPlayersSlowMotion(World world)
    {
        List<EntityPlayer> players = world.playerEntities;

        for (EntityPlayer player : players)
        {
            if (!player.isEntityAlive())
            {
                return false;
            }

            Hero hero = SHHelper.getHero(player);

            if (hero == null || !hero.hasEnabledModifier(player, Ability.ACCELERATED_PERCEPTION) || !SHData.SLOW_MOTION.get(player))
            {
                return false;
            }
        }

        return true;
    }

    public static TileEntityTreadmill getTreadmill(EntityPlayer player)
    {
        for (TileEntity tileentity : (List<TileEntity>) player.worldObj.loadedTileEntityList)
        {
            if (tileentity instanceof TileEntityTreadmill)
            {
                TileEntityTreadmill tile = (TileEntityTreadmill) tileentity;

                if (tile.playerId == player.getEntityId())
                {
                    return tile;
                }
            }
        }

        return null;
    }

    public static boolean isOnTreadmill(EntityPlayer player)
    {
        return getTreadmill(player) != null;
    }

    public static byte locateEquippedTachyonDevice(EntityPlayer player)
    {
        InventoryPlayer inventory = player.inventory;
        byte slot = -1;

        for (byte b = 0; b < inventory.mainInventory.length; ++b)
        {
            ItemStack itemstack = inventory.mainInventory[b];

            if (itemstack != null && itemstack.getItem() == ModItems.tachyonDevice)
            {
                if (slot < 0)
                {
                    slot = b;
                }

                if (ItemTachyonDevice.getCharge(itemstack) > 0)
                {
                    return b;
                }
            }
        }

        return slot;
    }

    public static float getTachyonCharge(EntityPlayer player)
    {
        Hero hero = SHHelper.getHero(player);
        float f = 0;

        if (hero == null)
        {
            return f;
        }

        for (int i = 0; i < 4; ++i)
        {
            ItemStack itemstack = player.getCurrentArmor(i);

            if (itemstack != null && itemstack.hasTagCompound())
            {
                f += itemstack.getTagCompound().getInteger("TachyonCharge");
            }
        }

        f /= hero.getPiecesToSet();
        f /= SHConstants.MAX_CHARGE_ARMOR;

        return f;
    }

    public static ItemStack getEquippedTachyonDevice(EntityPlayer player)
    {
        return FiskServerUtils.getStackInSlot(player, SHData.EQUIPPED_TACHYON_DEVICE_SLOT.get(player));
    }

    public static boolean hasTachyonDevice(EntityLivingBase entity)
    {
        return SHData.EQUIPPED_TACHYON_DEVICE_SLOT.get(entity) >= 0 && (entity.getHeldItem() == null || entity.getHeldItem().getItem() != ModItems.tachyonDevice);
    }

    public enum SpeedBar
    {
        SUIT,
        NATURAL(0x80B200),
        TACHYON(0xB23333),
        VELOCITY9(0x333380);

        private final Vec3 color;

        private SpeedBar(int hex)
        {
            float r = (hex >> 16 & 255) / 255F;
            float g = (hex >> 8 & 255) / 255F;
            float b = (hex & 255) / 255F;
            color = Vec3.createVectorHelper(r, g, b);
        }

        private SpeedBar()
        {
            this(-1);
        }

        public Vec3 getColor(EntityLivingBase entity)
        {
            return this == SUIT ? getTrailLightning(entity).getVecColor() : color;
        }
    }
}
