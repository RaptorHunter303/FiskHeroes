package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityIntangibility;
import com.fiskmods.heroes.common.interaction.InteractionType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressIntangibility extends KeyPressBase
{
    public KeyPressIntangibility()
    {
        setPredicate((e, h) -> h != null && (h.hasEnabledModifier(e, Ability.INTANGIBILITY) || h.hasEnabledModifier(e, Ability.ABSOLUTE_INTANGIBILITY)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, AbilityIntangibility.KEY_INTANGIBILITY);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isClient())
        {
            SHData.INTANGIBLE.set(sender, !SHData.INTANGIBLE.get(sender));
            Minecraft.getMinecraft().renderGlobal.loadRenderers();
        }
    }
}
