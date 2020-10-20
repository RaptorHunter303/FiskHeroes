package com.fiskmods.heroes.pack.accessor;

import com.fiskmods.heroes.util.NBTHelper;

import net.minecraft.nbt.NBTTagCompound;

public class JSNBTAccessor implements JSAccessor<JSNBTAccessor>
{
    public static final JSNBTAccessor EMPTY = new JSNBTAccessor(new NBTTagCompound());

    private final NBTTagCompound tag;

    private JSNBTAccessor(NBTTagCompound compound)
    {
        tag = compound;
    }

    @Override
    public boolean matches(JSNBTAccessor t)
    {
        return tag.equals(t.tag);
    }

    public static JSNBTAccessor wrap(NBTTagCompound compound)
    {
        return compound != null ? new JSNBTAccessor(compound) : EMPTY;
    }

    public boolean isEmpty()
    {
        return tag.hasNoTags();
    }

    public boolean hasKey(String key)
    {
        return tag.hasKey(key);
    }

    public byte getByte(String key)
    {
        return tag.getByte(key);
    }

    public short getShort(String key)
    {
        return tag.getShort(key);
    }

    public int getInteger(String key)
    {
        return tag.getInteger(key);
    }

    public long getLong(String key)
    {
        return tag.getLong(key);
    }

    public float getFloat(String key)
    {
        return tag.getFloat(key);
    }

    public double getDouble(String key)
    {
        return tag.getDouble(key);
    }

    public String getString(String key)
    {
        return tag.getString(key);
    }

    public byte[] getByteArray(String key)
    {
        return tag.getByteArray(key);
    }

    public boolean getBoolean(String key)
    {
        return tag.getBoolean(key);
    }

    public JSNBTAccessor getCompoundTag(String key)
    {
        return wrap(tag.getCompoundTag(key));
    }

    @Override
    public String toString()
    {
        return tag.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj instanceof String)
        {
            return tag.equals(NBTHelper.fromJson(String.valueOf(obj)));
        }
        else if (obj instanceof JSNBTAccessor)
        {
            return tag.equals(((JSNBTAccessor) obj).tag);
        }

        return toString().equals(String.valueOf(obj));
    }
}
