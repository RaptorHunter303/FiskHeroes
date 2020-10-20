package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import com.fiskmods.heroes.client.json.hero.FloatData;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class HeroEffectArmAnimation extends HeroEffect
{
    protected FloatData swordPose = FloatData.NULL;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return false;
    }

    public float swordPose(Entity entity, ItemStack itemstack)
    {
        return swordPose.get(entity, itemstack, 0);
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("dataSwordPose"))
        {
            swordPose = FloatData.read(in);
        }
    }
}
