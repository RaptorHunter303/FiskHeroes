package com.fiskmods.heroes.pack.accessor;

import net.minecraft.util.MathHelper;

public class JSVectorAccessor implements JSAccessor<JSVectorAccessor>
{
    private double xCoord, yCoord, zCoord;

    private JSVectorAccessor(double x, double y, double z)
    {
        xCoord = x;
        yCoord = y;
        zCoord = z;
    }

    @Override
    public boolean matches(JSVectorAccessor t)
    {
        return xCoord == t.xCoord && yCoord == t.yCoord && zCoord == t.zCoord;
    }

    public static JSVectorAccessor wrap(double x, double y, double z)
    {
        return new JSVectorAccessor(x, y, z);
    }

    public JSVectorAccessor add(double x, double y, double z)
    {
        return new JSVectorAccessor(xCoord + x, yCoord + y, zCoord + z);
    }

    public JSVectorAccessor multiply(double x, double y, double z)
    {
        return new JSVectorAccessor(xCoord * x, yCoord * y, zCoord * z);
    }

    public JSVectorAccessor multiply(double factor)
    {
        return multiply(factor, factor, factor);
    }

    public int[] toIntArray()
    {
        return new int[] {MathHelper.floor_double(xCoord), MathHelper.floor_double(yCoord), MathHelper.floor_double(zCoord)};
    }
}
