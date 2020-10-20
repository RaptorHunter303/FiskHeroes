package com.fiskmods.heroes.common.data;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.util.FiskServerUtils;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class SHDataInterp<T> extends SHData<T>
{
    protected final SHData<T> prevData;

    public SHDataInterp(T defaultValue)
    {
        super(defaultValue);
        prevData = new SHData(defaultValue).setExempt(SAVE_NBT | SYNC_BYTES | COMMAND_ACCESSIBLE);
    }

    public SHDataInterp(T defaultValue, Predicate<Entity> canSet)
    {
        super(defaultValue, canSet);
        prevData = new SHData(defaultValue, canSet).setExempt(SAVE_NBT | SYNC_BYTES | COMMAND_ACCESSIBLE);
    }

    @Override
    protected SHDataInterp setExempt(int exempt)
    {
        prevData.setExempt(exempt);
        return (SHDataInterp) super.setExempt(exempt);
    }

    @Override
    protected SHDataInterp revokePerms(Side side)
    {
        prevData.revokePerms(side);
        return (SHDataInterp) super.revokePerms(side);
    }

    public void update(Entity entity)
    {
        prevData.setWithoutNotify(entity, get(entity));
    }

    public SHData<T> getPrevData()
    {
        return prevData;
    }

    public T getPrev(EntityPlayer player)
    {
        return prevData.get(player);
    }

    public T getPrev(Entity entity)
    {
        return prevData.get(entity);
    }

    public T interpolate(Entity entity, float progress)
    {
        if (progress == 1)
        {
            return get(entity);
        }
        else if (ofType(Float.class))
        {
            return (T) Float.valueOf(FiskServerUtils.interpolate((Float) getPrev(entity), (Float) get(entity), progress));
        }
        else if (ofType(Double.class))
        {
            return (T) Double.valueOf(FiskServerUtils.interpolate((Double) getPrev(entity), (Double) get(entity), progress));
        }
        else
        {
            throw new RuntimeException("Cannot interpolate a non-decimal data type!");
        }
    }

    public T interpolate(Entity entity)
    {
        return interpolate(entity, FiskHeroes.proxy.getRenderTick());
    }

    @Override
    protected void init(Field field, String name) throws ClassNotFoundException
    {
        super.init(field, name);
        prevData.init(field, "PREV_" + name);
    }
}
