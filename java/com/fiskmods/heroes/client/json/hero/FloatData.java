package com.fiskmods.heroes.client.json.hero;

import java.io.IOException;
import java.util.function.BiFunction;

import com.google.gson.stream.JsonReader;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class FloatData
{
    public static final FloatData NULL = new FloatData(null)
    {
        @Override
        public float get(Entity entity, ItemStack stack, float defValue)
        {
            return defValue;
        }
    };

    private final String key;
    private BiFunction<Entity, ItemStack, Object> interpreter;

    private FloatData(String key)
    {
        this.key = key;
    }

    public float get(Entity entity, ItemStack itemstack, float defValue)
    {
        if (interpreter == null)
        {
            interpreter = JsonHeroVar.createInterpreter(key, true);
        }

        Object obj = interpreter.apply(entity, itemstack);
        return obj instanceof Number ? ((Number) obj).floatValue() : defValue;
    }

    public static FloatData read(JsonReader in) throws IOException
    {
        switch (in.peek())
        {
        case STRING:
            return new FloatData(in.nextString());
        case NUMBER:
            return new FloatData(String.valueOf(in.nextDouble()));
        default:
            in.skipValue();
        }

        return NULL;
    }
}
