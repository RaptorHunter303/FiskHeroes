package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.fiskmods.heroes.client.json.hero.Conditionals;
import com.fiskmods.heroes.client.json.hero.JsonHeroRenderer;
import com.fiskmods.heroes.client.json.hero.MultiTexture;
import com.fiskmods.heroes.client.json.hero.SlotType;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.google.common.collect.HashBiMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class HeroEffect implements Pass
{
    private static final HashBiMap<String, Class<? extends HeroEffect>> REGISTRY = HashBiMap.create();

    public static void register(String key, Class<? extends HeroEffect> value)
    {
        REGISTRY.put(key, value);
    }

    public static Class<? extends HeroEffect> get(String key)
    {
        return REGISTRY.get(key);
    }

    public static String getKey(Class<? extends HeroEffect> value)
    {
        return REGISTRY.inverse().get(value);
    }

    protected final Minecraft mc = Minecraft.getMinecraft();

    public Conditionals conditionals = new Conditionals();
    public Set<SlotType> applicable;
    public String id;

    public HeroRenderer renderer;
    public JsonHeroRenderer json;
    public ResourceLocation[] defaultTex = new ResourceLocation[2];

    public HeroEffect provideContext(HeroRenderer heroRenderer)
    {
        defaultTex[0] = heroRenderer.defaultTex[0];
        defaultTex[1] = heroRenderer.defaultTex[1];
        renderer = heroRenderer;
        json = heroRenderer.json;

        heroRenderer.model.renderer = heroRenderer;
        return this;
    }

    public abstract boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass);

    public boolean shouldRenderDefaultModel(ModelBipedMultiLayer model, EntityPlayer player, HeroIteration iter, boolean body)
    {
        return true;
    }

    public boolean preRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float f5)
    {
        return true;
    }

    public boolean preRenderArm(ModelBipedMultiLayer model, EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        return true;
    }

    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float f5)
    {
    }

    public void postRenderArm(ModelBipedMultiLayer model, EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
    }

    public void onClientTick(EntityPlayer player, HeroIteration iter, Phase phase)
    {
    }

    public abstract void read(JsonReader in, String name, JsonToken next) throws IOException;

    public void init(JsonHeroRenderer json)
    {
        conditionals.init(json);
    }

    public void load(JsonHeroRenderer json, IResourceManager manager, TextureManager textureManager) throws IOException
    {
    }

    public void bindTexture(Entity entity, ItemStack stack, String key, int pass)
    {
        if (Pass.isTexturePass(pass))
        {
            if (key == null)
            {
                renderer.resetTexture(pass);
                return;
            }

            ResourceLocation location = json.getResource(entity, stack, key);

            if (location != null)
            {
                mc.getTextureManager().bindTexture(location);
            }
        }
    }

    public void bindTexture(Entity entity, int slot, String key, int pass)
    {
        if (entity instanceof EntityLivingBase)
        {
            bindTexture(entity, ((EntityLivingBase) entity).getEquipmentInSlot(4 - slot), key, pass);
        }
        else
        {
            bindTexture(entity, null, key, pass);
        }
    }

    public void bindTexture(Entity entity, ItemStack stack, MultiTexture key, int pass)
    {
        bindTexture(entity, stack, key.get(pass), pass);
    }

    public void bindTexture(Entity entity, int slot, MultiTexture key, int pass)
    {
        bindTexture(entity, slot, key.get(pass), pass);
    }

    public static int readInt(JsonReader in, int defValue) throws IOException
    {
        try
        {
            switch (in.peek())
            {
            case NUMBER:
                return in.nextInt();
            case STRING:
                return Integer.decode(in.nextString());
            default:
                in.skipValue();
                return defValue;
            }
        }
        catch (NumberFormatException e)
        {
            throw new IOException(e);
        }
    }

    public static float[] readArray(JsonReader in, float[] array, Function<Float, Float> func) throws IOException
    {
        int index = -1;
        in.beginArray();

        while (in.hasNext())
        {
            if (in.peek() == JsonToken.NUMBER)
            {
                if (index + 1 < array.length)
                {
                    array[++index] = func.apply((float) in.nextDouble());
                    continue;
                }
            }

            in.skipValue();
        }

        in.endArray();
        return array;
    }

    public static float[] readArray(JsonReader in, Function<Float, Float> func) throws IOException
    {
        List<Float> list = new ArrayList<>();
        in.beginArray();

        while (in.hasNext())
        {
            if (in.peek() == JsonToken.NUMBER)
            {
                list.add(func.apply((float) in.nextDouble()));
                continue;
            }

            in.skipValue();
        }

        in.endArray();
        float[] array = new float[list.size()];

        for (int i = 0; i < array.length; ++i)
        {
            array[i] = list.get(i);
        }

        return array;
    }

    public static HeroEffect read(String key, JsonReader in) throws IOException
    {
        String id = key;

        if (key.contains("|"))
        {
            String[] astring = key.split("\\|");
            key = astring[0];
            id = astring[1];
        }

        Class<? extends HeroEffect> type = get(key);

        if (type != null)
        {
            HeroEffect effect = null;

            try
            {
                effect = type.newInstance();
            }
            catch (Exception e)
            {
                throw new IOException(e);
            }

            effect.id = id;

            in.beginObject();
            String name = "";

            while (in.hasNext())
            {
                if (in.peek() == JsonToken.NAME)
                {
                    name = in.nextName();

                    if (name.equals("applicable") && in.peek() == JsonToken.BEGIN_ARRAY)
                    {
                        effect.applicable = JsonHeroRenderer.Adapter.readSlotTypes(in);
                    }
                    else if (name.equals("effectId") && in.peek() == JsonToken.STRING)
                    {
                        effect.id = in.nextString();
                    }
                    else if (name.equals("conditionals"))
                    {
                        effect.conditionals.read(in);
                    }
                    else
                    {
                        effect.read(in, name, in.peek());
                    }
                }
            }

            if (effect.applicable == null)
            {
                effect.applicable = new HashSet<>();
            }

            in.endObject();
            return effect;
        }

        in.skipValue();
        return null;
    }
}
