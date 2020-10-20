package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;

public class HeroEffectSpellcasting extends HeroEffect
{
    protected Integer colorGeneric;
    protected Integer colorEarthCrack;
    protected Integer colorAtmosphere;
    protected Integer colorWhip;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return false;
    }

    public Integer getGenericColor(int defValue)
    {
        return colorGeneric != null ? colorGeneric : defValue;
    }

    public Integer getEarthCrackColor(int defValue)
    {
        return colorEarthCrack != null ? colorEarthCrack : defValue;
    }

    public Integer getAtmosphereColor(int defValue)
    {
        return colorAtmosphere != null ? colorAtmosphere : defValue;
    }

    public Integer getWhipColor(int defValue)
    {
        return colorWhip != null ? colorWhip : defValue;
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("colorGeneric"))
        {
            colorGeneric = readInt(in, 0);
        }
        else if (name.equals("colorEarthCrack"))
        {
            colorEarthCrack = readInt(in, 0);
        }
        else if (name.equals("colorAtmosphere"))
        {
            colorAtmosphere = readInt(in, 0);
        }
        else if (name.equals("colorWhip"))
        {
            colorWhip = readInt(in, 0);
        }
        else
        {
            in.skipValue();
        }
    }
}
