package com.fiskmods.heroes.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.common.data.SHData.ClassType;
import com.fiskmods.heroes.common.registry.FiskRegistryEntry;
import com.fiskmods.heroes.common.registry.FiskSimpleRegistry;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public class NBTHelper
{
    private static final Map<Class<? extends INBTSavedObject>, INBTSaveAdapter> ADAPTERS = new HashMap<>();

    public static List<NBTBase> getTags(NBTTagList nbttaglist)
    {
        return SHReflection.tagListField.get(nbttaglist);
    }

    public static <T extends NBTBase> T merge(T source, T merging)
    {
        if (source == null || source == merging || source instanceof NBTTagCompound && ((NBTTagCompound) source).hasNoTags())
        {
            return merging;
        }

        if (source instanceof NBTTagCompound && merging instanceof NBTTagCompound)
        {
            NBTTagCompound compound = (NBTTagCompound) merging;
            NBTTagCompound merged = (NBTTagCompound) source.copy();

            Set<String> keys = compound.func_150296_c();

            for (String key : keys)
            {
                merged.setTag(key, merge(((NBTTagCompound) source).getTag(key), compound.getTag(key)));
            }

            return (T) merged;
        }

        return (T) merging.copy();
    }

    public static NBTBase writeToNBT(Object obj)
    {
        if (obj instanceof INBTSavedObject)
        {
            return ((INBTSavedObject) obj).writeToNBT();
        }
        else if (obj instanceof Byte)
        {
            return new NBTTagByte((Byte) obj);
        }
        else if (obj instanceof Short)
        {
            return new NBTTagShort((Short) obj);
        }
        else if (obj instanceof Integer)
        {
            return new NBTTagInt((Integer) obj);
        }
        else if (obj instanceof Long)
        {
            return new NBTTagLong((Long) obj);
        }
        else if (obj instanceof Float)
        {
            return new NBTTagFloat((Float) obj);
        }
        else if (obj instanceof Double)
        {
            return new NBTTagDouble((Double) obj);
        }
        else if (obj instanceof Boolean)
        {
            return new NBTTagByte((byte) ((Boolean) obj ? 1 : 0));
        }
        else if (obj instanceof String)
        {
            String s = (String) obj;

            if (s != null)
            {
                return new NBTTagString(s);
            }
        }
        else if (obj instanceof List)
        {
            List list = (List) obj;
            NBTTagList nbttaglist = new NBTTagList();

            for (Object element : list)
            {
                NBTBase tag = writeToNBT(element);

                if (tag != null)
                {
                    nbttaglist.appendTag(tag);
                }
            }

            return nbttaglist;
        }
        else if (obj instanceof ItemStack)
        {
            return ((ItemStack) obj).writeToNBT(new NBTTagCompound());
        }

        return null;
    }

    public static <T> T readFromNBT(NBTBase tag, Class<T> type)
    {
        return readFromNBT(tag, new ClassType<>(type));
    }

    public static <T> T readFromNBT(NBTBase tag, ClassType<T> typeClass)
    {
        if (INBTSavedObject.class.isAssignableFrom(typeClass.getType()))
        {
            if (ADAPTERS.containsKey(typeClass.getType()))
            {
                return (T) ADAPTERS.get(typeClass.getType()).readFromNBT(tag);
            }

            return null;
        }
        else if (tag instanceof NBTPrimitive || tag instanceof NBTTagString)
        {
            return getTagValue(tag, typeClass.getType());
        }
        else if (typeClass.getType() == List.class && tag instanceof NBTTagList)
        {
            List<NBTBase> tags = getTags((NBTTagList) tag);
            List list = new ArrayList<>();

            for (NBTBase tag2 : tags)
            {
                Object entry = readFromNBT(tag2, typeClass.getParamSafe());

                if (entry != null)
                {
                    list.add(entry);
                }
            }

            return (T) list;
        }
        else if (tag instanceof NBTTagCompound)
        {
            NBTTagCompound nbt = (NBTTagCompound) tag;

            if (typeClass.getType() == ItemStack.class)
            {
                return (T) ItemStack.loadItemStackFromNBT(nbt);
            }
        }

        return null;
    }

    public static <T> T getTagValue(NBTBase tag)
    {
        if (tag instanceof NBTPrimitive)
        {
            NBTPrimitive nbt = (NBTPrimitive) tag;

            if (tag instanceof NBTTagByte)
            {
                return (T) Byte.valueOf(nbt.func_150290_f());
            }
            else if (tag instanceof NBTTagShort)
            {
                return (T) Short.valueOf(nbt.func_150289_e());
            }
            else if (tag instanceof NBTTagInt)
            {
                return (T) Integer.valueOf(nbt.func_150287_d());
            }
            else if (tag instanceof NBTTagLong)
            {
                return (T) Long.valueOf(nbt.func_150291_c());
            }
            else if (tag instanceof NBTTagFloat)
            {
                return (T) Float.valueOf(nbt.func_150288_h());
            }
            else if (tag instanceof NBTTagDouble)
            {
                return (T) Double.valueOf(nbt.func_150286_g());
            }
        }
        else if (tag instanceof NBTTagString)
        {
            return (T) ((NBTTagString) tag).func_150285_a_();
        }

        return null;
    }

    public static <T> T getTagValue(NBTBase tag, Class<T> c)
    {
        if (tag instanceof NBTPrimitive)
        {
            NBTPrimitive nbt = (NBTPrimitive) tag;

            if (c == Byte.class)
            {
                return (T) Byte.valueOf(nbt.func_150290_f());
            }
            else if (c == Short.class)
            {
                return (T) Short.valueOf(nbt.func_150289_e());
            }
            else if (c == Integer.class)
            {
                return (T) Integer.valueOf(nbt.func_150287_d());
            }
            else if (c == Long.class)
            {
                return (T) Long.valueOf(nbt.func_150291_c());
            }
            else if (c == Float.class)
            {
                return (T) Float.valueOf(nbt.func_150288_h());
            }
            else if (c == Double.class)
            {
                return (T) Double.valueOf(nbt.func_150286_g());
            }
            else if (c == Boolean.class)
            {
                return (T) Boolean.valueOf(nbt.func_150290_f() != 0);
            }
        }
        else if (c == String.class && tag instanceof NBTTagString)
        {
            return (T) ((NBTTagString) tag).func_150285_a_();
        }

        return null;
    }

    public static void toBytes(ByteBuf buf, Object obj)
    {
        if (obj instanceof Byte)
        {
            buf.writeByte((Byte) obj);
        }
        else if (obj instanceof Short)
        {
            buf.writeShort((Short) obj);
        }
        else if (obj instanceof Integer)
        {
            buf.writeInt((Integer) obj);
        }
        else if (obj instanceof Long)
        {
            buf.writeLong((Long) obj);
        }
        else if (obj instanceof Float)
        {
            buf.writeFloat((Float) obj);
        }
        else if (obj instanceof Double)
        {
            buf.writeDouble((Double) obj);
        }
        else if (obj instanceof Boolean)
        {
            buf.writeBoolean((Boolean) obj);
        }
        else
        {
            buf.writeBoolean(obj != null);

            if (obj instanceof INBTSavedObject)
            {
                ((INBTSavedObject) obj).toBytes(buf);
            }
            else if (obj instanceof String)
            {
                ByteBufUtils.writeUTF8String(buf, (String) obj);
            }
            else if (obj instanceof List)
            {
                List list = (List) obj;
                buf.writeInt(list.size());

                for (Object element : list)
                {
                    toBytes(buf, element);
                }
            }
            else if (obj instanceof ItemStack)
            {
                ByteBufUtils.writeItemStack(buf, (ItemStack) obj);
            }
        }
    }

    public static <T> T fromBytes(ByteBuf buf, Class<T> type)
    {
        return fromBytes(buf, new ClassType<>(type));
    }

    public static <T> T fromBytes(ByteBuf buf, ClassType<T> typeClass)
    {
        if (typeClass.getType() == Byte.class)
        {
            return (T) Byte.valueOf(buf.readByte());
        }
        else if (typeClass.getType() == Short.class)
        {
            return (T) Short.valueOf(buf.readShort());
        }
        else if (typeClass.getType() == Integer.class)
        {
            return (T) Integer.valueOf(buf.readInt());
        }
        else if (typeClass.getType() == Long.class)
        {
            return (T) Long.valueOf(buf.readLong());
        }
        else if (typeClass.getType() == Float.class)
        {
            return (T) Float.valueOf(buf.readFloat());
        }
        else if (typeClass.getType() == Double.class)
        {
            return (T) Double.valueOf(buf.readDouble());
        }
        else if (typeClass.getType() == Boolean.class)
        {
            return (T) Boolean.valueOf(buf.readBoolean());
        }
        else if (buf.readBoolean())
        {
            if (INBTSavedObject.class.isAssignableFrom(typeClass.getType()))
            {
                if (ADAPTERS.containsKey(typeClass.getType()))
                {
                    return (T) ADAPTERS.get(typeClass.getType()).fromBytes(buf);
                }
            }
            else if (typeClass.getType() == String.class)
            {
                return (T) ByteBufUtils.readUTF8String(buf);
            }
            else if (typeClass.getType() == List.class)
            {
                List list = new ArrayList<>();
                int size = buf.readInt();

                for (int i = 0; i < size; ++i)
                {
                    Object entry = fromBytes(buf, typeClass.getParamSafe());

                    if (entry != null)
                    {
                        list.add(entry);
                    }
                }

                return (T) list;
            }
            else if (typeClass.getType() == ItemStack.class)
            {
                return (T) ByteBufUtils.readItemStack(buf);
            }
        }

        return null;
    }

    public static <T extends FiskRegistryEntry<T>> List<T> readNBTList(NBTTagCompound compound, String name, FiskSimpleRegistry<T> registry)
    {
        List<T> list = null;

        if (compound.hasKey(name, NBT.TAG_LIST))
        {
            NBTTagList tagList = compound.getTagList(name, NBT.TAG_STRING);
            list = new ArrayList<>();

            for (int i = 0; i < tagList.tagCount(); ++i)
            {
                T entry = registry.getObject(tagList.getStringTagAt(i));

                if (entry != null)
                {
                    list.add(entry);
                }
            }
        }

        return list;
    }

    public static NBTTagCompound fromJson(String s)
    {
        try
        {
            NBTBase tag = JsonToNBT.func_150315_a(s);

            if (tag instanceof NBTTagCompound)
            {
                return (NBTTagCompound) tag;
            }
        }
        catch (NBTException e)
        {
        }

        return new NBTTagCompound();
    }

    public static <T extends INBTSavedObject<T>> void registerAdapter(Class<? extends T> c, INBTSaveAdapter<T> adapter)
    {
        ADAPTERS.put(c, adapter);
    }

    public interface INBTSavedObject<T extends INBTSavedObject<T>>
    {
        NBTBase writeToNBT();

        void toBytes(ByteBuf buf);
    }

    public interface INBTSaveAdapter<T extends INBTSavedObject<T>>
    {
        T readFromNBT(NBTBase tag);

        T fromBytes(ByteBuf buf);
    }
}
