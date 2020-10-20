package com.fiskmods.heroes.common.data.effect;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fiskmods.heroes.common.achievement.SHAchievements;
import com.fiskmods.heroes.common.data.SHEntityData;
import com.fiskmods.heroes.common.network.MessageUpdateEffects;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.google.common.primitives.Doubles;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public class StatusEffect implements Comparable<StatusEffect>
{
    public StatEffect effect;
    public int duration;
    public int amplifier;

    public StatusEffect(StatEffect effect, int duration, int amplifier)
    {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier & 0xFF;
    }

    public static StatusEffect fromBytes(ByteBuf buf)
    {
        StatEffect e = StatEffect.getEffectById(buf.readByte() & 0xFF);
        short duration = buf.readShort();
        byte amplifier = buf.readByte();

        if (e != null)
        {
            return new StatusEffect(e, duration & 0xFFFF, amplifier & 0xFF);
        }

        return null;
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(StatEffect.getIdFromEffect(effect));
        buf.writeShort(duration);
        buf.writeByte(amplifier);
    }

    public static StatusEffect readFromNBT(NBTTagCompound tag)
    {
        StatEffect effect = StatEffect.getEffectFromName(tag.getString("Id"));

        if (effect != null)
        {
            return new StatusEffect(effect, tag.getShort("Duration") & 0xFFFF, tag.getByte("Amplifier") & 0xFF);
        }

        return null;
    }

    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("Id", effect.delegate.name());
        nbt.setShort("Duration", (short) duration);
        nbt.setByte("Amplifier", (byte) amplifier);

        return nbt;
    }

    public boolean isMaxDuration()
    {
        return duration >= Short.MAX_VALUE;
    }

    public static void add(EntityLivingBase entity, StatEffect effect, int duration, int amplifier)
    {
        if (!entity.worldObj.isRemote)
        {
            List<StatusEffect> list = get(entity);
            duration = MathHelper.clamp_int(duration, 0, Short.MAX_VALUE);

            for (StatusEffect status : list)
            {
                if (status.effect == effect)
                {
                    int prevDuration = status.duration;
                    int prevAmplifier = status.amplifier;

                    status.duration = Math.max(status.duration, duration) & 0xFFFF;
                    status.amplifier = Math.max(status.amplifier, amplifier) & 0xFF;

                    if (prevDuration != status.duration || prevAmplifier != status.amplifier)
                    {
                        Collections.sort(list);
                        SHNetworkManager.wrapper.sendToDimension(new MessageUpdateEffects(entity, list), entity.dimension);
                    }

                    return;
                }
            }

            if (entity instanceof EntityPlayer)
            {
                boolean flag = true;

                for (StatEffect effect1 : StatEffect.REGISTRY)
                {
                    if (effect1.isNegative() && effect1 != StatEffect.SPEED_DAMPENER && !has(entity, effect1) && effect != effect1)
                    {
                        flag = false;
                        break;
                    }
                }

                if (flag)
                {
                    ((EntityPlayer) entity).triggerAchievement(SHAchievements.ALL_DEBUFFS);
                }
            }

            list.add(new StatusEffect(effect, duration, amplifier));
            Collections.sort(list);

            SHNetworkManager.wrapper.sendToDimension(new MessageUpdateEffects(entity, list), entity.dimension);
        }
    }

    public static List<StatusEffect> get(EntityLivingBase entity)
    {
        return SHEntityData.getData(entity).activeEffects;
    }

    public static StatusEffect get(EntityLivingBase entity, StatEffect effect)
    {
        for (StatusEffect status : get(entity))
        {
            if (status.effect == effect)
            {
                return status;
            }
        }

        return null;
    }

    public static boolean has(EntityLivingBase entity, StatEffect effect)
    {
        return get(entity, effect) != null;
    }

    public static void clear(EntityLivingBase entity)
    {
        if (!entity.worldObj.isRemote)
        {
            List<StatusEffect> list = get(entity);

            if (!list.isEmpty())
            {
                list.clear();
                SHNetworkManager.wrapper.sendToDimension(new MessageUpdateEffects(entity, list), entity.dimension);
            }
        }
    }

    public static void clear(EntityLivingBase entity, StatEffect effect)
    {
        if (!entity.worldObj.isRemote && has(entity, effect))
        {
            List<StatusEffect> list = get(entity);
            Iterator<StatusEffect> iter = list.iterator();

            while (iter.hasNext())
            {
                if (iter.next().effect == effect)
                {
                    iter.remove();
                }
            }

            if (!list.isEmpty())
            {
                Collections.sort(list);
            }

            SHNetworkManager.wrapper.sendToDimension(new MessageUpdateEffects(entity, list), entity.dimension);
        }
    }

    @Override
    public int compareTo(StatusEffect o)
    {
        return Doubles.compare(o.duration, duration);
    }
}
