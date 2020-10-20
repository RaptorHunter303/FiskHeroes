package com.fiskmods.heroes.client.json.trail;

import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public class JsonTrailLightning extends JsonConstantContainer implements ITrailAspect
{
    private final JsonConstant<Integer> color;
    private final JsonConstant<Integer> density;
    private final JsonConstant<Float> differ;
    private final JsonConstant<Float> opacity;

//    private final JsonConstant<Integer[]> colors;
    private Vec3 vecColor;

    public JsonTrailLightning()
    {
        color = init("color", Integer.class, -1);
        density = init("density", Integer.class, 6);
        differ = init("differ", Float.class, 0.435F);
        opacity = init("opacity", Float.class, 1F);

//        colors = init("colors", Integer[].class, new Integer[1]);
    }

    @Override
    public Integer getColor()
    {
        return color.get();
    }

    @Override
    public Integer getColor(EntityLivingBase entity)
    {
        return getColor();
    }

    @Override
    public Vec3 getVecColor()
    {
        if (vecColor == null)
        {
            vecColor = SHRenderHelper.getColorFromHex(getColor());
        }

        return vecColor;
    }

    @Override
    public Vec3 getVecColor(EntityLivingBase entity)
    {
        return getVecColor();
    }

    @Override
    public boolean isLightning()
    {
        return true;
    }

    public Integer getDensity()
    {
        return density.get();
    }

    public Float getDiffer()
    {
        return differ.get();
    }

    public Float getOpacity()
    {
        return opacity.get();
    }
}
