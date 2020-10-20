package com.fiskmods.heroes.client.keybinds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SHKeyBinds
{
    public static final List<SHKeyBinding> ABILITY_KEYS = new ArrayList<>();
    public static final Map<Integer, SHKeyBinding> KEY_MAPPINGS = new HashMap<>();

    public static final SHKeyBinding SELECT_ARROW = new SHKeyBinding("key.selectArrow", Keyboard.KEY_F);
//    public static final SHKeyBinding COMBAT_MODE = new SHKeyBinding("key.combatMode", Keyboard.KEY_B); // TODO: 1.4 Combat
    public static final SHKeyBinding OPEN_MASK = new KeyBindingHero("key.openMask", Keyboard.KEY_RSHIFT);

    public static final SHKeyBinding ABILITY_1 = new KeyBindingAbility("key.suitAbility1", 1, Keyboard.KEY_N);
    public static final SHKeyBinding ABILITY_2 = new KeyBindingAbility("key.suitAbility2", 2, Keyboard.KEY_M);
    public static final SHKeyBinding ABILITY_3 = new KeyBindingAbility("key.suitAbility3", 3, Keyboard.KEY_J);
    public static final SHKeyBinding ABILITY_4 = new KeyBindingAbility("key.suitAbility4", 4, Keyboard.KEY_K);
    public static final SHKeyBinding ABILITY_5 = new KeyBindingAbility("key.suitAbility5", 5, Keyboard.KEY_H);

    public static void register()
    {
        ClientRegistry.registerKeyBinding(SELECT_ARROW);
//        ClientRegistry.registerKeyBinding(COMBAT_MODE);
        ClientRegistry.registerKeyBinding(OPEN_MASK);

        ClientRegistry.registerKeyBinding(ABILITY_1);
        ClientRegistry.registerKeyBinding(ABILITY_2);
        ClientRegistry.registerKeyBinding(ABILITY_3);
        ClientRegistry.registerKeyBinding(ABILITY_4);
        ClientRegistry.registerKeyBinding(ABILITY_5);
    }

    public static SHKeyBinding getMapping(int key)
    {
        return KEY_MAPPINGS.get(key);
    }
}
