package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.FloatData;
import com.fiskmods.heroes.client.json.hero.MultiTexture;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HeroEffectOverlay extends HeroEffect
{
    protected MultiTexture texture = MultiTexture.NULL;
    protected float opacity = 1;

    protected FloatData data = FloatData.NULL;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return texture.hasTexture(pass) || pass == ENCHANTMENT;
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float scale)
    {
        render(model, entity, model.getArmorStack(entity), pass, scale, () -> model.renderBody(entity, pass, f, f1, f2, f3, f4, scale));
    }

    @Override
    public void postRenderArm(ModelBipedMultiLayer model, EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        render(model, player, itemstack, pass, 0.0625F, () -> model.renderArm(player, itemstack, iter, pass, 0.0625F));
    }

    protected void render(ModelBipedMultiLayer model, Entity entity, ItemStack itemstack, int pass, float scale, Runnable runnable)
    {
        if (conditionals.evaluate(entity))
        {
            float mult = data.get(entity, itemstack, 1);

            if (mult > 0)
            {
                bindTexture(entity, model.armorSlot, texture, pass);
                GL11.glColor4f(1, 1, 1, mult * opacity);
                runnable.run();
                GL11.glColor4f(1, 1, 1, 1);
            }
        }
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("texture"))
        {
            texture = MultiTexture.read(in);
        }
        else if (name.equals("opacity") && next == JsonToken.NUMBER)
        {
            opacity = (float) in.nextDouble();
        }
        else if (name.equals("data"))
        {
            data = FloatData.read(in);
        }
        else
        {
            in.skipValue();
        }
    }
}
