package com.fiskmods.heroes.common.interaction;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityFireBlast;
import com.fiskmods.heroes.common.hero.modifier.Ability;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class InteractionFireball extends InteractionBase
{
    public InteractionFireball()
    {
        requireModifier(Ability.PYROKINESIS);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return !SHData.AIMING.get(player) && player.getHeldItem() == null && !player.isSneaking();
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Cooldown.FIREBALL.available(player);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            sender.worldObj.spawnEntityInWorld(new EntityFireBlast(sender.worldObj, sender));
            sender.worldObj.playSoundAtEntity(sender, SHSounds.ABILITY_PYROKINESIS_FIREBALL.toString(), 1, 1);
        }
        else if (sender == clientPlayer)
        {
            sender.swingItem();
            Cooldown.FIREBALL.set(sender);
        }
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
