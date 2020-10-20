package com.fiskmods.heroes.common.move;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.entity.EntityCactusSpike;
import com.fiskmods.heroes.common.hero.Hero;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;

public class MoveSpikeRing extends Move
{
    public MoveSpikeRing(int tier)
    {
        super(tier, MouseAction.LEFT_CLICK);
        setModifierFormat(Number::intValue);
    }

    @Override
    public String localizeModifier(String key)
    {
        switch (key)
        {
        case KEY_QUANTITY:
            return "move.modifier.quantity";
        default:
            return null;
        }
    }

    @Override
    public boolean onActivated(EntityLivingBase entity, Hero hero, MovingObjectPosition mop, MoveActivation activation, ImmutableMap<String, Number> modifiers, float focus)
    {
        int num = modifiers.getOrDefault(KEY_QUANTITY, 0).intValue();

        if (num > 0)
        {
            if (!entity.worldObj.isRemote)
            {
                int spikes = 6 * num;
                float prevYaw = entity.rotationYaw;

                for (int i = 0; i < spikes; ++i)
                {
                    entity.rotationYaw = i * (360F / spikes);
                    entity.worldObj.spawnEntityInWorld(new EntityCactusSpike(entity.worldObj, entity));
                }

                entity.rotationYaw = prevYaw;
                entity.worldObj.playSoundAtEntity(entity, SHSounds.ITEM_BOW_SHOOT.toString(), 1.0F, 1.0F / (entity.getRNG().nextFloat() * 0.4F + 1.2F) + 0.75F);
            }

            return true;
        }

        return false;
    }
}
