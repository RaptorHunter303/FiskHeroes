package com.fiskmods.heroes.client.json.shape;

import java.io.IOException;

import com.fiskmods.heroes.client.render.hero.effect.HeroEffect;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class ShapeEntry
{
    public final float[] data, rotation;

    public ShapeEntry(float[] data, float[] rotation)
    {
        this.data = data;
        this.rotation = rotation;
    }

    public static ShapeEntry read(JsonReader in, IShapeFormat format) throws IOException
    {
        if (in.peek() == JsonToken.BEGIN_OBJECT)
        {
            float[] data = null, rotation = null;
            in.beginObject();

            while (in.hasNext())
            {
                if (in.peek() == JsonToken.NAME)
                {
                    String name = in.nextName();

                    if (name.equals("data") && in.peek() == JsonToken.BEGIN_ARRAY)
                    {
                        data = format.read(in);
                        continue;
                    }
                    else if (name.equals("rotation") && in.peek() == JsonToken.BEGIN_ARRAY)
                    {
                        rotation = HeroEffect.readArray(in, new float[3], f -> (float) Math.toRadians(f));
                        continue;
                    }
                }

                in.skipValue();
            }

            in.endObject();

            if (data != null)
            {
                return new ShapeEntry(data, rotation);
            }
        }
        else if (in.peek() == JsonToken.BEGIN_ARRAY)
        {
            return new ShapeEntry(format.read(in), new float[3]);
        }
        else
        {
            in.skipValue();
        }

        return null;
    }
}
