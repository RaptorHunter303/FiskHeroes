package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.FloatData;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HeroEffectMetalHeat extends HeroEffect
{
    protected String texture = "missingno";
    protected FloatData data = FloatData.NULL;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return pass == GLOW;
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
                bindTexture(entity, model.armorSlot, texture, BASE);
                GL11.glDepthMask(false);
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
                GL11.glColor4f(1, 0.75F, 0.5F, mult);
                runnable.run();
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glDepthMask(true);
            }
        }
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("texture") && next == JsonToken.STRING)
        {
            texture = in.nextString();

            if ("null".equals(texture))
            {
                texture = null;
            }
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
