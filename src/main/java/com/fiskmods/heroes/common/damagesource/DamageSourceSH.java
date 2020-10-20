package com.fiskmods.heroes.common.damagesource;

import com.fiskmods.heroes.FiskHeroes;

import net.minecraft.util.DamageSource;

public class DamageSourceSH extends DamageSource implements IExtendedDamage
{
    private int flags;

    public DamageSourceSH(String s)
    {
        super(FiskHeroes.MODID + "." + s);
    }

    @Override
    public DamageSourceSH with(DamageType... types)
    {
        flags = DamageType.with(flags, types);
        return this;
    }

    @Override
    public int getFlags()
    {
        return flags;
    }
}
