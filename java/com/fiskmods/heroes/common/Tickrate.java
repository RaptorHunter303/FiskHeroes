package com.fiskmods.heroes.common;

import java.lang.reflect.Field;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

public class Tickrate
{
    private static Field clientTimer = null;

    public static float CLIENT_TICKRATE = 20;
    public static long MILISECONDS_PER_TICK = 50;

    public static void updateTickrate(Side side, float tickrate)
    {
        if (side.isClient())
        {
            updateClientTickrate(tickrate);
        }
        else
        {
            updateServerTickrate(tickrate);
        }
    }

    public static void updateClientTickrate(float tickrate)
    {
        Minecraft mc = Minecraft.getMinecraft();

        try
        {
            if (tickrate != CLIENT_TICKRATE)
            {
                if (clientTimer == null)
                {
                    for (Field f : mc.getClass().getDeclaredFields())
                    {
                        if (f.getType() == Timer.class)
                        {
                            clientTimer = f;
                            clientTimer.setAccessible(true);
                            break;
                        }
                    }
                }

                Timer timer = (Timer) clientTimer.get(mc);
                timer.timerSpeed = tickrate / 20F;
                CLIENT_TICKRATE = tickrate;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void updateServerTickrate(float tickrate)
    {
        MILISECONDS_PER_TICK = (long) (1000L / tickrate);
    }
}
