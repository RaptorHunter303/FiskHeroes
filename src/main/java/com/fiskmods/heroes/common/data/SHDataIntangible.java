package com.fiskmods.heroes.common.data;

import java.util.function.Predicate;

import net.minecraft.entity.Entity;

public class SHDataIntangible extends SHData<Boolean>
{
    public SHDataIntangible(Boolean defaultValue)
    {
        super(defaultValue);
    }

    public SHDataIntangible(Boolean defaultValue, Predicate<Entity> canSet)
    {
        super(defaultValue, canSet);
    }

    @Override
    protected void onValueChanged(Entity entity, Boolean value)
    {
        if (!value)
        {
            entity.noClip = false;
        }
    }
}
