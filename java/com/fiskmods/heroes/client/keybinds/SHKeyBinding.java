package com.fiskmods.heroes.client.keybinds;

import com.fiskmods.heroes.FiskHeroes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

@SideOnly(Side.CLIENT)
public class SHKeyBinding extends KeyBinding
{
    public final String defaultKeyDescription;
    public String keyDescription;

    public SHKeyBinding(String name, int key)
    {
        super(name, key, FiskHeroes.NAME);
        defaultKeyDescription = name;
        keyDescription = name;

        SHKeyBinds.ABILITY_KEYS.add(this);
    }

    @Override
    public String getKeyDescription()
    {
        // FIXME: Keybinds

        return keyDescription;
//        return defaultKeyDescription;
    }

    @Override
    public String toString()
    {
        return defaultKeyDescription;
    }
}
