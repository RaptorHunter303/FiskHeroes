package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.interaction.InteractionBase;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public abstract class KeyPressBase extends InteractionBase
{
    public KeyPressBase(InteractionType... types)
    {
        super(types);
    }

    public KeyPressBase()
    {
        this(InteractionType.KEY_PRESS);
    }

    @Override
    public boolean syncWithServer()
    {
        return false;
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return true;
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        KeyBinding key;
        return (key = getKey(player, SHHelper.getHero(player))) != null && key.getIsKeyPressed();
    }

    @SideOnly(Side.CLIENT)
    public abstract KeyBinding getKey(EntityPlayer player, Hero hero);
}
