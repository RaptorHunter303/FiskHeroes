package com.fiskmods.heroes.client.json.trail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public abstract class JsonConstantContainer
{
    private final Map<String, JsonConstant> map = new HashMap<>();

    protected <T> JsonConstant<T> init(String key, Class<T> type, T defVal)
    {
        JsonConstant<T> constant = new JsonConstant<>(key, type, defVal);
        map.put(key, constant);

        return constant;
    }

    public void inherit(JsonConstantContainer parent) throws IOException
    {
        for (Map.Entry<String, JsonConstant> e : map.entrySet())
        {
            e.getValue().inherit(parent.map.get(e.getKey()));
        }
    }

    public void postInit(JsonTrail trail) throws IOException
    {
        for (JsonConstant constant : map.values())
        {
            constant.postInit(trail);
        }
    }

    public JsonConstantContainer read(JsonReader in) throws IOException
    {
        in.beginObject();
        String name = "";

        while (in.hasNext())
        {
            JsonToken next = in.peek();

            if (next == JsonToken.NAME)
            {
                name = in.nextName();
            }
            else if (next == JsonToken.BOOLEAN || next == JsonToken.NULL || next == JsonToken.NUMBER || next == JsonToken.STRING)
            {
                if (map.containsKey(name))
                {
                    map.get(name).read(in);
                }
                else
                {
                    in.skipValue();
                }
            }
            else
            {
                in.skipValue();
            }
        }

        in.endObject();

        return this;
    }
}
