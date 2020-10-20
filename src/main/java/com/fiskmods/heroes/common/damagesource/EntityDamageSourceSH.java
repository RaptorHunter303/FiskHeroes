package com.fiskmods.heroes.common.damagesource;

import com.fiskmods.heroes.FiskHeroes;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class EntityDamageSourceSH extends EntityDamageSource implements IExtendedDamage
{
    private int flags;

    public EntityDamageSourceSH(String s, Entity entity)
    {
        super(FiskHeroes.MODID + "." + s, entity);
    }

    @Override
    public EntityDamageSourceSH with(DamageType... types)
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
