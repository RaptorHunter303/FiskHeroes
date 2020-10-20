package com.fiskmods.heroes.client.hud;

import net.minecraft.client.gui.ScaledResolution;

public class ScreenInfo
{
    public final int w;
    public final int h;

    public ScreenInfo(int width, int height)
    {
        w = width;
        h = height;
    }

    public ScreenInfo(ScaledResolution res)
    {
        this(res.getScaledWidth(), res.getScaledHeight());
    }
}
