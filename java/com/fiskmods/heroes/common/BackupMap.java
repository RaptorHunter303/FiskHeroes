package com.fiskmods.heroes.common;

import java.util.HashMap;
import java.util.Map;

public class BackupMap<K, V> extends HashMap<K, V>
{
    private Map<K, V> backupMap;

    private final int capacity;
    private final float load;

    public BackupMap(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
        capacity = initialCapacity;
        load = loadFactor;
    }

    public BackupMap(int initialCapacity)
    {
        this(initialCapacity, 0.75F);
    }

    public BackupMap()
    {
        this(1, 0.75F);
    }

    public BackupMap(Map<? extends K, ? extends V> m)
    {
        super(m);

        if (m instanceof BackupMap)
        {
            BackupMap map = (BackupMap) m;
            backupMap = new HashMap<>(map.backupMap);
            capacity = map.capacity;
            load = map.load;
        }
        else
        {
            capacity = 1;
            load = 0.75F;
        }
    }

    public void backup()
    {
        backupMap = new HashMap<>(this);
    }

    public void reset()
    {
        backup();
        super.clear();
    }

    public boolean restore()
    {
        if (backupMap != null)
        {
            super.clear();
            putAll(backupMap);

            return true;
        }

        return false;
    }

    @Override
    public void clear()
    {
        super.clear();

        if (backupMap != null)
        {
            backupMap.clear();
        }
    }

    @Override
    public Object clone()
    {
        return new BackupMap<>(this);
    }
}
