package com.fiskmods.heroes.gameboii.graphics;

import java.awt.image.BufferedImage;

import net.minecraft.util.MathHelper;

public class ResourceAnimated extends Resource
{
    private float progress;

    public ResourceAnimated(int width, int height, BufferedImage[] data)
    {
        super(width, height, null);
        supplier = () -> data[Math.min(MathHelper.floor_float(progress * (data.length + 1)), data.length) % data.length];
    }

    public ResourceAnimated frame(float f)
    {
        progress = f;
        return this;
    }
}
