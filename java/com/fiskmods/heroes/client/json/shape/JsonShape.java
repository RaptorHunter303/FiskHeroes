package com.fiskmods.heroes.client.json.shape;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fiskmods.heroes.common.JsonTypeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class JsonShape
{
    public static final GsonBuilder GSON_FACTORY = new GsonBuilder().registerTypeAdapter(JsonShape.class, new JsonShape.Adapter());
    public static final Map<ResourceLocation, JsonShape> CACHE = new HashMap<>();

    public static final String MODEL_DIR = "models/shapes/";

    public String parent;
    public Float maxAngle;

    public ShapeDataFormat dataFormat = ShapeDataFormat.CIRCLES;
    public List<ShapeEntry> shapes = new ArrayList<>();

    public static JsonShape read(Gson gson, IResourceManager manager, ResourceLocation location) throws IOException
    {
        return readInternal(gson, manager, location, new LinkedList<>());
    }

    public static JsonShape read(Gson gson, IResourceManager manager, Reader reader, ResourceLocation location) throws IOException
    {
        return readInternal(gson, manager, reader, location, new LinkedList<>());
    }

    private static JsonShape readInternal(Gson gson, IResourceManager manager, ResourceLocation location, LinkedList<ResourceLocation> hierarchy) throws IOException
    {
        if (CACHE.containsKey(location))
        {
            return CACHE.get(location);
        }

        IResource iresource = manager.getResource(new ResourceLocation(location.getResourceDomain(), MODEL_DIR + location.getResourcePath() + ".json"));
        InputStreamReader reader = new InputStreamReader(iresource.getInputStream());

        return readInternal(gson, manager, reader, location, hierarchy);
    }

    private static JsonShape readInternal(Gson gson, IResourceManager manager, Reader reader, ResourceLocation location, LinkedList<ResourceLocation> hierarchy) throws IOException
    {
        if (CACHE.containsKey(location))
        {
            return CACHE.get(location);
        }

        JsonShape shape = null;

        try
        {
            shape = gson.fromJson(reader, JsonShape.class);
            hierarchy.add(location);
        }
        catch (Exception e)
        {
            throw new IOException(String.format("Failed to load shape renderer '%s'", location), e);
        }

        if (shape.parent != null)
        {
            ResourceLocation parentLocation = new ResourceLocation(shape.parent);

            if (hierarchy.contains(parentLocation))
            {
                throw new IOException(String.format("Infinite inheritance loop! %s", hierarchy));
            }

            shape.inherit(readInternal(gson, manager, parentLocation, hierarchy));
        }

        if (shape.maxAngle == null)
        {
            shape.maxAngle = 360F;
        }

        if (location != null)
        {
            CACHE.put(location, shape);
        }

        return shape;
    }

    public void inherit(JsonShape parent) throws IOException
    {
        if (shapes == null && parent.shapes != null)
        {
            shapes = parent.shapes;
        }

        if (!parent.shapes.isEmpty())
        {
            if (shapes.isEmpty())
            {
                shapes = new ArrayList<>(parent.shapes);
            }
            else
            {
                List<ShapeEntry> list = shapes;
                shapes = new ArrayList<>(parent.shapes);
                shapes.addAll(list);
            }
        }

        if (maxAngle == null && parent.maxAngle != null)
        {
            maxAngle = parent.maxAngle;
        }

        if (shapes == null)
        {
            throw new IOException(String.format("Shape can't have no component shape data!"));
        }
    }

    public static class Adapter extends JsonTypeDeserializer<JsonShape>
    {
        @Override
        public JsonShape deserialize(JsonReader in) throws IOException
        {
            JsonShape shape = new JsonShape();
            in.beginObject();

            while (in.hasNext())
            {
                if (in.peek() == JsonToken.NAME)
                {
                    String name = in.nextName();

                    if (name.equals("parent") && in.peek() == JsonToken.STRING)
                    {
                        shape.parent = in.nextString();
                    }
                    else if (name.equals("shapes") && in.peek() == JsonToken.BEGIN_ARRAY)
                    {
                        in.beginArray();

                        while (in.hasNext())
                        {
                            ShapeEntry entry = ShapeEntry.read(in, shape.dataFormat.format);

                            if (entry != null)
                            {
                                shape.shapes.add(entry);
                            }
                        }

                        in.endArray();
                    }
                    else if (name.equals("dataFormat") && in.peek() == JsonToken.STRING)
                    {
                        try
                        {
                            shape.dataFormat = ShapeDataFormat.valueOf(in.nextString());
                        }
                        catch (IllegalArgumentException e)
                        {
                        }
                    }
                    else if (name.equals("maxAngle") && in.peek() == JsonToken.NUMBER)
                    {
                        shape.maxAngle = (float) in.nextDouble();
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

            return shape;
        }
    }
}
