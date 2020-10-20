package com.fiskmods.heroes.gameboii.engine;

import javax.vecmath.Vector2d;

public class BoundingBox
{
    public double minX;
    public double minY;
    public double maxX;
    public double maxY;

    public static BoundingBox getBoundingBox(double minX, double minY, double maxX, double maxY)
    {
        return new BoundingBox(minX, minY, maxX, maxY);
    }

    protected BoundingBox(double minX, double minY, double maxX, double maxY)
    {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public BoundingBox setBounds(double minX, double minY, double maxX, double maxY)
    {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        return this;
    }

    public BoundingBox addCoord(double x, double y)
    {
        double d0 = minX;
        double d1 = minY;
        double d2 = maxX;
        double d3 = maxY;

        if (x < 0)
        {
            d0 += x;
        }

        if (x > 0)
        {
            d2 += x;
        }

        if (y < 0)
        {
            d1 += y;
        }

        if (y > 0)
        {
            d3 += y;
        }

        return getBoundingBox(d0, d1, d2, d3);
    }

    public BoundingBox expand(double x, double y)
    {
        return getBoundingBox(minX - x, minY - y, maxX + x, maxY + y);
    }

    public BoundingBox merge(BoundingBox box)
    {
        double d0 = Math.min(minX, box.minX);
        double d1 = Math.min(minY, box.minY);
        double d2 = Math.max(maxX, box.maxX);
        double d3 = Math.max(maxY, box.maxY);

        return getBoundingBox(d0, d1, d2, d3);
    }

    public BoundingBox getOffsetBoundingBox(double x, double y)
    {
        return getBoundingBox(minX + x, minY + y, maxX + x, maxY + y);
    }

    public boolean intersectsWith(BoundingBox box)
    {
        return box.maxX > minX && box.minX < maxX && box.maxY > minY && box.minY < maxY;
    }

    public BoundingBox offset(double x, double y)
    {
        minX += x;
        minY += y;
        maxX += x;
        maxY += y;
        return this;
    }

    public boolean isVecInside(Vector2d vec)
    {
        return vec.x > minX && vec.x < maxX && vec.y > minY && vec.y < maxY;
    }

    public double getAverageEdgeLength()
    {
        double d0 = maxX - minX;
        double d1 = maxY - minY;
        return (d0 + d1) / 2.0;
    }

    public BoundingBox contract(double x, double y)
    {
        return getBoundingBox(minX + x, minY + y, maxY - y, maxX - x);
    }

    public BoundingBox copy()
    {
        return getBoundingBox(minX, minY, maxX, maxY);
    }

    public void setBB(BoundingBox box)
    {
        minX = box.minX;
        minY = box.minY;
        maxX = box.maxX;
        maxY = box.maxY;
    }

    public double calculateXOffset(BoundingBox box, double d0)
    {
        if (box.maxY > minY && box.minY < maxY)
        {
            double d1;

            if (d0 > 0 && box.maxX <= minX)
            {
                d1 = minX - box.maxX;

                if (d1 < d0)
                {
                    d0 = d1;
                }
            }

            if (d0 < 0 && box.minX >= maxX)
            {
                d1 = maxX - box.minX;

                if (d1 > d0)
                {
                    d0 = d1;
                }
            }

            return d0;
        }
        else
        {
            return d0;
        }
    }

    public double calculateYOffset(BoundingBox box, double d0)
    {
        if (box.maxX > minX && box.minX < maxX)
        {
            double d1;

            if (d0 > 0 && box.maxY <= minY)
            {
                d1 = minY - box.maxY;

                if (d1 < d0)
                {
                    d0 = d1;
                }
            }

            if (d0 < 0 && box.minY >= maxY)
            {
                d1 = maxY - box.minY;

                if (d1 > d0)
                {
                    d0 = d1;
                }
            }

            return d0;
        }
        else
        {
            return d0;
        }
    }

    @Override
    public String toString()
    {
        return "box[" + minX + ", " + minY + " -> " + maxX + ", " + maxY + "]";
    }
}
