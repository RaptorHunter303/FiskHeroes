package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityCanaryCry;
import com.fiskmods.heroes.common.hero.Hero;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AbilityCanaryCry extends Ability
{
    public static final String KEY_CANARY_CRY = "CANARY_CRY";

    public AbilityCanaryCry(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END)
        {
            if (enabled && SHData.SHOOTING.get(entity))
            {
                if (!entity.worldObj.isRemote)
                {
                    entity.worldObj.spawnEntityInWorld(new EntityCanaryCry(entity.worldObj, entity));
                }

                if (!(entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying))
                {
                    float f = Math.max(entity.rotationPitch - 45, 0) / 45;
                    entity.fallDistance = Math.max(entity.fallDistance - 0.4F * f, 0);
                    entity.motionY += 0.05 * f;
                }
            }

            if (entity.worldObj.isRemote && FiskHeroes.proxy.isClientPlayer(entity))
            {
                SHData.SHOOTING.set(entity, enabled && hero.isKeyPressed(entity, KEY_CANARY_CRY));
            }
        }
    }
}
