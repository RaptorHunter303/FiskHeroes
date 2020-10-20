package com.fiskmods.heroes.client.json.hero;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.fiskmods.heroes.client.SHResourceHandler;
import com.fiskmods.heroes.common.JsonTypeDeserializer;
import com.fiskmods.heroes.util.TextureHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class ResourceVarTx implements IResourceVar
{
    public static final GsonBuilder GSON_FACTORY = new GsonBuilder().registerTypeAdapter(ResourceVarTx.class, new ResourceVarTx.Adapter());

    protected final Set<ResourceLocation> resources = new HashSet<>();

    private final ImmutableMap<String, Getter> getters;
    private final Map<String, Integer> index = new HashMap<>();

    private final TexturePath path;
    private final boolean isDynamic;

    public ResourceVarTx(Function<ResourceVarTx, TexturePath> texture, Map<String, Getter> getters, boolean dynamic)
    {
        this.getters = ImmutableMap.copyOf(getters);
        int i = 0;

        for (Getter getter : getters.values())
        {
            index.put(getter.name, ++i);
        }

        path = texture.apply(this);
        isDynamic = dynamic;
    }

    @Override
    public void load(TextureManager manager)
    {
        for (Map<String, String> perm : generatePermutations())
        {
            path.load(manager, this, perm);
        }
    }

    void load(TextureManager manager, ResourceLocation loc)
    {
        manager.bindTexture(loc);
        resources.add(loc);
    }

    @Override
    public ResourceLocation get(Entity entity, ItemStack stack)
    {
        if (entity != null)
        {
            if (isDynamic)
            {
                return compute(entity, stack);
            }

            return SHResourceHandler.compute(this, entity, stack);
        }

        return TextureHelper.MISSING_TEXTURE;
    }

    @Override
    public ImmutableSet<ResourceLocation> getResources()
    {
        return ImmutableSet.copyOf(resources);
    }

    public ResourceLocation compute(Entity entity, ItemStack stack)
    {
        Object[] args = new Object[getters.size()];

        for (Getter getter : getters.values())
        {
            int i = indexOf(getter.name) - 1;
            args[MathHelper.clamp_int(i, 0, args.length - 1)] = getter.get(entity, stack);
        }

        return new ResourceLocation(path.formatPath(args));
    }

    public List<Map<String, String>> generatePermutations()
    {
        return generatePermutations(new LinkedList<>(getters.values()), new LinkedHashMap<>(), new LinkedList<>());
    }

    private List<Map<String, String>> generatePermutations(LinkedList<Getter> getters, Map<String, String> map, List<Map<String, String>> permutations)
    {
        if (!getters.isEmpty())
        {
            Getter getter = getters.poll();

            for (String s : getter.values)
            {
                map.put(getter.name, s);
                generatePermutations(new LinkedList<>(getters), map, permutations);
            }
        }
        else
        {
            permutations.add(ImmutableMap.copyOf(map));
        }

        return permutations;
    }

    public int indexOf(String name)
    {
        return index.getOrDefault(name, -1);
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        ResourceVarTx other = (ResourceVarTx) obj;
        return Objects.equals(path, other.path) && Objects.equals(getters, other.getters);
    }

    @Override
    public String toString()
    {
        return String.format("ResourceTx[\"%s\", getters=%s]", path, getters);
    }

    public static ResourceVarTx read(JsonReader in) throws IOException
    {
        String name = "";
        in.beginObject();

        Function<ResourceVarTx, TexturePath> texture = null;
        Map<String, Getter> getters = null;
        boolean dynamic = false;

        while (in.hasNext())
        {
            JsonToken next = in.peek();

            if (next == JsonToken.NAME)
            {
                name = in.nextName();
            }
            else if (name.equals("texture"))
            {
                if (next == JsonToken.STRING)
                {
                    String s = in.nextString();

                    if (!StringUtils.isNullOrEmpty(s))
                    {
                        texture = tx -> new TexturePath(tx, s);
                        continue;
                    }
                }
                else if (next == JsonToken.BEGIN_OBJECT)
                {
                    texture = TexturePathModifier.read(in);
                    continue;
                }

                in.skipValue();
            }
            else if (name.equals("getters") && next == JsonToken.BEGIN_OBJECT)
            {
                getters = new LinkedHashMap<>();
                in.beginObject();

                while (in.hasNext())
                {
                    if (in.peek() == JsonToken.NAME)
                    {
                        String getterName = in.nextName();

                        if (!StringUtils.isNullOrEmpty(getterName) && in.peek() == JsonToken.BEGIN_OBJECT)
                        {
                            String key = null;
                            List<String> values = null;
                            in.beginObject();

                            while (in.hasNext())
                            {
                                if (in.peek() == JsonToken.NAME)
                                {
                                    name = in.nextName();
                                    next = in.peek();

                                    if (name.equals("key") && next == JsonToken.STRING)
                                    {
                                        key = in.nextString();
                                        continue;
                                    }
                                    else if (name.equals("values"))
                                    {
                                        if (next == JsonToken.BEGIN_ARRAY)
                                        {
                                            values = new LinkedList<>();
                                            in.beginArray();

                                            while (in.hasNext())
                                            {
                                                values.add(in.nextString());
                                            }

                                            in.endArray();
                                            continue;
                                        }
                                        else if (next == JsonToken.BEGIN_OBJECT)
                                        {
                                            int min = 0, max = 0;
                                            in.beginObject();

                                            while (in.hasNext())
                                            {
                                                if (in.peek() == JsonToken.NAME)
                                                {
                                                    name = in.nextName();

                                                    if (in.peek() == JsonToken.NUMBER)
                                                    {
                                                        if (name.equals("min"))
                                                        {
                                                            min = (int) in.nextDouble();
                                                            continue;
                                                        }
                                                        else if (name.equals("max"))
                                                        {
                                                            max = (int) in.nextDouble();
                                                            continue;
                                                        }
                                                    }
                                                }

                                                in.skipValue();
                                            }

                                            values = new LinkedList<>();

                                            for (int i = min; i <= max; ++i)
                                            {
                                                values.add(String.valueOf(i));
                                            }

                                            in.endObject();
                                            continue;
                                        }
                                    }
                                }

                                in.skipValue();
                            }

                            if (!StringUtils.isNullOrEmpty(key) && values != null && !values.isEmpty())
                            {
                                getters.put(getterName, new Getter(getterName, key, values));
                            }

                            in.endObject();
                            continue;
                        }
                    }

                    in.skipValue();
                }

                in.endObject();
            }
            else if (name.equals("isDynamic") && next == JsonToken.BOOLEAN)
            {
                dynamic = in.nextBoolean();
            }
            else
            {
                in.skipValue();
            }
        }

        in.endObject();

        if (texture != null && getters != null && !getters.isEmpty())
        {
            return new ResourceVarTx(texture, getters, dynamic);
        }

        return null;
    }

    private static class Getter
    {
        private final String key;
        private final String name;

        private final ImmutableList<String> values;
        private BiFunction<Entity, ItemStack, Object> interpreter;

        public Getter(String name, String key, List<String> values)
        {
            this.key = key;
            this.name = name;
            this.values = ImmutableList.copyOf(values);
        }

        public Object get(Entity entity, ItemStack stack)
        {
            if (interpreter == null)
            {
                interpreter = JsonHeroVar.createInterpreter(key, false);
            }

            return interpreter.apply(entity, stack);
        }

        @Override
        public String toString()
        {
            return String.format("{key=\"%s\", values=%s}", key, values);
        }
    }

    public static class Adapter extends JsonTypeDeserializer<ResourceVarTx>
    {
        @Override
        public ResourceVarTx deserialize(JsonReader in) throws IOException
        {
            return ResourceVarTx.read(in);
        }
    }
}
