package com.fiskmods.heroes.client.json.trail;

import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.util.Vec3;

public class JsonTrailBlur extends JsonConstantContainer
{
    private final JsonConstant<Integer> color;
    private final JsonConstant<Float> opacity;

    private Vec3 vecColor;

    public JsonTrailBlur()
    {
        color = init("color", Integer.class, -1);
        opacity = init("opacity", Float.class, 0.5F);
    }

    public Integer getColor()
    {
        return color.get();
    }

    public Vec3 getVecColor()
    {
        if (vecColor == null)
        {
            vecColor = SHRenderHelper.getColorFromHex(getColor());
        }

        return vecColor;
    }

    public Float getOpacity()
    {
        return opacity.get();
    }
}
