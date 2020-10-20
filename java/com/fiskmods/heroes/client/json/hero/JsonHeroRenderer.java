package com.fiskmods.heroes.client.json.hero;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fiskmods.heroes.client.SHResourceHandler;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffect;
import com.fiskmods.heroes.common.JsonTypeDeserializer;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class JsonHeroRenderer
{
    public static final GsonBuilder GSON_FACTORY = new GsonBuilder().registerTypeAdapter(JsonHeroRenderer.class, new JsonHeroRenderer.Adapter());
    public static final Map<ResourceLocation, JsonHeroRenderer> CACHE = new HashMap<>();

    public static final String MODEL_DIR = "models/heroes/";
    public static final String TEXTURE_DIR = "textures/heroes/";

    public String parent;
    private Map<String, IResourceVar> resources;
    public Map<String, JsonHeroVar> variables;
    public Map<String, HeroEffect> effects;

    public Set<String> functions;
    public TextureGetter texture;
    public TextureGetter lights;

    public Set<SlotType>[] showModel;
    public Set<SlotType> fixHatLayer;
    public String[] itemIcons;

    public static JsonHeroRenderer read(Gson gson, IResourceManager manager, ResourceLocation location) throws IOException
    {
        return read(gson, manager, location, new LinkedList<>());
    }

    public static JsonHeroRenderer read(Gson gson, IResourceManager manager, Reader reader, ResourceLocation location) throws IOException
    {
        return read(gson, manager, reader, location, new LinkedList<>());
    }

    private static JsonHeroRenderer read(Gson gson, IResourceManager manager, ResourceLocation location, LinkedList<ResourceLocation> hierarchy) throws IOException
    {
        if (CACHE.containsKey(location))
        {
            return CACHE.get(location);
        }

        IResource iresource = manager.getResource(new ResourceLocation(location.getResourceDomain(), MODEL_DIR + location.getResourcePath() + ".json"));
        InputStreamReader reader = new InputStreamReader(iresource.getInputStream());

        return read(gson, manager, reader, location, hierarchy);
    }

    private static JsonHeroRenderer read(Gson gson, IResourceManager manager, Reader reader, ResourceLocation location, LinkedList<ResourceLocation> hierarchy) throws IOException
    {
        if (location != null && CACHE.containsKey(location))
        {
            return CACHE.get(location);
        }

        JsonHeroRenderer renderer = null;

        try
        {
            renderer = Preconditions.checkNotNull(gson.fromJson(reader, JsonHeroRenderer.class));
            hierarchy.add(location);
        }
        catch (Exception e)
        {
            throw new IOException(String.format("Failed to load Hero model '%s'", location), e);
        }

        if (renderer.parent != null)
        {
            ResourceLocation parentLocation = new ResourceLocation(renderer.parent);

            if (hierarchy.contains(parentLocation))
            {
                throw new IOException(String.format("Infinite inheritance loop! %s", hierarchy));
            }

            renderer.inherit(read(gson, manager, parentLocation, hierarchy));
        }

        renderer.init();

        if (location != null)
        {
            CACHE.put(location, renderer);
        }

        return renderer;
    }

    public void inherit(JsonHeroRenderer parent)
    {
        if (parent.resources != null)
        {
            if (resources == null)
            {
                resources = new HashMap<>(parent.resources);
            }
            else
            {
                Map<String, IResourceVar> map = resources;
                resources = new HashMap<>(parent.resources);
                resources.putAll(map);
            }
        }

        if (parent.variables != null)
        {
            if (variables == null)
            {
                variables = new HashMap<>(parent.variables);
            }
            else
            {
                Map<String, JsonHeroVar> map = variables;
                variables = new HashMap<>(parent.variables);
                variables.putAll(map);
            }
        }

        if (parent.effects != null)
        {
            if (effects == null)
            {
                effects = new HashMap<>(parent.effects);
            }
            else
            {
                Map<String, HeroEffect> map = effects;
                effects = new HashMap<>(parent.effects);
                effects.putAll(map);
            }
        }

        if (texture == null)
        {
            texture = parent.texture;
        }

        if (lights == null)
        {
            lights = parent.lights;
        }

        if (showModel == null)
        {
            showModel = parent.showModel;
        }

        if (fixHatLayer == null)
        {
            fixHatLayer = parent.fixHatLayer;
        }

        if (itemIcons == null)
        {
            itemIcons = parent.itemIcons;
        }
    }

    public void init()
    {
        functions = new HashSet<>();

        if (texture != null)
        {
            functions.addAll(texture.getFunctions(functions));
        }

        if (lights != null)
        {
            functions.addAll(lights.getFunctions(functions));
        }

        if (effects != null)
        {
            effects.values().forEach(t -> t.init(this));
        }
    }

    public void load(IResourceManager manager, TextureManager textureManager) throws IOException
    {
        if (resources != null)
        {
            Set<Map.Entry<String, IResourceVar>> txJsons = resources.entrySet().stream().filter(e -> e.getValue() instanceof ResourceVar && ((ResourceVar) e.getValue()).path.endsWith(".tx.json")).collect(Collectors.toSet());

            if (!txJsons.isEmpty())
            {
                Gson gson = ResourceVarTx.GSON_FACTORY.create();

                for (Map.Entry<String, IResourceVar> e : txJsons)
                {
                    String path = ((ResourceVar) e.getValue()).path;

                    try
                    {
                        ResourceLocation loc = new ResourceLocation(path);
                        IResource iresource = manager.getResource(new ResourceLocation(loc.getResourceDomain(), TEXTURE_DIR + loc.getResourcePath()));

                        resources.put(e.getKey(), gson.fromJson(new InputStreamReader(iresource.getInputStream()), ResourceVarTx.class));
                    }
                    catch (IOException e1)
                    {
                        throw new IOException(String.format("An error was caught while reading Hero texture stitcher '%s'", path), e1);
                    }
                }
            }

            for (IResourceVar resource : resources.values())
            {
                resource.load(textureManager);
                SHResourceHandler.listen(resource.getResources());
            }
        }

        if (effects != null)
        {
            for (HeroEffect effect : effects.values())
            {
                effect.load(this, manager, textureManager);
            }
        }
    }

    public ResourceLocation getResource(Entity entity, ItemStack stack, String key)
    {
        if (resources != null && resources.containsKey(key))
        {
            return resources.get(key).get(entity, stack);
        }

        return null;
    }

    public ResourceLocation getResource(Entity entity, int slot, String key)
    {
        ItemStack stack = null;

        if (entity instanceof EntityLivingBase)
        {
            stack = ((EntityLivingBase) entity).getEquipmentInSlot(4 - slot);
        }

        return getResource(entity, stack, key);
    }

    public Set<String> getResourceKeys()
    {
        if (resources != null)
        {
            return resources.keySet();
        }

        return null;
    }

    public static class Adapter extends JsonTypeDeserializer<JsonHeroRenderer>
    {
        @Override
        public JsonHeroRenderer deserialize(JsonReader in) throws IOException
        {
            JsonHeroRenderer renderer = new JsonHeroRenderer();

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
                    renderer.parent = in.nextString();
                }
                else if (name.equals("resources") && next == JsonToken.BEGIN_OBJECT)
                {
                    renderer.resources = new HashMap<>();
                    in.beginObject();

                    while (in.hasNext())
                    {
                        if (in.peek() == JsonToken.NAME)
                        {
                            String key = in.nextName();
                            ResourceVar resource = ResourceVar.read(in);

                            if (resource != null)
                            {
                                renderer.resources.put(key, resource);
                            }

                            continue;
                        }

                        in.skipValue();
                    }

                    in.endObject();
                }
                else if (name.equals("vars") && next == JsonToken.BEGIN_OBJECT)
                {
                    renderer.variables = new HashMap<>();
                    in.beginObject();

                    while (in.hasNext())
                    {
                        if (in.peek() == JsonToken.NAME)
                        {
                            String key = in.nextName();

                            if (in.peek() == JsonToken.STRING)
                            {
                                renderer.variables.put(key, new JsonHeroVar(in.nextString()));
                                continue;
                            }
                        }

                        in.skipValue();
                    }

                    in.endObject();
                }
                else if (name.equals("custom") && next == JsonToken.BEGIN_OBJECT)
                {
                    renderer.effects = new HashMap<>();
                    in.beginObject();

                    while (in.hasNext())
                    {
                        if (in.peek() == JsonToken.NAME)
                        {
                            String key = in.nextName();

                            if (in.peek() == JsonToken.BEGIN_OBJECT)
                            {
                                HeroEffect effect = HeroEffect.read(key, in);

                                if (effect != null)
                                {
                                    effect.json = renderer;
                                    renderer.effects.put(effect.id, effect);
                                }
                            }

                            continue;
                        }

                        in.skipValue();
                    }

                    in.endObject();
                }
                else if (name.equals("texture") && next == JsonToken.BEGIN_OBJECT)
                {
                    renderer.texture = TextureGetter.read(in);
                }
                else if (name.equals("lights") && next == JsonToken.BEGIN_OBJECT)
                {
                    renderer.lights = TextureGetter.read(in);
                }
                else if (name.equals("showModel") && next == JsonToken.BEGIN_OBJECT)
                {
                    renderer.showModel = new Set[BodyPart.values().length];
                    in.beginObject();

                    while (in.hasNext())
                    {
                        if (in.peek() == JsonToken.NAME)
                        {
                            BodyPart part = BodyPart.get(in.nextName());

                            if (part != null && in.peek() == JsonToken.BEGIN_ARRAY)
                            {
                                renderer.showModel[part.ordinal()] = readSlotTypes(in);
                            }

                            continue;
                        }

                        in.skipValue();
                    }

                    in.endObject();
                }
                else if (name.equals("fixHatLayer") && next == JsonToken.BEGIN_ARRAY)
                {
                    renderer.fixHatLayer = readSlotTypes(in);
                }
                else if (name.equals("itemIcons") && next == JsonToken.BEGIN_OBJECT)
                {
                    renderer.itemIcons = new String[SlotType.values().length];
                    in.beginObject();

                    while (in.hasNext())
                    {
                        if (in.peek() == JsonToken.NAME)
                        {
                            SlotType slot = null;
                            String s = in.nextName();

                            try
                            {
                                slot = SlotType.valueOf(s);
                            }
                            catch (Exception e)
                            {
                                throw new IOException("Invalid slot type: " + s);
                            }

                            if (slot != null && in.peek() == JsonToken.STRING)
                            {
                                renderer.itemIcons[slot.ordinal()] = in.nextString();
                            }

                            continue;
                        }

                        in.skipValue();
                    }

                    in.endObject();
                }
                else
                {
                    in.skipValue();
                }
            }

            in.endObject();

            return renderer;
        }

        public static Set<SlotType> readSlotTypes(JsonReader in) throws IOException
        {
            Set<SlotType> slots = new HashSet<>();
            in.beginArray();

            while (in.hasNext())
            {
                if (in.peek() == JsonToken.STRING)
                {
                    try
                    {
                        slots.add(SlotType.valueOf(in.nextString()));
                    }
                    catch (IllegalArgumentException e)
                    {
                    }

                    continue;
                }

                in.skipValue();
            }

            in.endArray();

            return slots;
        }
    }
}
