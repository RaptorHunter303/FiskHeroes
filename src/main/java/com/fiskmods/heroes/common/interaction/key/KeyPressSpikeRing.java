package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.entity.EntityCactusSpike;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityCactusPhysiology;
import com.fiskmods.heroes.common.interaction.InteractionType;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressSpikeRing extends KeyPressBase
{
    public KeyPressSpikeRing()
    {
        requireModifier(Ability.CACTUS_PHYSIOLOGY);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return (player.capabilities.isCreativeMode || player.getFoodStats().getFoodLevel() >= 12) && super.serverRequirements(player, type, x, y, z);
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Cooldown.SPIKE_RING.available(player) && super.clientRequirements(player, type, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, AbilityCactusPhysiology.KEY_SHOOT_SPIKES);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            if (!sender.capabilities.isCreativeMode)
            {
                sender.addExhaustion(6);
            }

            int spikes = 72;
            float prevYaw = sender.rotationYaw;

            for (int i = 0; i < spikes; ++i)
            {
                sender.rotationYaw = i * (360F / spikes);
                sender.worldObj.spawnEntityInWorld(new EntityCactusSpike(sender.worldObj, sender));
            }

            sender.rotationYaw = prevYaw;
            sender.worldObj.playSoundAtEntity(sender, SHSounds.ITEM_BOW_SHOOT.toString(), 1.0F, 1.0F / (sender.getRNG().nextFloat() * 0.4F + 1.2F) + 0.75F);
        }
        else if (sender == clientPlayer)
        {
            Cooldown.SPIKE_RING.set(sender);
        }
    }

    @Override
    public boolean syncWithServer()
    {
        return true;
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
