package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.world.ModDimensions;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AbilityGliding extends Ability
{
    public AbilityGliding(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled)
        {
            boolean flag = shouldDisable(entity);

            if (entity.worldObj.isRemote && FiskHeroes.proxy.isClientPlayer(entity) && entity instanceof EntityPlayerSP)
            {
                EntityPlayerSP player = (EntityPlayerSP) entity;

                if (flag)
                {
                    SHData.GLIDING.set(entity, false);
                }
                else if (player.movementInput.jump && !SHData.PREV_JUMPING.get(entity) && (SHData.GLIDING.get(entity) || entity.motionY < 0))
                {
                    SHData.GLIDING.set(entity, !SHData.GLIDING.get(entity));

                    if (SHData.GLIDING.get(entity))
                    {
                        FiskHeroes.proxy.playSound(entity, "gliding", 1, 1);
                    }
                }

                SHData.PREV_JUMPING.setWithoutNotify(entity, player.movementInput.jump);
            }

            SHHelper.incr(SHData.WING_ANIMATION_TIMER, entity, SHConstants.ANIMATION_WINGS, SHData.GLIDING.get(entity));
        }
    }

    public boolean shouldDisable(EntityLivingBase entity)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;

            if (player.capabilities.isFlying)
            {
                return true;
            }
        }

        return entity.onGround || entity.isCollided || entity.worldObj.provider.dimensionId == ModDimensions.QUANTUM_REALM_ID || entity.isInWater() || entity.isRiding();
    }
}
