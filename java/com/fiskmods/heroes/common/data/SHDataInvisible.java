package com.fiskmods.heroes.common.data;

import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class SHDataInvisible extends SHData<Boolean>
{
    public SHDataInvisible(Boolean defaultValue)
    {
        super(defaultValue);
    }

    public SHDataInvisible(Boolean defaultValue, Predicate<Entity> canSet)
    {
        super(defaultValue, canSet);
    }

    @Override
    protected void onValueChanged(Entity entity, Boolean value)
    {
        if (!value && entity instanceof EntityLivingBase && !((EntityLivingBase) entity).isPotionActive(Potion.invisibility))
        {
            entity.setInvisible(false);
        }
    }
}
