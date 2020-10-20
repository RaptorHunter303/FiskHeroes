package com.fiskmods.heroes.client.render.hero;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.SHResourceHandler;
import com.fiskmods.heroes.client.json.hero.BodyPart;
import com.fiskmods.heroes.client.json.hero.JsonHeroRenderer;
import com.fiskmods.heroes.client.json.hero.SlotType;
import com.fiskmods.heroes.client.json.hero.TextureGetter;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffect;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.TextureHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class HeroRenderer
{
    public static final Map<String, HeroRenderer> REGISTRY = new HashMap<>();
    public static final HeroRenderer DEFAULT = new HeroRenderer();

    public static void register(String key, HeroRenderer value)
    {
        REGISTRY.put(key, value);
    }

    public static HeroRenderer get(String key)
    {
        return key != null ? REGISTRY.computeIfAbsent(key, k -> new HeroRenderer(SHResourceHandler.getDefaultRenderer())) : DEFAULT;
    }

    public static HeroRenderer get(HeroIteration key)
    {
        return key != null ? get(key.toString()) : DEFAULT;
    }

    protected final Map<String, String> variables = new HashMap<>();
    protected final Minecraft mc = Minecraft.getMinecraft();

    public final ModelBipedMultiLayer model;
    public JsonHeroRenderer json;

    public ResourceLocation[] defaultTex = new ResourceLocation[2];

    public HeroRenderer(JsonHeroRenderer jsonRenderer)
    {
        this();
        json = jsonRenderer;
    }

    public HeroRenderer()
    {
        this(new ModelBipedMultiLayer());
    }

    public HeroRenderer(ModelBipedMultiLayer modelBiped)
    {
        model = modelBiped;
    }

    public ResourceLocation getTexture(ItemStack itemstack, Entity entity, int slot)
    {
        if (json != null && json.getResourceKeys() != null)
        {
            if (entity instanceof EntityLivingBase)
            {
                evaluateVariables((EntityLivingBase) entity, slot);
            }

            if (json.texture != null)
            {
                return defaultTex[Pass.BASE] = json.getResource(entity, itemstack, json.texture.getResult(variables));
            }
        }

        return null;
    }

    public ResourceLocation getLightsTexture(Entity entity, int slot)
    {
        if (json != null && json.getResourceKeys() != null && json.lights != null)
        {
            return defaultTex[Pass.GLOW] = json.getResource(entity, slot, json.lights.getResult(variables));
        }

        return null;
    }

    public void resetTexture(int pass)
    {
        if (Pass.isTexturePass(pass) && defaultTex[pass] != null)
        {
            mc.getTextureManager().bindTexture(defaultTex[pass]);
        }
    }

    public String[] getItemIcons()
    {
        if (json != null && json.itemIcons != null)
        {
            return json.itemIcons;
        }

        return new String[4];
    }

    public void setupRenderLayers(ModelBipedMultiLayer model, int slot)
    {
        if (json != null && json.showModel != null)
        {
            for (BodyPart part : BodyPart.values())
            {
                part.getPart(model).showModel = json.showModel[part.ordinal()].contains(SlotType.values()[slot]);
            }
        }
    }

    public boolean fixHatLayer(EntityPlayer player, int slot)
    {
        return json != null && json.fixHatLayer != null && json.fixHatLayer.contains(SlotType.values()[slot]);
    }

    public boolean shouldRenderDefaultModel(EntityPlayer player, HeroIteration iter, boolean body)
    {
        if (json != null && json.effects != null)
        {
            SlotType slot = body ? SlotType.values()[model.armorSlot] : SlotType.CHESTPLATE;

            for (HeroEffect effect : json.effects.values())
            {
                if (!effect.provideContext(this).shouldRenderDefaultModel(model, player, iter, body) && effect.applicable.contains(slot))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean preRenderBody(Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float scale)
    {
        return getEffects(entity, SlotType.values()[model.armorSlot], pass).stream().allMatch(t -> t.preRenderBody(model, entity, pass, f, f1, f2, f3, f4, scale));
    }

    public boolean preRenderArm(EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        return getEffects(player, SlotType.CHESTPLATE, pass).stream().allMatch(t -> t.preRenderArm(model, player, itemstack, iter, pass));
    }

    public void postRenderBody(Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float scale)
    {
        getEffects(entity, SlotType.values()[model.armorSlot], pass).forEach(t -> t.postRenderBody(model, entity, pass, f, f1, f2, f3, f4, scale));
    }

    public void postRenderArm(EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        getEffects(player, SlotType.CHESTPLATE, pass).forEach(t -> t.postRenderArm(model, player, itemstack, iter, pass));
    }

    public Collection<HeroEffect> getEffects()
    {
        return json != null && json.effects != null ? json.effects.values() : Collections.EMPTY_SET;
    }

    public Collection<HeroEffect> getEffects(Entity entity, SlotType slot, int pass)
    {
        return json != null && json.effects != null ? json.effects.values().stream().filter(t -> t.provideContext(this).shouldRenderPass(model, entity, pass) && t.applicable.contains(slot)).collect(Collectors.toList()) : Collections.EMPTY_SET;
    }

    public boolean hasEffect(Class<? extends HeroEffect> type)
    {
        return json != null && json.effects != null && json.effects.values().stream().anyMatch(type::isInstance);
    }

    public <T extends HeroEffect> T getEffect(Class<T> type, Predicate<HeroEffect> p)
    {
        if (json != null && json.effects != null)
        {
            Stream<HeroEffect> s = json.effects.entrySet().stream().map(Entry::getValue).filter(type::isInstance);

            if (p != null)
            {
                s = s.filter(p);
            }

            return type.cast(s.findFirst().orElse(null));
        }

        return null;
    }

    public <T extends HeroEffect> T getEffect(Class<T> type, Entity entity)
    {
        return getEffect(type, t -> t.conditionals.evaluate(entity));
    }

    public <T extends HeroEffect> T getAnyEffect(Class<T> type)
    {
        return getEffect(type, (Predicate<HeroEffect>) null);
    }

    public void loadTextures(IResourceManager manager) throws IOException
    {
        json.load(manager, mc.getTextureManager());
    }

    public void evaluateVariables(EntityLivingBase entity, int slot)
    {
        variables.clear();

        for (String func : json.functions)
        {
            if (TextureGetter.FUNCTIONS.containsKey(func))
            {
                variables.put(func, TextureGetter.FUNCTIONS.get(func).apply(entity, slot));
            }
            else if (json.variables != null && func.startsWith("vars:"))
            {
                func = func.substring("vars:".length());

                if (json.variables.containsKey(func))
                {
                    ItemStack stack = entity.getEquipmentInSlot(4 - slot);
                    variables.put("vars:" + func, String.valueOf(json.variables.get(func).get(entity, stack)));
                }
            }
        }
    }

    public interface Pass
    {
        int BASE = 0;
        int GLOW = 1;
        int ENCHANTMENT = 2;
        int HURT = 3;

        public static boolean isTexturePass(int pass)
        {
            return pass == BASE || pass == GLOW;
        }

        public static int detectPass()
        {
            return TextureHelper.isBoundTexture(TextureHelper.RES_ITEM_GLINT) ? ENCHANTMENT : GL11.glGetBoolean(GL11.GL_TEXTURE_2D) ? BASE : HURT;
        }
    }
}
