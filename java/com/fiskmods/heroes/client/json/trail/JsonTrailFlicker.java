package com.fiskmods.heroes.client.json.trail;

import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public class JsonTrailFlicker extends JsonConstantContainer implements ITrailAspect
{
    private final JsonConstant<Integer> color;
    private final JsonConstant<Integer> density;
    private final JsonConstant<Float> frequency;
    private final JsonConstant<Float> length;
    private final JsonConstant<Float> spread;
    private final JsonConstant<Float> opacity;

    private final JsonConstant<Boolean> confined;

    private Vec3 vecColor;

    public JsonTrailFlicker()
    {
        color = init("color", Integer.class, -1);
        density = init("density", Integer.class, 16);
        frequency = init("frequency", Float.class, 0.4F);
        length = init("length", Float.class, 0.1F);
        spread = init("spread", Float.class, 1F);
        opacity = init("opacity", Float.class, 1F);

        confined = init("confined", Boolean.class, false);
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

    public Float getFrequency()
    {
        return frequency.get();
    }

    public Float getLength()
    {
        return length.get();
    }

    public Float getSpread()
    {
        return spread.get();
    }

    public Float getOpacity()
    {
        return opacity.get();
    }

    public Boolean isConfined()
    {
        return confined.get();
    }
}
