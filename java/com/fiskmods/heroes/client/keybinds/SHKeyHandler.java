package com.fiskmods.heroes.client.keybinds;

import com.fiskmods.heroes.common.interaction.InteractionType;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public enum SHKeyHandler
{
    INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();

    private int pressed;
    private int[] timePressed;

    @SubscribeEvent
    public void onKeyPress(KeyInputEvent event)
    {
        if (mc.currentScreen == null)
        {
            int prevPressed = pressed;
            pressed = 0;

            for (int i = 0; i < SHKeyBinds.ABILITY_KEYS.size(); ++i)
            {
                if (SHKeyBinds.ABILITY_KEYS.get(i).getIsKeyPressed())
                {
                    pressed |= 1 << i;
                }
            }

            if (pressed != prevPressed)
            {
                InteractionType.KEY_PRESS.interact(mc.thePlayer, MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.boundingBox.minY), MathHelper.floor_double(mc.thePlayer.posZ), false);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (mc.currentScreen == null && event.phase == TickEvent.Phase.END)
        {
            if (timePressed == null)
            {
                timePressed = new int[SHKeyBinds.ABILITY_KEYS.size()];
            }

            for (int i = 0; i < SHKeyBinds.ABILITY_KEYS.size(); ++i)
            {
                if (SHKeyBinds.ABILITY_KEYS.get(i).getIsKeyPressed())
                {
                    if (++timePressed[i] == 10)
                    {
                        InteractionType.KEY_HOLD.interact(mc.thePlayer, MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.boundingBox.minY), MathHelper.floor_double(mc.thePlayer.posZ), false);
                    }
                }
                else
                {
                    timePressed[i] = 0;
                }
            }
        }
    }
}
