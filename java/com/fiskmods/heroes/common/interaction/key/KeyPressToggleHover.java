package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityHover;
import com.fiskmods.heroes.common.interaction.InteractionType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressToggleHover extends KeyPressBase
{
    public KeyPressToggleHover()
    {
        requireModifier(Ability.HOVER);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return !player.capabilities.isFlying && !player.onGround && super.serverRequirements(player, type, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, AbilityHover.KEY_HOVER);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isClient())
        {
            SHData.HOVERING.set(sender, !SHData.HOVERING.get(sender));
        }
    }
}
