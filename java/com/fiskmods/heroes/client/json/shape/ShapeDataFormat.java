package com.fiskmods.heroes.client.json.shape;

public enum ShapeDataFormat
{
    CIRCLES(ShapeFormatCircles.INSTANCE),
    LINES(ShapeFormatLines.INSTANCE),
    WIREFRAME(ShapeFormatWireframe.INSTANCE);

    public final IShapeFormat format;

    ShapeDataFormat(IShapeFormat format)
    {
        this.format = format;
    }
}
