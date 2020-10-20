package com.fiskmods.heroes.gameboii.level;

import java.util.List;

import com.fiskmods.heroes.gameboii.engine.BoundingBox;

import net.minecraft.util.MathHelper;

public abstract class MovingLevelObject extends LevelObject
{
    public double motionX;
    public double motionY;

    public boolean onGround;

    public MovingLevelObject(double x, double y, int width, int height)
    {
        super(x, y, width, height);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (level != null)
        {
            double mx = MathHelper.clamp_double(motionX, -0.01, 0.01);
            double my = MathHelper.clamp_double(motionY, -0.01, 0.01);
            move(motionX, motionY);

            for (LevelObject obj : level.getIntersectingObjects(this, boundingBox.addCoord(mx, my)))
            {
                onCollideWith(obj);
                obj.onCollideWith(this);
            }

            if (hasGravity())
            {
                float f = 0.9F;
                motionY -= 1.5;
                motionX *= f;
                motionY *= f;
            }
        }
    }

    public boolean hasGravity()
    {
        return true;
    }

    public void move(double x, double y)
    {
        List<BoundingBox> list = level.getCollidingBoundingBoxes(this, boundingBox.addCoord(x, y));
        double x1 = x;
        double y1 = y;

        for (BoundingBox element : list)
        {
            y = element.calculateYOffset(boundingBox, y);
        }

        boundingBox.offset(0, y);

        for (BoundingBox element : list)
        {
            x = element.calculateXOffset(boundingBox, x);
        }

        boundingBox.offset(x, 0);
        onGround = y1 != y && y1 < 0;

        if (x1 != x)
        {
            motionX = 0;
        }

        if (y1 != y)
        {
            motionY = 0;
        }

        posX = (boundingBox.minX + boundingBox.maxX) / 2.0;
        posY = boundingBox.maxY;
    }

    public void setPosition(double x, double y)
    {
        posX = prevPosX = x;
        posY = prevPosY = y;
        setSize(width, height);
    }
}
