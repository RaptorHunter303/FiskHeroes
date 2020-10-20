package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityShapeShifting;
import com.fiskmods.heroes.common.interaction.InteractionType;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressShapeShiftReset extends KeyPressBase
{
    public KeyPressShapeShiftReset()
    {
        requireModifier(Ability.SHAPE_SHIFTING);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return SHData.DISGUISE.get(player) != null && SHData.SHAPE_SHIFT_TIMER.get(player) == 0 && super.serverRequirements(player, type, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, AbilityShapeShifting.KEY_SHAPE_SHIFT_RESET);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        SHData.SHAPE_SHIFT_TIMER.setWithoutNotify(sender, 1.0F);
        SHData.SHAPE_SHIFTING_FROM.setWithoutNotify(sender, SHData.DISGUISE.get(sender));
        SHData.SHAPE_SHIFTING_TO.setWithoutNotify(sender, null);
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
