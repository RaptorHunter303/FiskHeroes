package com.fiskmods.heroes.common.damagesource;

import com.fiskmods.heroes.FiskHeroes;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSourceIndirect;

public class EntityDamageSourceIndirectSH extends EntityDamageSourceIndirect implements IExtendedDamage
{
    private int flags;

    public EntityDamageSourceIndirectSH(String s, Entity entity, Entity indirectEntity)
    {
        super(FiskHeroes.MODID + "." + s, entity, indirectEntity);
    }

    @Override
    public EntityDamageSourceIndirectSH with(DamageType... types)
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
