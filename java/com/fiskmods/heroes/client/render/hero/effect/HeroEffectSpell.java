package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.json.hero.FloatData;
import com.fiskmods.heroes.client.json.shape.JsonShape;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class HeroEffectSpell extends HeroEffect
{
    protected float[] offset = new float[3];
    protected float[] rotation = new float[3];
    protected boolean mirror;
    protected float scale;
    protected int color;

    protected JsonShape shape;
    protected FloatData data = FloatData.NULL;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return pass == GLOW;
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
        if (shape != null && conditionals.evaluate(entity))
        {
            float mult = data.get(entity, itemstack, 0);

            if (mult > 0)
            {
                boolean flag = GL11.glGetBoolean(GL11.GL_CULL_FACE);
                float f = 0;

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
                SHRenderHelper.setGlColor(SHRenderHelper.getColorFromHex(color));

                if (mc.gameSettings.thirdPersonView == 0 && FiskHeroes.proxy.isClientPlayer(entity))
                {
                    f = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
                    GL11.glLineWidth(4);
                }

                model.renderParts(entity, model.bipedRightArm, scale, anim ->
                {
                    GL11.glPushMatrix();
                    model.bipedRightArm.postRender(scale);
                    GL11.glTranslatef(offset[0] * scale, (5 + offset[1]) * scale, offset[2] * scale);
                    GL11.glRotatef(rotation[0], 1, 0, 0);
                    GL11.glRotatef(rotation[1], 0, 1, 0);
                    GL11.glRotatef(rotation[2], 0, 0, 1);
                    renderSpell(model, entity, itemstack, pass, scale, mult, false);
                    GL11.glPopMatrix();
                });

                if (mirror && !hand)
                {
                    model.renderParts(entity, model.bipedLeftArm, scale, anim ->
                    {
                        GL11.glPushMatrix();
                        model.bipedLeftArm.postRender(scale);
                        GL11.glTranslatef(-offset[0] * scale, (5 + offset[1]) * scale, offset[2] * scale);
                        GL11.glRotatef(180, 0, 1, 0);
                        GL11.glRotatef(rotation[0], 1, 0, 0);
                        GL11.glRotatef(rotation[1], 0, 1, 0);
                        GL11.glRotatef(rotation[2], 0, 0, 1);
                        renderSpell(model, entity, itemstack, pass, scale, mult, true);
                        GL11.glPopMatrix();
                    });
                }

                GL11.glColor4f(1, 1, 1, 1);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glEnable(GL11.GL_TEXTURE_2D);

                if (!flag)
                {
                    GL11.glDisable(GL11.GL_CULL_FACE);
                }

                if (f > 0)
                {
                    GL11.glLineWidth(f);
                }
            }
        }
    }

    protected void renderSpell(ModelBipedMultiLayer model, Entity entity, ItemStack itemstack, int pass, float scale, float mult, boolean left)
    {
        shape.dataFormat.format.render(shape, model, entity, itemstack, pass, scale, FiskMath.curve(mult), entity.ticksExisted + FiskHeroes.proxy.getRenderTick(), this.scale, left);
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("data"))
        {
            data = FloatData.read(in);
        }
        else if (name.equals("shape") && next == JsonToken.STRING)
        {
            shape = JsonShape.read(JsonShape.GSON_FACTORY.create(), mc.getResourceManager(), new ResourceLocation(in.nextString()));
        }
        else if (name.equals("mirror") && next == JsonToken.BOOLEAN)
        {
            mirror = in.nextBoolean();
        }
        else if (name.equals("scale") && next == JsonToken.NUMBER)
        {
            scale = (float) in.nextDouble();
        }
        else if (name.equals("color"))
        {
            color = readInt(in, 0);
        }
        else if (name.equals("offset") && next == JsonToken.BEGIN_ARRAY)
        {
            offset = readArray(in, new float[3], f -> f);
        }
        else if (name.equals("rotation") && next == JsonToken.BEGIN_ARRAY)
        {
            rotation = readArray(in, new float[3], f -> f);
        }
        else
        {
            in.skipValue();
        }
    }
}
