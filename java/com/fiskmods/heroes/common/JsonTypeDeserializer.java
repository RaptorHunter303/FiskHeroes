package com.fiskmods.heroes.common;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public abstract class JsonTypeDeserializer<T> extends TypeAdapter<T>
{
    @Override
    public final void write(JsonWriter out, T value) throws IOException
    {
        throw new IllegalStateException(String.format("Type %s cannot be JSON serialized!", getClass().getName()));
    }

    @Override
    public T read(JsonReader in) throws IOException
    {
        if (in.peek() == JsonToken.NULL)
        {
            in.nextNull();
            return null;
        }

        return deserialize(in);
    }

    public abstract T deserialize(JsonReader in) throws IOException;
}
