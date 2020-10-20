package com.fiskmods.heroes.gameboii.level;

import java.awt.Graphics2D;

import com.fiskmods.heroes.gameboii.engine.BoundingBox;
import com.fiskmods.heroes.gameboii.graphics.Screen;

public abstract class LevelObject
{
    public BoundingBox boundingBox;
    public Level level;

    public int width;
    public int height;

    public double posX;
    public double posY;
    public double prevPosX;
    public double prevPosY;

    public boolean isDead;

    public LevelObject(double x, double y, int width, int height)
    {
        posX = prevPosX = x;
        posY = prevPosY = y;
        setSize(width, height);
    }

    public abstract void draw(Graphics2D g2d, Screen screen, int x, int y, int screenWidth, int screenHeight, int scale);

    public final boolean tick()
    {
        onUpdate();
        return isDead;
    }

    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
    }

    public void onCollideWith(LevelObject obj)
    {
    }

    public boolean canCollideWith(LevelObject obj)
    {
        return true;
    }

    public int depthPlane()
    {
        return 0;
    }

    public void destroy()
    {
        isDead = true;
    }

    public void setSize(int w, int h)
    {
        boundingBox = BoundingBox.getBoundingBox(-w / 2, -h, w / 2, 0);
        boundingBox.offset(posX, posY);
        width = w;
        height = h;
    }
}
