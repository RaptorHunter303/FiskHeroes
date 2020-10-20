package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.FloatData;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HeroEffectInvisibility extends HeroEffect
{
    protected float opacityMin = 0;
    protected float opacityMax = 1;

    protected FloatData data = FloatData.NULL;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return true;
    }

    @Override
    public boolean shouldRenderDefaultModel(ModelBipedMultiLayer model, EntityPlayer player, HeroIteration iter, boolean body)
    {
        return !conditionals.evaluate(player);
    }

    @Override
    public boolean preRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float f5)
    {
        return preRender(model, entity, model.getArmorStack(entity), pass);
    }

    @Override
    public boolean preRenderArm(ModelBipedMultiLayer model, EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        return preRender(model, player, itemstack, pass);
    }

    public boolean preRender(ModelBipedMultiLayer model, Entity entity, ItemStack itemstack, int pass)
    {
        if (conditionals.evaluate(entity))
        {
            float delta = data.get(entity, itemstack, 1);
            float mult = FiskServerUtils.interpolate(opacityMax, opacityMin, delta);

            if (mult < 1)
            {
                if (pass != Pass.ENCHANTMENT && pass != Pass.HURT && delta > 0.5F)
                {
                    GL11.glDepthMask(false);
                }

                SHRenderHelper.setAlpha(SHRenderHelper.getAlpha() * mult);
            }
        }

        return true;
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("opacityMin") && next == JsonToken.NUMBER)
        {
            opacityMin = (float) in.nextDouble();
        }
        else if (name.equals("opacityMax") && next == JsonToken.NUMBER)
        {
            opacityMax = (float) in.nextDouble();
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
