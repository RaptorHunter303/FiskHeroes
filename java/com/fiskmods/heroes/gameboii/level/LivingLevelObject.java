package com.fiskmods.heroes.gameboii.level;

import java.awt.Graphics2D;
import java.util.Random;

import com.fiskmods.heroes.gameboii.graphics.Resource;
import com.fiskmods.heroes.gameboii.graphics.Screen;

public abstract class LivingLevelObject extends MovingLevelObject
{
    public int ticksExisted;

    public boolean facing;
    public double walkAmount;
    public double walkDelta;

    public LivingLevelObject(double x, double y, int width, int height, Random rand)
    {
        super(x, y + height, width, height);

        if (rand != null)
        {
            ticksExisted = rand.nextInt(20);
            facing = rand.nextBoolean();
        }
    }

    public void drawBody(Graphics2D g2d, Screen screen, int x, int y, int scale, Resource resource, int frameX, int frameY)
    {
        int srcX1 = frameX * 20;
        int srcY1 = frameY * 20;
        int srcX2 = srcX1 + 20;

        if (facing)
        {
            int i = srcX1;
            srcX1 = srcX2;
            srcX2 = i;
        }

        x -= (20 - width) / 2 * scale;
        screen.drawImage(g2d, resource, x, y, 20 * scale, 20 * scale, srcX1, srcY1, srcX2, srcY1 + 20);
    }

    @Override
    public void onUpdate()
    {
        ++ticksExisted;
        super.onUpdate();
        onLivingUpdate();

        if (!hasGravity())
        {
            float f = 0.8F;
            motionX *= f;
            motionY *= f;
        }
        else if (onGround)
        {
            motionX *= 0.75;
        }
        else
        {
            motionX *= 0.8;
        }
    }

    public abstract void onLivingUpdate();

    @Override
    public void move(double x, double y)
    {
        if (x > 0)
        {
            facing = false;
        }
        else if (x < 0)
        {
            facing = true;
        }

        super.move(x, y);
        double prev = walkAmount;
        walkAmount += Math.abs(x) / 4;
        walkAmount %= 2;
        walkDelta = Math.abs(walkAmount - prev);
    }
}
