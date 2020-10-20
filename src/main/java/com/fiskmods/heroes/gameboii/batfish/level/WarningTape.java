package com.fiskmods.heroes.gameboii.batfish.level;

import java.awt.Graphics2D;

import com.fiskmods.heroes.gameboii.batfish.BatfishGraphics;
import com.fiskmods.heroes.gameboii.graphics.Screen;
import com.fiskmods.heroes.gameboii.level.LevelObject;

public class WarningTape extends LevelObject
{
    public boolean broken;

    public WarningTape(double x, double y)
    {
        super(x + 13, y + 6, 22, 2);
    }

    @Override
    public void draw(Graphics2D g2d, Screen screen, int x, int y, int screenWidth, int screenHeight, int scale)
    {
        int i = broken ? 1 : 0;
        screen.drawImage(g2d, BatfishGraphics.warning_tape, x - 2 * scale, y, 26 * scale, 8 * scale, 0, i * 8, 26, i * 8 + 8);
    }

    @Override
    public void onCollideWith(LevelObject obj)
    {
        if (obj instanceof BatfishPlayer && !broken)
        {
            broken = true;
        }
    }

    @Override
    public boolean canCollideWith(LevelObject obj)
    {
        return false;
    }

    @Override
    public int depthPlane()
    {
        return -2;
    }
}
