package com.fiskmods.heroes.client.json.shape;

import java.io.IOException;

import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.google.gson.stream.JsonReader;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface IShapeFormat
{
    float[] read(JsonReader in) throws IOException;

    void render(JsonShape shape, ModelBipedMultiLayer model, Entity entity, ItemStack itemstack, int pass, float s, float mult, float ticks, float scale, boolean left);
}
