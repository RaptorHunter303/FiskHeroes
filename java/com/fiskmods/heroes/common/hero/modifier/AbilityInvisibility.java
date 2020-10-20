package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AbilityInvisibility extends Ability
{
    public static final String KEY_INVISIBILITY = "INVISIBILITY";

    public AbilityInvisibility(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled)
        {
            boolean flag = SHData.INVISIBLE.get(entity);

            if (flag)
            {
                if (entity.worldObj.isRemote && !FiskHeroes.proxy.isClientPlayer(entity))
                {
                    entity.setInvisible(!SHHelper.canPlayerSeeMartianInvis(FiskHeroes.proxy.getPlayer()));
                }
                else
                {
                    entity.setInvisible(true);
                }
            }

            SHHelper.incr(SHData.INVISIBILITY_TIMER, entity, SHConstants.FADE_INVISIBILITY, flag);
        }
    }

    @Override
    public boolean renderIcon(EntityPlayer player)
    {
        return SHData.INVISIBLE.get(player);
    }

    @Override
    public int getX()
    {
        return 72;
    }

    @Override
    public int getY()
    {
        return 0;
    }
}
