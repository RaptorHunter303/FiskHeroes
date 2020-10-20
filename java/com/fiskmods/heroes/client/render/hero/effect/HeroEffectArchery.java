package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.model.item.ModelQuiver;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class HeroEffectArchery extends HeroEffect
{
    public static final ResourceLocation ARROW_TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/heroes/arrow/arrow.png");
    public static final ResourceLocation QUIVER_TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/heroes/quiver/quiver.png");
    public static final ResourceLocation AUTO_QUIVER_TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/heroes/quiver/auto.png");
    public static final ModelQuiver MODEL = new ModelQuiver();

    protected String arrow;
    protected String normal;
    protected String auto;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return false;
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("textureArrow") && next == JsonToken.STRING)
        {
            arrow = in.nextString();
        }
        else if (name.equals("textureQuiver") && next == JsonToken.STRING)
        {
            normal = in.nextString();
        }
        else if (name.equals("textureAutoQuiver") && next == JsonToken.STRING)
        {
            auto = in.nextString();
        }
        else
        {
            in.skipValue();
        }
    }

    public ResourceLocation getArrow(Entity entity)
    {
        if (arrow != null)
        {
            return renderer.json.getResource(entity, null, arrow); // TODO: This probably doesn't work with null
        }

        return ARROW_TEXTURE;
    }

    public ResourceLocation getQuiver(Entity entity, int metadata)
    {
        switch (metadata)
        {
        case 1:
            if (auto != null)
            {
                return renderer.json.getResource(entity, null, auto);
            }

            return AUTO_QUIVER_TEXTURE;
        default:
            if (normal != null)
            {
                return renderer.json.getResource(entity, null, normal);
            }

            return QUIVER_TEXTURE;
        }
    }
}
