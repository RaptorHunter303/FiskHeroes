package com.fiskmods.heroes.common.registry;

import java.util.Iterator;

import net.minecraft.util.ObjectIntIdentityMap;

public class FiskRegistryNumerical<T extends FiskRegistryEntry<T>> extends FiskSimpleRegistry<T>
{
    protected ObjectIntIdentityMap underlyingIntegerMap = new ObjectIntIdentityMap();
    protected int nextId = -1;

    public FiskRegistryNumerical(String domain, String key)
    {
        super(domain, key);
    }

    @Override
    public void putObject(String key, T value)
    {
        if (containsKey(key))
        {
            addObject(getIDForObject(value), key, value);
        }
        else
        {
            addObject(++nextId, key, value);
        }
    }

    public void addObject(int id, String key, T value)
    {
        underlyingIntegerMap.func_148746_a(value, id);
        super.putObject(key, value);
    }

    public int getIDForObject(T value)
    {
        return underlyingIntegerMap.func_148747_b(value);
    }

    public T getObjectById(int id)
    {
        return (T) underlyingIntegerMap.func_148745_a(id);
    }

    public boolean containsId(int id)
    {
        return underlyingIntegerMap.func_148744_b(id);
    }

    @Override
    public Iterator<T> iterator()
    {
        return underlyingIntegerMap.iterator();
    }

    public T lookup(String key)
    {
        if (containsKey(key))
        {
            return getObject(key);
        }

        try
        {
            int id = Integer.parseInt(key);

            if (containsId(id))
            {
                return getObjectById(id);
            }
        }
        catch (NumberFormatException e)
        {
        }

        return null;
    }
}
