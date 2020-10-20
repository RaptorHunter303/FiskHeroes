package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.google.common.collect.ImmutableList;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;

public class HeroEffectHeatVision extends HeroEffect
{
    public static final ImmutableList<Beam> DEFAULT_BEAMS;

    static
    {
        ImmutableList.Builder<Beam> builder = ImmutableList.builder();
        builder.add(new Beam(-2, 3.5F, 1, 0.5F));
        builder.add(new Beam(2, 3.5F, 1, 0.5F));

        DEFAULT_BEAMS = builder.build();
    }

    protected int color;
    protected List<Beam> beams;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return false;
    }

    public int getColor()
    {
        return color;
    }

    public List<Beam> getBeams()
    {
        return beams;
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("color"))
        {
            color = readInt(in, 0);
        }
        else if (name.equals("beams") && next == JsonToken.BEGIN_ARRAY)
        {
            beams = new ArrayList<>();
            in.beginArray();

            while (in.hasNext())
            {
                if (in.peek() == JsonToken.BEGIN_OBJECT)
                {
                    Beam beam = new Beam();
                    in.beginObject();

                    while (in.hasNext())
                    {
                        if (in.peek() == JsonToken.NAME)
                        {
                            name = in.nextName();

                            if (in.peek() == JsonToken.BEGIN_ARRAY)
                            {
                                if (name.equals("offset"))
                                {
                                    beam.loadOffset(readArray(in, new float[2], f -> f));
                                    continue;
                                }
                                else if (name.equals("size"))
                                {
                                    beam.loadSize(readArray(in, new float[2], f -> f));
                                    continue;
                                }
                            }
                        }

                        in.skipValue();
                    }

                    in.endObject();
                    beams.add(beam);
                }
                else
                {
                    in.skipValue();
                }
            }

            in.endArray();
        }
        else
        {
            in.skipValue();
        }
    }

    public static class Beam
    {
        public float x;
        public float y;
        public float width;
        public float height;

        public Beam(float x, float y, float width, float height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Beam()
        {
            this(0, 0, 1, 1);
        }

        public void loadOffset(float[] afloat)
        {
            x = afloat[0];
            y = afloat[1];
        }

        public void loadSize(float[] afloat)
        {
            width = afloat[0];
            height = afloat[1];
        }
    }
}
