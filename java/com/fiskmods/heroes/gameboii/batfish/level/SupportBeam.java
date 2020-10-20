package com.fiskmods.heroes.gameboii.batfish.level;

import java.awt.Graphics2D;

import com.fiskmods.heroes.gameboii.batfish.BatfishGraphics;
import com.fiskmods.heroes.gameboii.graphics.Screen;
import com.fiskmods.heroes.gameboii.level.LevelObject;

public class SupportBeam extends LevelObject
{
    public SupportBeam(double x, double y)
    {
        super(x, y, 10, BatfishSection.HEIGHT);
    }

    @Override
    public void draw(Graphics2D g2d, Screen screen, int x, int y, int screenWidth, int screenHeight, int scale)
    {
        for (int h = 0; h < height; h += 16)
        {
            screen.drawImage(g2d, BatfishGraphics.support_beam, x, y + h * scale, width * scale, Math.min(16, height - h) * scale);
        }
    }
}
