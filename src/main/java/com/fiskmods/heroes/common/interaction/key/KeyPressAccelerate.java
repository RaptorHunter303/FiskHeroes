package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilitySuperSpeed;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.util.SpeedsterHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressAccelerate extends KeyPressBase
{
    public KeyPressAccelerate()
    {
        requireModifier(Ability.SUPER_SPEED);
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return super.clientRequirements(player, type, x, y, z) && SHData.SPEED.get(player) < SpeedsterHelper.getMaxSpeedSetting(player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, AbilitySuperSpeed.KEY_ACCELERATE);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isClient())
        {
            SHData.SPEED.incr(sender, (byte) 1);
        }
    }
}
