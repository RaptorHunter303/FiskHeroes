package com.fiskmods.heroes.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BackupList<T> extends ArrayList<T>
{
    private List<T> backupList;

    private final int capacity;

    public BackupList(int initialCapacity)
    {
        super(initialCapacity);
        capacity = initialCapacity;
    }

    public BackupList()
    {
        this(10);
    }

    public BackupList(Collection<? extends T> c)
    {
        super(c);

        if (c instanceof BackupList)
        {
            BackupList list = (BackupList) c;
            capacity = list.capacity;
        }
        else
        {
            capacity = 10;
        }
    }

    public void backup()
    {
        backupList = new ArrayList<>(this);
    }

    public void reset()
    {
        backup();
        super.clear();
    }

    public boolean restore()
    {
        if (backupList != null)
        {
            super.clear();
            addAll(backupList);

            return true;
        }

        return false;
    }

    @Override
    public void clear()
    {
        super.clear();

        if (backupList != null)
        {
            backupList.clear();
        }
    }

    @Override
    public Object clone()
    {
        return new BackupList<>(this);
    }
}
