package com.fiskmods.heroes.client.keybinds;

import com.fiskmods.heroes.common.event.ClientEventHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindingHero extends SHKeyBinding
{
    public KeyBindingHero(String name, int key)
    {
        super(name, key);
    }

    @Override
    public boolean getIsKeyPressed()
    {
        return super.getIsKeyPressed() && !ClientEventHandler.blockKeyPresses;
    }

    @Override
    public boolean isPressed()
    {
        if (ClientEventHandler.blockKeyPresses)
        {
            super.isPressed();
            return false;
        }

        return super.isPressed();
    }
}
