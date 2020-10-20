package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class HeroEffectEnergyProj extends HeroEffectHeatVision
{
    protected boolean useHands;

    public boolean shouldUseHands()
    {
        return useHands;
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("useHands") && next == JsonToken.BOOLEAN)
        {
            useHands = in.nextBoolean();
        }
        else
        {
            super.read(in, name, next);
        }
    }
}
