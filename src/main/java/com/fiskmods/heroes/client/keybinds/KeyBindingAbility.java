package com.fiskmods.heroes.client.keybinds;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindingAbility extends KeyBindingHero
{
    public final int abilityIndex;

    public KeyBindingAbility(String name, int index, int key)
    {
        super(name, key);
        abilityIndex = index;
        SHKeyBinds.KEY_MAPPINGS.put(index, this);
    }

    @Override
    public String toString()
    {
        return "Ability" + abilityIndex;
    }
}
