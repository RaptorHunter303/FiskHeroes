package com.fiskmods.heroes.common;

import com.google.common.base.Supplier;

import net.minecraft.entity.Entity;

public class Vec3Container
{
    private final Supplier<Double> x, y, z;

    public Vec3Container(Supplier<Double> x, Supplier<Double> y, Supplier<Double> z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX()
    {
        return x.get();
    }

    public double getY()
    {
        return y.get();
    }

    public double getZ()
    {
        return z.get();
    }

    public static Vec3Container wrap(Entity entity)
    {
        return new Vec3Container(() -> entity.getPosX(), () -> entity.getBoundingBox().minY + entity.getPosYHeight(2) / 2, () -> entity.getPosZ());
    }
}
