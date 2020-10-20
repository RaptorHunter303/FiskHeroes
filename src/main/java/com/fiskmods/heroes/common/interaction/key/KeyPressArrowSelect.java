package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.client.keybinds.SHKeyBinding;
import com.fiskmods.heroes.client.keybinds.SHKeyBinds;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.QuiverHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressArrowSelect extends KeyPressBase
{
    public KeyPressArrowSelect()
    {
        setPredicate((e, m) -> true);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return QuiverHelper.hasQuiver(player) && player.getHeldItem() != null && player.getHeldItem().getItem() == ModItems.compoundBow && super.serverRequirements(player, type, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SHKeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return SHKeyBinds.SELECT_ARROW;
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isClient())
        {
            if (SHData.SELECTED_ARROW.get(sender) < 4)
            {
                SHData.SELECTED_ARROW.incr(sender, (byte) 1);
            }
            else
            {
                SHData.SELECTED_ARROW.set(sender, (byte) 0);
            }
        }
    }
}
