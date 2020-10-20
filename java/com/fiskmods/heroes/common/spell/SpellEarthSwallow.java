package com.fiskmods.heroes.common.spell;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.entity.EntityEarthCrack;
import com.fiskmods.heroes.common.entity.EntitySpellDuplicate;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

public class SpellEarthSwallow extends Spell
{
    private final List<EntityLivingBase> targets = new ArrayList<>();

    public SpellEarthSwallow()
    {
        super(Cooldown.SPELL_EARTHSWALLOW, "wwadsw", true);
    }

    @Override
    public boolean canTrigger(EntityLivingBase caster)
    {
        if (!caster.worldObj.isRemote)
        {
            collectTargets(caster);
            return !targets.isEmpty();
        }

        return true;
    }

    @Override
    public void onTrigger(EntityLivingBase caster)
    {
        if (!caster.worldObj.isRemote)
        {
            if (targets.isEmpty())
            {
                collectTargets(caster);
            }

            for (EntityLivingBase entity : targets)
            {
                caster.worldObj.spawnEntityInWorld(new EntityEarthCrack(caster.worldObj, caster, entity));
            }

            targets.clear();
        }
    }

    private void collectTargets(EntityLivingBase caster)
    {
        EntityLivingBase realCaster = SHHelper.filterDuplicate(caster);
        Hero hero = SHHelper.getHero(realCaster);
        
        MovingObjectPosition mop = SHHelper.rayTrace(caster, Rule.RANGE_SPELL_EARTHSWALLOW.get(realCaster, hero), 2, 1);

        if (mop != null && mop.hitVec != null)
        {
            double x = mop.hitVec.xCoord;
            double y = mop.hitVec.yCoord;
            double z = mop.hitVec.zCoord;
            float radius = Rule.RADIUS_SPELL_EARTHSWALLOW.get(realCaster, hero);

            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
            List<EntityLivingBase> list = caster.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

            for (EntityLivingBase entity : list)
            {
                if (entity != caster && entity.isEntityAlive() && !entity.isOnSameTeam(caster) && canTarget(caster, entity))
                {
                    targets.add(entity);
                }
            }
        }
    }

    private boolean canTarget(EntityLivingBase caster, EntityLivingBase entity)
    {
        return !(entity instanceof EntitySpellDuplicate || caster instanceof EntitySpellDuplicate && ((EntitySpellDuplicate) caster).isOwner(entity)) && !SHHelper.isEarthCrackTarget(entity);
    }
}
