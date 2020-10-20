package com.fiskmods.heroes.client.json.hero;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.base.Objects;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.EntityLivingBase;

public abstract class TextureGetter
{
    public static final Map<String, BiFunction<EntityLivingBase, Integer, String>> FUNCTIONS = new HashMap<>();

    public static final String RENDER_LAYER = "renderLayer";
    public static final String HELD_ITEM = "heldItem";
    public static final String WORN_HELMET = "wornHelmet";
    public static final String WORN_CHESTPLATE = "wornChestplate";
    public static final String WORN_LEGGINGS = "wornLeggings";
    public static final String WORN_BOOTS = "wornBoots";

    public static void registerFunctions()
    {
        FUNCTIONS.put(RENDER_LAYER, (entity, slot) -> SlotType.values()[slot].name());
        FUNCTIONS.put(HELD_ITEM, (entity, slot) -> entity.getHeldItem() != null ? entity.getHeldItem().getItem().delegate.name() : "null");

        String[] equipment = {WORN_BOOTS, WORN_LEGGINGS, WORN_CHESTPLATE, WORN_HELMET};

        for (int i = 0; i < equipment.length; ++i)
        {
            final int j = i;

            FUNCTIONS.put(equipment[i], (entity, slot) -> HeroIteration.getName(SHHelper.getHeroIterFromArmor(entity, j)) + "");
        }
    }

    public abstract String getResult(Map<String, String> variables);

    public abstract Set<String> getFunctions(Set<String> functions);

    public static Wrapper read(JsonReader in) throws IOException
    {
        return read(in, new Wrapper());
    }

    private static Wrapper read(JsonReader in, Wrapper wrapper) throws IOException
    {
        in.beginObject();
        String name = "";

        while (in.hasNext())
        {
            JsonToken next = in.peek();

            switch (next)
            {
            case NAME:
                name = in.nextName();
                break;
            case STRING:
                wrapper.map.put(name, new Constant(in.nextString()));
                break;
            case BEGIN_OBJECT:
                wrapper.map.put(name, read(in, new SwitchCase(name)));
                break;
            default:
                in.skipValue();
                break;
            }
        }

        in.endObject();

        return wrapper;
    }

    private static class Wrapper extends TextureGetter
    {
        public final Map<String, TextureGetter> map = new LinkedHashMap<>();

        @Override
        public String getResult(Map<String, String> variables)
        {
            for (Map.Entry<String, TextureGetter> e : map.entrySet())
            {
                String key = e.getKey();
                TextureGetter value = e.getValue();

                if (value instanceof Constant)
                {
                    if (key.equals("default"))
                    {
                        return ((Constant) value).value;
                    }
                }
                else if (value instanceof SwitchCase)
                {
                    String result = ((SwitchCase) value).getResult(variables);

                    if (result == null || !result.isEmpty())
                    {
                        return result;
                    }
                }
            }

            return "";
        }

        @Override
        public Set<String> getFunctions(Set<String> functions)
        {
            for (Map.Entry<String, TextureGetter> e : map.entrySet())
            {
                if (FUNCTIONS.containsKey(e.getKey()) || e.getKey().startsWith("tags:") || e.getKey().startsWith("vars:"))
                {
                    functions.add(e.getKey());
                }

                e.getValue().getFunctions(functions);
            }

            return functions;
        }

        @Override
        public String toString()
        {
            return String.format("Wrapper%s", map.toString());
        }
    }

    private static class Constant extends TextureGetter
    {
        public final String value;

        public Constant(String v)
        {
            value = v;
        }

        @Override
        public String getResult(Map<String, String> variables)
        {
            return value;
        }

        @Override
        public Set<String> getFunctions(Set<String> functions)
        {
            return functions;
        }

        @Override
        public String toString()
        {
            return String.format("Constant(\"%s\")", value);
        }
    }

    private static class SwitchCase extends Wrapper
    {
        public final String function;

        public SwitchCase(String func)
        {
            function = FUNCTIONS.containsKey(func) || func.startsWith("tags:") || func.startsWith("vars:") ? func : null;
        }

        @Override
        public String getResult(Map<String, String> variables)
        {
            for (Map.Entry<String, TextureGetter> e : map.entrySet())
            {
                String key = e.getKey();
                TextureGetter value = e.getValue();

                if (function == null || key.equals("default") || variables.containsKey(function) && Objects.equal(key, variables.get(function)))
                {
                    if (value instanceof Constant)
                    {
                        return ((Constant) value).value;
                    }
                    else if (value instanceof SwitchCase)
                    {
                        String result = ((SwitchCase) value).getResult(variables);

                        if (result == null || !result.isEmpty())
                        {
                            return result;
                        }
                    }
                }
            }

            return "";
        }

        @Override
        public String toString()
        {
            return String.format("Switch(%s)%s", function, map.toString());
        }
    }
}
