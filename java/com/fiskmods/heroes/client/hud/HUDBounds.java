package com.fiskmods.heroes.client.hud;

import java.util.function.Function;

public class HUDBounds
{
    public static final HUDBounds FULL_SCREEN = new HUDBounds(i -> 0, i -> 0, i -> i.w, i -> i.h);

    public final Function<ScreenInfo, Integer> xPos;
    public final Function<ScreenInfo, Integer> yPos;
    public final Function<ScreenInfo, Integer> width;
    public final Function<ScreenInfo, Integer> height;

    public HUDBounds(Function<ScreenInfo, Integer> x, Function<ScreenInfo, Integer> y, Function<ScreenInfo, Integer> w, Function<ScreenInfo, Integer> h)
    {
        xPos = x;
        yPos = y;
        width = w;
        height = h;
    }
}
