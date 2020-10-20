package com.fiskmods.heroes.client.json.hero;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;

public class Conditionals
{
    private Set<JsonHeroVar> conditionals;
    private Set<String> conditionalsQueue;

    public boolean evaluate(Entity entity)
    {
        if (conditionals != null)
        {
            return conditionals.stream().allMatch(t -> t.test(entity, null));
        }

        return true;
    }

    public void read(JsonReader in) throws IOException
    {
        if (in.peek() == JsonToken.BEGIN_ARRAY)
        {
            conditionalsQueue = new HashSet<>();
            in.beginArray();

            while (in.hasNext())
            {
                if (in.peek() == JsonToken.STRING)
                {
                    String s = in.nextString();

                    if (s.startsWith("vars:"))
                    {
                        conditionalsQueue.add(s.substring("vars:".length()));
                    }

                    continue;
                }

                in.skipValue();
            }

            in.endArray();
        }
        else
        {
            in.skipValue();
        }
    }

    public void init(JsonHeroRenderer json)
    {
        if (conditionalsQueue != null && json.variables != null)
        {
            conditionals = new HashSet<>();
            conditionalsQueue.stream().filter(json.variables::containsKey).forEach(s -> conditionals.add(json.variables.get(s)));
        }
    }
}
