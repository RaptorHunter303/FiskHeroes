package com.fiskmods.heroes.client.json.cloud;

import java.util.Random;

import com.fiskmods.heroes.util.FiskServerUtils;

public class ParticleColors
{
    private final float[][] data = new float[3][2];

    public ParticleColors(ParticleColor[] color, Random rand)
    {
        float[] r = {rand.nextFloat(), rand.nextFloat(), rand.nextFloat()};

        for (int i = 0; i < 3; ++i)
        {
            ParticleColor c0 = color[i];
            ParticleColor c = color[c0.getter.apply(i)];

            for (int j = 0; j < 2; ++j)
            {
                data[i][j] = c0.operation.apply(c.data[j][0] + c.data[j][1] * r[c.link]);
            }
        }
    }

    public float red(float f)
    {
        return get(0, f);
    }

    public float green(float f)
    {
        return get(1, f);
    }

    public float blue(float f)
    {
        return get(2, f);
    }

    private float get(int rgb, float f)
    {
        return FiskServerUtils.interpolate(data[rgb][0], data[rgb][1], f);
    }
}
