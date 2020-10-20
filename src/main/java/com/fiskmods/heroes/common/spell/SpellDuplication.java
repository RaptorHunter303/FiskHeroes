package com.fiskmods.heroes.common.spell;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.entity.EntitySpellDuplicate;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.util.MovingObjectPosition;

public class SpellDuplication extends Spell
{
    private EntityLivingBase target;

    public SpellDuplication()
    {
        super(Cooldown.SPELL_DUPLICATION, "adadaswad", false);
    }

    @Override
    public boolean canTrigger(EntityLivingBase caster)
    {
        collectTarget(caster);
        return target != null;
    }

    @Override
    public void onTrigger(EntityLivingBase caster)
    {
        if (!caster.worldObj.isRemote)
        {
            if (target == null)
            {
                collectTarget(caster);
            }

            if (target != null)
            {
                caster.worldObj.loadedEntityList.stream().filter(t -> t instanceof EntitySpellDuplicate && ((EntitySpellDuplicate) t).isOwner(caster)).forEach(t -> ((EntityLivingBase) t).setHealth(0));
                int duplicates = 6;

                for (int i = 1; i < duplicates; ++i)
                {
                    caster.worldObj.spawnEntityInWorld(new EntitySpellDuplicate(caster, target, 360F / duplicates * i));
                }

                target = null;
            }
        }
    }

    private void collectTarget(EntityLivingBase caster)
    {
        MovingObjectPosition mop = null;

        for (float f = 0; f <= 1 && (mop == null || mop.entityHit == null); ++f)
        {
            mop = SHHelper.rayTrace(caster, SHConstants.RANGE_DUPLICATION, f, 4, 1);
        }

        Entity entity = mop != null ? mop.entityHit : null;

        if (entity instanceof EntityDragonPart && ((EntityDragonPart) entity).entityDragonObj instanceof EntityLivingBase)
        {
            entity = (Entity) ((EntityDragonPart) entity).entityDragonObj;
        }

        target = entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null;
    }
}
