package com.fiskmods.heroes.client.json.trail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.fiskmods.heroes.client.json.trail.ITrailAspect.V9Wrapper;
import com.fiskmods.heroes.common.JsonTypeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class JsonTrail
{
    public static final GsonBuilder GSON_FACTORY = new GsonBuilder().registerTypeAdapter(JsonTrail.class, new JsonTrail.Adapter());
    public static final Map<ResourceLocation, JsonTrail> CACHE = new HashMap<>();

    public static final String MODEL_DIR = "models/trails/";

    public String parent;
    public Integer fade;

    public Map<String, Object> constants = new HashMap<>();
    public V9Wrapper<JsonTrailLightning> lightning;
    public V9Wrapper<JsonTrailFlicker> flicker;

    public JsonTrailParticles particles;
    public JsonTrailBlur blur;

    public static JsonTrail read(Gson gson, IResourceManager manager, ResourceLocation location) throws IOException
    {
        JsonTrail trail = readInternal(gson, manager, location, new LinkedList<>());
        trail.postInit();

        return trail;
    }

    public static JsonTrail read(Gson gson, IResourceManager manager, Reader reader, ResourceLocation location) throws IOException
    {
        JsonTrail trail = readInternal(gson, manager, reader, location, new LinkedList<>());
        trail.postInit();

        return trail;
    }

    private static JsonTrail readInternal(Gson gson, IResourceManager manager, ResourceLocation location, LinkedList<ResourceLocation> hierarchy) throws IOException
    {
        if (CACHE.containsKey(location))
        {
            return CACHE.get(location);
        }

        IResource iresource = manager.getResource(new ResourceLocation(location.getResourceDomain(), MODEL_DIR + location.getResourcePath() + ".json"));
        InputStreamReader reader = new InputStreamReader(iresource.getInputStream());

        return readInternal(gson, manager, reader, location, hierarchy);
    }

    private static JsonTrail readInternal(Gson gson, IResourceManager manager, Reader reader, ResourceLocation location, LinkedList<ResourceLocation> hierarchy) throws IOException
    {
        if (CACHE.containsKey(location))
        {
            return CACHE.get(location);
        }

        JsonTrail trail = null;

        try
        {
            trail = gson.fromJson(reader, JsonTrail.class);
            hierarchy.add(location);
        }
        catch (Exception e)
        {
            throw new IOException(String.format("Failed to load trail renderer '%s'", location), e);
        }

        if (trail.parent != null)
        {
            ResourceLocation parentLocation = new ResourceLocation(trail.parent);

            if (hierarchy.contains(parentLocation))
            {
                throw new IOException(String.format("Infinite inheritance loop! %s", hierarchy));
            }

            trail.inherit(readInternal(gson, manager, parentLocation, hierarchy));
        }

        if (trail.fade == null)
        {
            trail.fade = 10;
        }

        if (location != null)
        {
            CACHE.put(location, trail);
        }

        return trail;
    }

    public void inherit(JsonTrail parent) throws IOException
    {
        if (!parent.constants.isEmpty())
        {
            if (constants.isEmpty())
            {
                constants = new HashMap<>(parent.constants);
            }
            else
            {
                Map<String, Object> map = constants;
                constants = new HashMap<>(parent.constants);
                constants.putAll(map);
            }
        }

        if (parent.lightning != null)
        {
            if (lightning == null)
            {
                lightning = new V9Wrapper(new JsonTrailLightning());
            }

            lightning.get().inherit(parent.lightning.get());
        }

        if (parent.flicker != null)
        {
            if (flicker == null)
            {
                flicker = new V9Wrapper(new JsonTrailFlicker());
            }

            flicker.get().inherit(parent.flicker.get());
        }

        if (parent.particles != null)
        {
            if (particles == null)
            {
                particles = new JsonTrailParticles();
            }

            particles.inherit(parent.particles);
        }

        if (parent.blur != null)
        {
            if (blur == null)
            {
                blur = new JsonTrailBlur();
            }

            blur.inherit(parent.blur);
        }

        if (fade == null && parent.fade != null)
        {
            fade = parent.fade;
        }

        if (lightning == null && flicker == null && particles == null && blur == null)
        {
            throw new IOException(String.format("Trail can't have no aspects!"));
        }
    }

    public void postInit() throws IOException
    {
        if (lightning != null)
        {
            lightning.get().postInit(this);
        }

        if (flicker != null)
        {
            flicker.get().postInit(this);
        }

        if (particles != null)
        {
            particles.postInit(this);
        }
    }

    public void load(IResourceManager manager, TextureManager textureManager) throws IOException
    {
        if (particles != null)
        {
            particles.load(this, manager, textureManager);
        }
    }

    public ITrailAspect getTrailLightning()
    {
        if (lightning != null)
        {
            return lightning;
        }
        else if (flicker != null)
        {
            return flicker;
        }

        return ITrailAspect.DEFAULT;
    }

    public static class Adapter extends JsonTypeDeserializer<JsonTrail>
    {
        @Override
        public JsonTrail deserialize(JsonReader in) throws IOException
        {
            JsonTrail trail = new JsonTrail();

            in.beginObject();
            String name = "";

            while (in.hasNext())
            {
                JsonToken next = in.peek();

                if (next == JsonToken.NAME)
                {
                    name = in.nextName();
                }
                else if (name.equals("parent") && next == JsonToken.STRING)
                {
                    trail.parent = in.nextString();
                }
                else if (name.equals("constants") && next == JsonToken.BEGIN_OBJECT)
                {
                    trail.constants.clear();
                    in.beginObject();

                    while (in.hasNext())
                    {
                        if (in.peek() == JsonToken.NAME)
                        {
                            String key = in.nextName();
                            Object constant = null;

                            switch (in.peek())
                            {
                            case BOOLEAN:
                                constant = in.nextBoolean();
                                break;
                            case NUMBER:
                                constant = in.nextDouble();
                                break;
                            case STRING:
                                constant = in.nextString();
                                break;
                            default:
                                in.skipValue();
                                break;
                            }

                            if (constant != null)
                            {
                                trail.constants.put(key, constant);
                            }

                            continue;
                        }

                        in.skipValue();
                    }

                    in.endObject();
                }
                else if (name.equals("lightning") && next == JsonToken.BEGIN_OBJECT)
                {
                    trail.lightning = new V9Wrapper((JsonTrailLightning) new JsonTrailLightning().read(in));
                }
                else if (name.equals("flicker") && next == JsonToken.BEGIN_OBJECT)
                {
                    trail.flicker = new V9Wrapper((JsonTrailFlicker) new JsonTrailFlicker().read(in));
                }
                else if (name.equals("particles") && next == JsonToken.BEGIN_OBJECT)
                {
                    trail.particles = (JsonTrailParticles) new JsonTrailParticles().read(in);
                }
                else if (name.equals("blur") && next == JsonToken.BEGIN_OBJECT)
                {
                    trail.blur = (JsonTrailBlur) new JsonTrailBlur().read(in);
                }
                else if (name.equals("fade") && next == JsonToken.NUMBER)
                {
                    trail.fade = in.nextInt();
                }
                else
                {
                    in.skipValue();
                }
            }

            in.endObject();

            return trail;
        }
    }
}
