package com.fiskmods.heroes.common.entity.ai;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityIronMan;

import net.minecraft.entity.ai.EntityAIWander;

public class EntityAIIronManWander extends EntityAIWander
{
    private EntityIronMan ironMan;

    public EntityAIIronManWander(EntityIronMan entity, double speed)
    {
        super(entity, speed);
        ironMan = entity;
    }

    @Override
    public boolean shouldExecute()
    {
        return !ironMan.get(SHData.SUIT_OPEN) && super.shouldExecute();
    }

    @Override
    public boolean continueExecuting()
    {
        return !ironMan.get(SHData.SUIT_OPEN) && super.continueExecuting();
    }
}
