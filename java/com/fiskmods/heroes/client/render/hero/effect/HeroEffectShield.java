package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.FloatData;
import com.fiskmods.heroes.client.json.hero.MultiTexture;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.model.item.ModelRetractableShield;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HeroEffectShield extends HeroEffect
{
    protected static final ModelRetractableShield MODEL = new ModelRetractableShield(2, 6, 2);
    protected static final ModelRetractableShield MODEL_LARGE = new ModelRetractableShield(3, 11, 6);

    protected float[] offset = new float[3];
    protected float[] rotation = new float[3];
    protected float[] curve = new float[2];
    protected boolean mirror;
    protected boolean large;

    protected MultiTexture texture = MultiTexture.NULL;
    protected FloatData data = FloatData.NULL;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return texture.hasTexture(pass) || !Pass.isTexturePass(pass);
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float scale)
    {
        render(model, entity, model.getArmorStack(entity), pass, scale, false);
    }

    @Override
    public void postRenderArm(ModelBipedMultiLayer model, EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        render(model, player, itemstack, pass, 0.0625F, true);
    }

    protected void render(ModelBipedMultiLayer model, Entity entity, ItemStack itemstack, int pass, float scale, boolean hand)
    {
        if (conditionals.evaluate(entity))
        {
            float mult = data.get(entity, itemstack, 0);
            float f = Math.min(mult * 6, 1);

            if (f > 0)
            {
                boolean flag = GL11.glGetBoolean(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_CULL_FACE);
                bindTexture(entity, model.armorSlot, texture, pass);
                model.renderParts(entity, model.bipedRightArm, scale, anim ->
                {
                    GL11.glPushMatrix();
                    model.bipedRightArm.postRender(scale);
                    GL11.glTranslatef(-FiskServerUtils.interpolate(2.9F, offset[0], f) * scale, FiskServerUtils.interpolate(5, offset[1], f) * scale, offset[2] * f * scale);
                    GL11.glRotatef(rotation[0] * f, 1, 0, 0);
                    GL11.glRotatef(rotation[1] * f, 0, 1, 0);
                    GL11.glRotatef(rotation[2] * f, 0, 0, 1);
                    getModel().render(mult, curve[0], curve[1], scale);
                    GL11.glPopMatrix();
                });

                if (mirror && !hand) // TODO: Check mirrored version for rotation discrepancies
                {
                    model.renderParts(entity, model.bipedLeftArm, scale, anim ->
                    {
                        GL11.glPushMatrix();
                        model.bipedLeftArm.postRender(scale);
                        GL11.glTranslatef(-2.9F * (1 - f) * scale, 0, 0);
                        GL11.glRotatef(180, 0, 1, 0);
                        GL11.glTranslatef(-offset[0] * f * scale, FiskServerUtils.interpolate(5, offset[1], f) * scale, offset[2] * f * scale);
                        GL11.glRotatef(rotation[0] * f, 1, 0, 0);
                        GL11.glRotatef(rotation[1] * f, 0, 1, 0);
                        GL11.glRotatef(rotation[2] * f, 0, 0, 1);
                        getModel().render(mult, curve[0], curve[1], scale);
                        GL11.glPopMatrix();
                    });
                }

                if (!flag)
                {
                    GL11.glDisable(GL11.GL_CULL_FACE);
                }
            }
        }
    }

    protected ModelRetractableShield getModel()
    {
        return large ? MODEL_LARGE : MODEL;
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("texture"))
        {
            texture = MultiTexture.read(in);
        }
        else if (name.equals("data"))
        {
            data = FloatData.read(in);
        }
        else if (name.equals("mirror") && next == JsonToken.BOOLEAN)
        {
            mirror = in.nextBoolean();
        }
        else if (name.equals("large") && next == JsonToken.BOOLEAN)
        {
            large = in.nextBoolean();
        }
        else if (next == JsonToken.BEGIN_ARRAY)
        {
            if (name.equals("offset"))
            {
                offset = readArray(in, new float[3], f -> f);
            }
            else if (name.equals("rotation"))
            {
                rotation = readArray(in, new float[3], f -> f);
            }
            else if (name.equals("curve"))
            {
                curve = readArray(in, new float[2], f -> (float) Math.toRadians(f));
            }
            else
            {
                in.skipValue();
            }
        }
        else
        {
            in.skipValue();
        }
    }
}
