package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityUmbrakinesis;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressShadowform extends KeyPressBase
{
    public KeyPressShadowform()
    {
        requireModifier(Ability.UMBRAKINESIS);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        float f = SHData.SHADOWFORM_TIMER.get(player);
        return (f == 0 || f == 1) && super.serverRequirements(player, type, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, AbilityUmbrakinesis.KEY_SHADOWFORM);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isClient())
        {
            SHHelper.setShadowForm(sender, !SHData.SHADOWFORM.get(sender), true);
        }
    }
}
