package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityShield;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressToggleShield extends KeyPressBase
{
    public KeyPressToggleShield()
    {
        requireModifier(Ability.RETRACTABLE_SHIELD);
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        Hero hero;
        return super.clientRequirements(player, type, x, y, z) && (hero = SHHelper.getHero(player)) != null && hero.getFuncBoolean(player, AbilityShield.FUNC_TOGGLE, true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, AbilityShield.KEY_SHIELD);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isClient())
        {
            SHData.SHIELD.set(sender, !SHData.SHIELD.get(sender));
        }
    }
}
