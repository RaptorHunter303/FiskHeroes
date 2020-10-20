package com.fiskmods.heroes.common.entity.ai;

import com.fiskmods.heroes.common.data.SHData;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;

public class EntityAIIronManAttack extends EntityAIArrowAttack
{
    private final EntityLiving entityHost;

    public EntityAIIronManAttack(IRangedAttackMob mob, double moveSpeed, int delay, float range)
    {
        this(mob, moveSpeed, delay, delay, range);
    }

    public EntityAIIronManAttack(IRangedAttackMob mob, double moveSpeed, int minDelay, int maxDelay, float range)
    {
        super(mob, moveSpeed, minDelay, maxDelay, range);
        entityHost = (EntityLiving) mob;
    }

    @Override
    public boolean shouldExecute()
    {
        return !SHData.SUIT_OPEN.get(entityHost) && super.shouldExecute();
    }
}
