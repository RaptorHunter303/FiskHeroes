package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;

public class HeroEffectLightningAttack extends HeroEffect
{
    protected int color;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return false;
    }

    public int getColor()
    {
        return color;
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("color"))
        {
            color = readInt(in, 0);
        }
        else
        {
            in.skipValue();
        }
    }
}
