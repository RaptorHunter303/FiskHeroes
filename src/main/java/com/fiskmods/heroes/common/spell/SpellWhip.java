package com.fiskmods.heroes.common.spell;

import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.entity.EntitySpellWhip;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class SpellWhip extends Spell
{
    public SpellWhip()
    {
        super(Cooldown.SPELL_WHIP, "wssds", true);
    }

    @Override
    public void onTrigger(EntityLivingBase caster)
    {
        if (!caster.worldObj.isRemote)
        {
            caster.worldObj.loadedEntityList.stream().filter(t -> t instanceof EntitySpellWhip && ((EntitySpellWhip) t).casterEntity == caster).forEach(t -> ((Entity) t).setDead());
            caster.worldObj.spawnEntityInWorld(new EntitySpellWhip(caster.worldObj, caster));
        }
    }
}
