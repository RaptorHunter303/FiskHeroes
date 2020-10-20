package com.fiskmods.heroes.common.interaction;

import java.util.Random;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityCactusSpike;
import com.fiskmods.heroes.common.hero.modifier.Ability;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class InteractionSpikeBurst extends InteractionBase
{
    public InteractionSpikeBurst()
    {
        requireModifier(Ability.CACTUS_PHYSIOLOGY);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return !SHData.AIMING.get(player) && (player.capabilities.isCreativeMode || player.getFoodStats().getFoodLevel() >= 12) && player.getHeldItem() == null && !player.isSneaking();
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Cooldown.SPIKE_BURST.available(player);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            float spread = 0.9F;

            if (!sender.capabilities.isCreativeMode)
            {
                sender.addExhaustion(1);
            }

            for (int i = 0; i <= 10; ++i)
            {
                EntityCactusSpike entity = new EntityCactusSpike(sender.worldObj, sender);

                if (i > 0)
                {
                    entity.motionX += (Math.random() - 0.5D) * spread;
                    entity.motionY += (Math.random() - 0.5D) * spread;
                    entity.motionZ += (Math.random() - 0.5D) * spread;
                }

                sender.worldObj.spawnEntityInWorld(entity);
            }

            Random rand = new Random();
            sender.worldObj.playSoundAtEntity(sender, SHSounds.ITEM_BOW_SHOOT.toString(), 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + 0.75F);
        }
        else if (sender == clientPlayer)
        {
            sender.swingItem();
            Cooldown.SPIKE_BURST.set(sender);
        }
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
