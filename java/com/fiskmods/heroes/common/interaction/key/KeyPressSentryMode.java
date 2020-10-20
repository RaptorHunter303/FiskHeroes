package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressSentryMode extends KeyPressBase
{
    public static final String KEY_SENTRY_MODE = "SENTRY_MODE";

    public KeyPressSentryMode()
    {
        requireModifier(Ability.SENTRY_MODE);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Rule.ALLOW_SENTRYMODE.get(player.worldObj, x, z) && super.serverRequirements(player, type, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, KEY_SENTRY_MODE);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        boolean isSuitOpen = SHData.SUIT_OPEN.get(sender);
        int suitOpenTimer = SHData.SUIT_OPEN_TIMER.get(sender);

        if (SHData.MASK_OPEN.get(sender))
        {
            if (side.isClient())
            {
                SHHelper.getHero(sender).onToggleMask(sender, false);
            }

            SHData.MASK_OPEN.setWithoutNotify(sender, false);
        }

        if (!isSuitOpen && suitOpenTimer == 0)
        {
            SHData.SUIT_OPEN.setWithoutNotify(sender, true);
        }
        else if (isSuitOpen && suitOpenTimer == 5)
        {
            SHData.SUIT_OPEN.setWithoutNotify(sender, false);
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
        return TARGET_ALL;
    }
}
