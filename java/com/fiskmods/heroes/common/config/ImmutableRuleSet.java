package com.fiskmods.heroes.common.config;

import java.io.File;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class ImmutableRuleSet extends RuleSet
{
    public ImmutableRuleSet(RuleSet set)
    {
        super(set);
    }

    @Override
    public void tick()
    {
    }

    @Override
    void markDirty()
    {
    }

    @Override
    @Deprecated
    public <T> T put(Rule<T> rule, T value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public RuleSet readFromNBT(NBTTagCompound compound)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void save(File file) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public boolean load(File file)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString()
    {
        return "ImmutableRuleSet" + backingMap;
    }
}
