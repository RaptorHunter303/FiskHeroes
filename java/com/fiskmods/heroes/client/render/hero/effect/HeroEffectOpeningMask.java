package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.FloatData;
import com.fiskmods.heroes.client.json.hero.MultiTexture;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class HeroEffectOpeningMask extends HeroEffect
{
    protected float[] translation;
    protected float[] rotation;

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
        render(model, entity, model.getArmorStack(entity), pass, scale);
    }

    @Override
    public void postRenderArm(ModelBipedMultiLayer model, EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        render(model, player, itemstack, pass, 0.0625F);
    }

    protected void render(ModelBipedMultiLayer model, Entity entity, ItemStack itemstack, int pass, float scale)
    {
        if (conditionals.evaluate(entity))
        {
            model.renderParts(entity, anim ->
            {
                ModelRenderer headwear = model.bipedHeadwear[pass == ENCHANTMENT ? 1 : 0];
                ModelRenderer head = model.bipedHead;

                float[] rot = {head.rotateAngleX, head.rotateAngleY, head.rotateAngleZ};
                float[] pos = {head.rotationPointX, head.rotationPointY, head.rotationPointZ};
                float mult = data.get(entity, itemstack, 0);
                float y = 1 - MathHelper.cos((float) (mult * Math.PI / 2));
                float x = MathHelper.sin((float) (mult * Math.PI / 2));
                float s = 1 + 0.002F * mult;

                GL11.glPushMatrix();
                head.postRender(scale);
                head.rotateAngleX = 0;
                head.rotateAngleY = 0;
                head.rotateAngleZ = 0;
                head.setRotationPoint(0, 0, 0);
                ModelBipedMultiLayer.sync(head, headwear);

                GL11.glTranslatef(x * translation[0] * scale, y * translation[1] * scale, x * translation[2] * scale);
                GL11.glRotatef(mult * rotation[2], 0, 0, 1);
                GL11.glRotatef(mult * rotation[1], 0, 1, 0);
                GL11.glRotatef(mult * rotation[0], 1, 0, 0);
                GL11.glScalef(s, s, s);
                bindTexture(entity, model.armorSlot, texture, pass);
                model.renderPart(head, scale, anim);
                model.renderPart(headwear, scale, anim);
                GL11.glPopMatrix();

                head.rotateAngleX = rot[0];
                head.rotateAngleY = rot[1];
                head.rotateAngleZ = rot[2];
                head.setRotationPoint(pos[0], pos[1], pos[2]);
                ModelBipedMultiLayer.sync(head, headwear);
            });
        }
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
        else if (next == JsonToken.BEGIN_ARRAY)
        {
            if (name.equals("translation"))
            {
                translation = readArray(in, new float[3], f -> f);
            }
            else if (name.equals("rotation"))
            {
                rotation = readArray(in, new float[3], f -> f);
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
