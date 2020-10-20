package com.fiskmods.heroes.client.json.cloud;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.fiskmods.heroes.common.JsonTypeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class JsonCloud
{
    public static final GsonBuilder GSON_FACTORY = new GsonBuilder().registerTypeAdapter(JsonCloud.class, new JsonCloud.Adapter());
    public static final Map<ResourceLocation, JsonCloud> CACHE = new HashMap<>();

    public static final String MODEL_DIR = "models/clouds/";

    public String parent;
    public Integer fade;

    public ParticleColor[] color;

    public static JsonCloud read(Gson gson, IResourceManager manager, ResourceLocation location) throws IOException
    {
        return readInternal(gson, manager, location, new LinkedList<>());
    }

    public static JsonCloud read(Gson gson, IResourceManager manager, Reader reader, ResourceLocation location) throws IOException
    {
        return readInternal(gson, manager, reader, location, new LinkedList<>());
    }

    private static JsonCloud readInternal(Gson gson, IResourceManager manager, ResourceLocation location, LinkedList<ResourceLocation> hierarchy) throws IOException
    {
        if (CACHE.containsKey(location))
        {
            return CACHE.get(location);
        }

        IResource iresource = manager.getResource(new ResourceLocation(location.getResourceDomain(), MODEL_DIR + location.getResourcePath() + ".json"));
        InputStreamReader reader = new InputStreamReader(iresource.getInputStream());

        return readInternal(gson, manager, reader, location, hierarchy);
    }

    private static JsonCloud readInternal(Gson gson, IResourceManager manager, Reader reader, ResourceLocation location, LinkedList<ResourceLocation> hierarchy) throws IOException
    {
        if (CACHE.containsKey(location))
        {
            return CACHE.get(location);
        }

        JsonCloud cloud = null;

        try
        {
            cloud = gson.fromJson(reader, JsonCloud.class);
            hierarchy.add(location);
        }
        catch (Exception e)
        {
            throw new IOException(String.format("Failed to load particle cloud renderer '%s'", location), e);
        }

        if (cloud.parent != null)
        {
            ResourceLocation parentLocation = new ResourceLocation(cloud.parent);

            if (hierarchy.contains(parentLocation))
            {
                throw new IOException(String.format("Infinite inheritance loop! %s", hierarchy));
            }

            cloud.inherit(readInternal(gson, manager, parentLocation, hierarchy));
        }

        if (cloud.fade == null)
        {
            cloud.fade = 10;
        }

        if (location != null)
        {
            CACHE.put(location, cloud);
        }

        return cloud;
    }

    public void inherit(JsonCloud parent) throws IOException
    {
        if (color == null && parent.color != null)
        {
            color = parent.color;
        }

        if (fade == null && parent.fade != null)
        {
            fade = parent.fade;
        }

        if (color == null)
        {
            throw new IOException(String.format("Particle cloud can't have no aspects!"));
        }
    }

    public static class Adapter extends JsonTypeDeserializer<JsonCloud>
    {
        @Override
        public JsonCloud deserialize(JsonReader in) throws IOException
        {
            JsonCloud cloud = new JsonCloud();
            in.beginObject();

            while (in.hasNext())
            {
                if (in.peek() == JsonToken.NAME)
                {
                    String name = in.nextName();

                    if (name.equals("parent") && in.peek() == JsonToken.STRING)
                    {
                        cloud.parent = in.nextString();
                    }
                    else if (name.equals("color") && in.peek() == JsonToken.BEGIN_OBJECT)
                    {
                        ParticleColor r = null, g = null, b = null;
                        in.beginObject();

                        while (in.hasNext())
                        {
                            if (in.peek() == JsonToken.NAME)
                            {
                                name = in.nextName();
                                ParticleColor c = ParticleColor.read(in);
                                String[] as = name.split(",");
                                int i = 0;

                                for (String s : as)
                                {
                                    if (s.equals("red"))
                                    {
                                        r = c;
                                        i |= 1;
                                    }
                                    else if (s.equals("green"))
                                    {
                                        g = c;
                                        i |= 2;
                                    }
                                    else if (s.equals("blue"))
                                    {
                                        b = c;
                                        i |= 4;
                                    }
                                }

//                                if (i == 6)
//                                {
//                                    c.link = 1;
//                                }
//                                else if (i != 7 && i != 5 && i != 3)
//                                {
//                                    c.link = i == 4 ? 2 : i - 1;
//                                }

                                c.link = i == 6 ? 1 : i != 7 && i != 5 && i != 3 ? i == 4 ? 2 : i - 1 : 0;
                                continue;
                            }

                            in.skipValue();
                        }

                        if (r != null && g != null && b != null)
                        {
                            cloud.color = new ParticleColor[] {r, g, b};
                        }

                        in.endObject();
                    }
                    else if (name.equals("fade") && in.peek() == JsonToken.NUMBER)
                    {
                        cloud.fade = in.nextInt();
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

            return cloud;
        }
    }
}
