package com.fiskmods.heroes.client.json.cloud;

import java.io.IOException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class ParticleColor
{
    private static final Pattern PATTERN = Pattern.compile("(red|green|blue) (\\+|-|\\*|\\/) (.+)");

    final float[][] data;
    int link;

    Function<Integer, Integer> getter = i -> i;
    Function<Float, Float> operation = f -> f;

    public ParticleColor(float[] start, float[] end)
    {
        data = new float[][] {start, end};
    }

    public ParticleColor(float base, float rand)
    {
        data = new float[][] {{base, rand}, {base, rand}};
    }

    public ParticleColor(float startBase, float startRand, float endBase, float endRand)
    {
        this(new float[] {startBase, startRand}, new float[] {endBase, endRand});
    }

    public ParticleColor(float factor)
    {
        this(factor, 0, factor, 0);
    }

    public static ParticleColor read(JsonReader in) throws IOException
    {
        if (in.peek() == JsonToken.NUMBER)
        {
            return new ParticleColor((float) in.nextDouble());
        }
        else if (in.peek() == JsonToken.STRING)
        {
            ParticleColor c = new ParticleColor(0);
            Matcher m = PATTERN.matcher(in.nextString());

            try
            {
                if (m.matches())
                {
                    String s = m.group(1);
                    String s1 = m.group(2);
                    int i = "blue".equals(s) ? 2 : "green".equals(s) ? 1 : 0;
                    float f = Float.valueOf(m.group(3));

                    c.getter = t -> i;
                    c.operation = t -> "/".equals(s1) ? t / f : "*".equals(s1) ? t * f : "-".equals(s1) ? t - f : t + f;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return c;
        }
        else if (in.peek() == JsonToken.BEGIN_OBJECT)
        {
            float[] start = null, end = null;
            float base = 0, rand = 0;

            in.beginObject();

            while (in.hasNext())
            {
                if (in.peek() == JsonToken.NAME)
                {
                    String name = in.nextName();

                    if (name.equals("start"))
                    {
                        start = readFloats(in);
                        continue;
                    }
                    else if (name.equals("end"))
                    {
                        end = readFloats(in);
                        continue;
                    }
                    else if (in.peek() == JsonToken.NUMBER)
                    {
                        if (name.equals("base"))
                        {
                            base = (float) in.nextDouble();
                            continue;
                        }
                        else if (name.equals("rand"))
                        {
                            rand = (float) in.nextDouble();
                            continue;
                        }
                    }
                }

                in.skipValue();
            }

            in.endObject();

            if (start != null && end != null)
            {
                return new ParticleColor(start, end);
            }

            return new ParticleColor(base, rand);
        }

        in.skipValue();
        return null;
    }

    private static float[] readFloats(JsonReader in) throws IOException
    {
        if (in.peek() == JsonToken.NUMBER)
        {
            return new float[] {(float) in.nextDouble(), 0};
        }
        else if (in.peek() == JsonToken.BEGIN_OBJECT)
        {
            float[] afloat = new float[2];
            in.beginObject();

            while (in.hasNext())
            {
                if (in.peek() == JsonToken.NAME)
                {
                    String name = in.nextName();

                    if (in.peek() == JsonToken.NUMBER)
                    {
                        if (name.equals("base"))
                        {
                            afloat[0] = (float) in.nextDouble();
                            continue;
                        }
                        else if (name.equals("rand"))
                        {
                            afloat[1] = (float) in.nextDouble();
                            continue;
                        }
                    }
                }

                in.skipValue();
            }

            in.endObject();
            return afloat;
        }

        in.skipValue();
        return new float[2];
    }
}
