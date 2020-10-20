package com.fiskmods.heroes.common.spell;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class KeySequence
{
    public final Key[] keys;

    public KeySequence(String sequence)
    {
        List<Key> list = new ArrayList<>();

        for (int i = 0; i < sequence.length(); ++i)
        {
            Key key = Key.parse(sequence.charAt(i));

            if (key != null)
            {
                list.add(key);
            }
        }

        keys = list.toArray(new Key[0]);
    }

    @SideOnly(Side.CLIENT)
    public Sequencer sequence(Runnable onSuccess, Runnable onFail)
    {
        return new Sequencer(onSuccess, onFail);
    }

    @SideOnly(Side.CLIENT)
    public class Sequencer
    {
        private final boolean[] keyPressed = new boolean[Key.values().length];
        private final Runnable success, fail;

        public int index;

        public Sequencer(Runnable onSuccess, Runnable onFail)
        {
            success = onSuccess;
            fail = onFail;
        }

        public boolean keyPress()
        {
            int i = index;

            if (index >= 0)
            {
                for (Key key : Key.values())
                {
                    KeyBinding keyBind = key.get();

                    if (keyBind.getIsKeyPressed())
                    {
                        if (!keyPressed[key.ordinal()])
                        {
                            keyPressed[key.ordinal()] = true;
                            keyPress(key, keyBind);
                        }
                    }
                    else
                    {
                        keyPressed[key.ordinal()] = false;
                    }
                }
            }

            return index > i;
        }

        private void keyPress(Key key, KeyBinding keyBind)
        {
            if (key == keys[index])
            {
                if (++index >= keys.length)
                {
                    success.run();
                    index = -1;
                }
            }
            else
            {
                fail.run();
                index = -1;
            }
        }
    }

    public static enum Key
    {
        W(() -> Minecraft.getMinecraft().gameSettings.keyBindForward),
        A(() -> Minecraft.getMinecraft().gameSettings.keyBindLeft),
        S(() -> Minecraft.getMinecraft().gameSettings.keyBindBack),
        D(() -> Minecraft.getMinecraft().gameSettings.keyBindRight);

        private final Supplier key;

        Key(Supplier s)
        {
            key = s;
        }

        @SideOnly(Side.CLIENT)
        public KeyBinding get()
        {
            return (KeyBinding) key.get();
        }

        private static Key parse(char c)
        {
            switch (c)
            {
            case 'w':
                return W;
            case 'a':
                return A;
            case 's':
                return S;
            case 'd':
                return D;
            }

            return null;
        }
    }
}
