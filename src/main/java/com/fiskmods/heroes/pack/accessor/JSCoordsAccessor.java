package com.fiskmods.heroes.pack.accessor;

import com.fiskmods.heroes.common.DimensionalCoords;

public class JSCoordsAccessor
{
    public static final JSCoordsAccessor EMPTY = new JSCoordsAccessor(new DimensionalCoords());

    private final DimensionalCoords coords;

    private JSCoordsAccessor(DimensionalCoords coords)
    {
        this.coords = coords;
    }

    public static JSCoordsAccessor wrap(DimensionalCoords coords)
    {
        return coords != null ? new JSCoordsAccessor(coords) : EMPTY;
    }

    public int xCoord()
    {
        return coords.posX;
    }

    public int yCoord()
    {
        return coords.posY;
    }

    public int zCoord()
    {
        return coords.posZ;
    }

    public int dimension()
    {
        return coords.dimension;
    }

    @Override
    public String toString()
    {
        return String.format("%s,%s,%s,%s", xCoord(), yCoord(), zCoord(), dimension());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj instanceof String && toString().equals(obj))
        {
            return true;
        }

        return toString().equals(String.valueOf(obj));
    }
}
