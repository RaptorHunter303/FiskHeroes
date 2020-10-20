package com.fiskmods.heroes.client.json.hero;

import java.io.IOException;

import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.google.gson.stream.JsonReader;

public class MultiTexture
{
    public static final MultiTexture NULL = new MultiTexture("missingno", null);

    public final String baseTexture;
    public final String glowTexture;

    private MultiTexture(String base, String glow)
    {
        baseTexture = base;
        glowTexture = glow;
    }

    public String get(int pass)
    {
        return pass == HeroRenderer.Pass.GLOW ? glowTexture : baseTexture;
    }

    public boolean hasTexture(int pass)
    {
        return HeroRenderer.Pass.isTexturePass(pass) && get(pass) != null;
    }

    public static MultiTexture read(JsonReader in) throws IOException
    {
        switch (in.peek())
        {
        case STRING:
            return new MultiTexture(in.nextString(), null);
        case BEGIN_ARRAY:
            String base = null;
            String glow = null;
            int i = 0;

            in.beginArray();

            while (in.hasNext())
            {
                ++i;

                if (i == 1)
                {
                    base = readString(in);
                    continue;
                }
                else if (i == 2)
                {
                    glow = readString(in);
                    continue;
                }

                in.skipValue();
            }

            in.endArray();
            return new MultiTexture(base, glow);
        default:
            in.skipValue();
        }

        return NULL;
    }

    private static String readString(JsonReader in) throws IOException
    {
        switch (in.peek())
        {
        case STRING:
            String s = in.nextString();
            return "null".equals(s) ? null : s;
        case NULL:
            in.nextNull();
            return null;
        default:
            in.skipValue();
            return null;
        }
    }
}
