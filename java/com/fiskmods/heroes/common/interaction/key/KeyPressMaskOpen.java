package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.client.keybinds.SHKeyBinding;
import com.fiskmods.heroes.client.keybinds.SHKeyBinds;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Hero.Property;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressMaskOpen extends KeyPressBase
{
    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return SHData.SUIT_OPEN_TIMER.get(player) == 0 && SHHelper.hasProperty(player, Property.MASK_TOGGLE) && super.serverRequirements(player, type, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SHKeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return SHKeyBinds.OPEN_MASK;
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isClient())
        {
            Hero hero = SHHelper.getHero(sender);
            boolean isMaskOpen = SHData.MASK_OPEN.get(sender);
            int maskOpenTimer = SHData.MASK_OPEN_TIMER.get(sender);

            if (!isMaskOpen && maskOpenTimer == 0)
            {
                hero.onToggleMask(sender, true);
                SHData.MASK_OPEN.set(sender, true);
            }
            else if (isMaskOpen && maskOpenTimer == 5)
            {
                hero.onToggleMask(sender, false);
                SHData.MASK_OPEN.set(sender, false);
            }
        }
    }
}
