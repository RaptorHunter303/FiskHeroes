package com.fiskmods.heroes.gameboii.batfish.level;

import java.awt.Graphics2D;

import com.fiskmods.heroes.gameboii.batfish.Batfish;
import com.fiskmods.heroes.gameboii.batfish.BatfishGraphics;
import com.fiskmods.heroes.gameboii.graphics.Screen;
import com.fiskmods.heroes.gameboii.level.LevelObject;
import com.fiskmods.heroes.gameboii.level.MovingLevelObject;

public class OldSpice extends MovingLevelObject
{
    public OldSpice(double x, double y)
    {
        super(x, y, 18, 28);
    }

    @Override
    public void draw(Graphics2D g2d, Screen screen, int x, int y, int screenWidth, int screenHeight, int scale)
    {
        screen.drawImage(g2d, BatfishGraphics.old_spice, x, y, width * scale, height * scale);
    }

    @Override
    public void onUpdate()
    {
        if (Batfish.INSTANCE.worldPowerup > 0)
        {
            prevPosX = posX;
            prevPosY = posY;
        }
        else
        {
            if (posX > Batfish.INSTANCE.player.posX)
            {
                motionX -= 0.05;
            }

            motionX *= 0.95;
            motionY *= 0.98;
            super.onUpdate();
        }
    }

    @Override
    public boolean hasGravity()
    {
        return false;
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
