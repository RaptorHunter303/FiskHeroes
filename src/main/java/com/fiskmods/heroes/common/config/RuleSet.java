package com.fiskmods.heroes.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fiskmods.heroes.common.network.MessageSyncRules;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.util.NBTHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class RuleSet
{
    protected final Map<Rule, Object> backingMap;
    protected final Map<Rule, Object> dirty = new HashMap<>();

    public RuleSet()
    {
        backingMap = new HashMap<>();

        for (Rule rule : Rule.REGISTRY)
        {
            backingMap.put(rule, rule.defaultValue);
        }
    }

    public RuleSet(RuleSet set)
    {
        backingMap = new HashMap<>(set.backingMap);
    }

    public void tick()
    {
        if (!dirty.isEmpty())
        {
            SHNetworkManager.wrapper.sendToAll(new MessageSyncRules(dirty));
            dirty.clear();
        }
    }

    void markDirty()
    {
        for (Rule rule : Rule.REGISTRY)
        {
            if (rule.requiresClientSync)
            {
                dirty.put(rule, get(rule));
            }
        }
    }

    public <T> T put(Rule<T> rule, T value)
    {
        Object obj = backingMap.put(rule, value);

        if (rule.requiresClientSync && FMLCommonHandler.instance().getSide().isServer() && !Objects.equals(obj, value))
        {
            dirty.put(rule, value);
        }

        return value;
    }

    public <T> T get(Rule<T> rule)
    {
        return (T) backingMap.get(rule);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        for (Map.Entry<Rule, Object> e : backingMap.entrySet())
        {
            compound.setTag(Rule.getNameFor(e.getKey()), NBTHelper.writeToNBT(e.getValue()));
        }

        return compound;
    }

    public RuleSet readFromNBT(NBTTagCompound compound)
    {
        for (Map.Entry<Rule, Object> e : backingMap.entrySet())
        {
            NBTBase nbt = compound.getTag(Rule.getNameFor(e.getKey()));

            if (nbt != null)
            {
                Object value = NBTHelper.readFromNBT(nbt, e.getKey().getType());
                e.setValue(value != null ? value : null);
            }
        }

        return this;
    }

    public void save(File file) throws IOException
    {
        CompressedStreamTools.writeCompressed(writeToNBT(new NBTTagCompound()), new FileOutputStream(file));
    }

    public boolean load(File file)
    {
        if (file.exists())
        {
            try
            {
                readFromNBT(CompressedStreamTools.readCompressed(new FileInputStream(file)));
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return backingMap.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj == null || obj.getClass() != getClass())
        {
            return false;
        }

        RuleSet other = (RuleSet) obj;

        if (backingMap == null)
        {
            if (other.backingMap != null)
            {
                return false;
            }
        }
        else if (!backingMap.equals(other.backingMap))
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "RuleSet" + backingMap;
    }
}
