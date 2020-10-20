package com.fiskmods.heroes.client.keybinds;

import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public class KeyBindTranslator
{
    public static void translate(EntityPlayer player)
    {
        Hero hero = SHHelper.getHero(player);

        for (SHKeyBinding key : SHKeyBinds.ABILITY_KEYS)
        {
            key.keyDescription = key.defaultKeyDescription;
        }

        if (hero != null)
        {
            Map<String, String> mappings = new HashMap<>();
            hero.translateKeys(mappings);

            for (Map.Entry<String, String> e : mappings.entrySet())
            {
                KeyBinding keybind = hero.getKey(player, e.getKey());

                if (keybind instanceof SHKeyBinding)
                {
                    ((SHKeyBinding) keybind).keyDescription = e.getValue();
                }
            }
        }
    }
}
