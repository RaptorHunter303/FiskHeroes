package com.fiskmods.heroes.util;

import com.fiskmods.heroes.common.data.SHEntityData;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.network.MessageSetTemperature;
import com.fiskmods.heroes.common.network.SHNetworkManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class TemperatureHelper
{
    public static final float DEFAULT_BODY_TEMPERATURE = 36.0F;

    public static float getCurrentBiomeTemperature(EntityLivingBase entity)
    {
        return getCurrentBiomeTemperature(entity.worldObj, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.boundingBox.minY), MathHelper.floor_double(entity.posZ));
    }

    public static float getCurrentBiomeTemperature(World world, int x, int y, int z)
    {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        float f = biome.getFloatTemperature(x, y, z);
        return f * 70 / 2 - 10;
    }

    public static float getCurrentBodyTemperature(EntityLivingBase entity)
    {
        return getTemperature(entity);
    }

    @SideOnly(Side.CLIENT)
    public static int getTemperatureForGui()
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        float temp = getCurrentBodyTemperature(player);

        if (temp != DEFAULT_BODY_TEMPERATURE)
        {
            float diff = temp - DEFAULT_BODY_TEMPERATURE;
            int amount = 7 - (int) (7 * Math.min(temp / DEFAULT_BODY_TEMPERATURE, 1));

            if (diff < 0)
            {
                amount *= -1;
            }

            return MathHelper.clamp_int(amount, -7, 7);
        }

        return 0;
    }

    public static void updateTemperature(EntityLivingBase entity)
    {
        float temp = getCurrentBodyTemperature(entity);
        float newTemp = temp;

        if (temp > DEFAULT_BODY_TEMPERATURE)
        {
            newTemp -= 0.01F;
        }
        else if (temp < DEFAULT_BODY_TEMPERATURE)
        {
            newTemp += 0.01F;
        }

        if (newTemp < 0)
        {
            newTemp = 0;
        }

        newTemp = (float) Math.round(newTemp * 100) / 100;
        setTemperatureWithoutNotify(entity, newTemp);

        // if (shouldFreeze(entity) && (entity.ticksExisted + entity.getEntityId()) % 30 == 0)
        // {
        // entity.attackEntityFrom(ModDamageSources.freeze, 1.0F);
        // }
    }

    public static boolean shouldFreeze(EntityLivingBase entity)
    {
        return getCurrentBodyTemperature(entity) < DEFAULT_BODY_TEMPERATURE;
    }

    public static void setTemperatureWithoutNotify(EntityLivingBase entity, float temperature)
    {
        float prevTemperature = SHEntityData.getData(entity).temperature;

        if (SHHelper.hasEnabledModifier(entity, Ability.COLD_RESISTANCE))
        {
            if (temperature < prevTemperature)
            {
                temperature = prevTemperature - (prevTemperature - temperature) / 2;
            }
        }

        SHEntityData.getData(entity).temperature = temperature;
    }

    public static void setTemperature(EntityLivingBase entity, float temperature)
    {
        if (getTemperature(entity) != temperature)
        {
            if (!entity.worldObj.isRemote)
            {
                SHNetworkManager.wrapper.sendToDimension(new MessageSetTemperature(entity, temperature), entity.dimension);
            }

            setTemperatureWithoutNotify(entity, temperature);
        }
    }

    public static float getTemperature(EntityLivingBase entity)
    {
        return SHEntityData.getData(entity).temperature;
    }
}
