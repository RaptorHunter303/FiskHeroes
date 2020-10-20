package com.fiskmods.heroes.client.render.arrow;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelArrow;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectArchery;
import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.registry.FiskRegistryEntry;
import com.fiskmods.heroes.common.registry.FiskSimpleRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.ResourceLocation;

public class ArrowRenderer extends FiskRegistryEntry<ArrowRenderer>
{
    public static final FiskSimpleRegistry<ArrowRenderer> REGISTRY = new FiskSimpleRegistry(FiskHeroes.MODID, "normal");

    public static void register(String key, ArrowRenderer value)
    {
        REGISTRY.putObject(key, value);
    }

    public static void register(ArrowType key, ArrowRenderer value)
    {
        register(key.delegate.name(), value);
    }

    public static ArrowRenderer get(String key)
    {
        return REGISTRY.getObject(key);
    }

    public static ArrowRenderer get(ArrowType key)
    {
        return get(key.delegate.name());
    }

    protected Minecraft mc = Minecraft.getMinecraft();
    protected ItemRenderer itemRenderer = new ItemRenderer(mc);

    protected ModelArrow model = new ModelArrow();

    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        mc.getTextureManager().bindTexture(getTexture(arrow));
        model.render(0.0625F);
    }

    public void preRender(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
    }

    public ResourceLocation getTexture(EntityTrickArrow arrow)
    {
        HeroRenderer renderer = HeroRenderer.get(arrow.getHero());

        if (renderer != null)
        {
            HeroEffectArchery archery = renderer.getAnyEffect(HeroEffectArchery.class);

            if (archery != null)
            {
                return archery.getArrow(arrow.getShooter());
            }
        }

        return HeroEffectArchery.ARROW_TEXTURE;
    }
}
