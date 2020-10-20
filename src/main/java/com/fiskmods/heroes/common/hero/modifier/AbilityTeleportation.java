package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.DimensionalCoords;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;

public class AbilityTeleportation extends Ability
{
    public static final String KEY_TELEPORT = "TELEPORT";

    public AbilityTeleportation(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled)
        {
            int delay = SHData.TELEPORT_DELAY.get(entity) & 0xFF;

            if (delay > 0 && SHData.TELEPORT_DELAY.setWithoutNotify(entity, (byte) --delay) && delay == 0 && !entity.worldObj.isRemote)
            {
                DimensionalCoords coords = SHData.TELEPORT_DEST.get(entity);

                if (coords != null)
                {
                    if (entity.isRiding())
                    {
                        entity.mountEntity(null);
                    }

                    SHData.TELEPORT_DEST.setWithoutNotify(entity, null);
                    entity.setPositionAndUpdate(coords.posX + 0.5, coords.posY, coords.posZ + 0.5);
                    entity.fallDistance = 0.0F;
                }
            }
        }
    }
}
