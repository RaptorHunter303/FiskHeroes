package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.FloatData;
import com.fiskmods.heroes.client.json.hero.MultiTexture;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.model.ModelFalconWings;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.FiskMath;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HeroEffectWings extends HeroEffect
{
    protected static final ModelFalconWings MODEL = new ModelFalconWings();

    protected MultiTexture texture = MultiTexture.NULL;
    protected FloatData dataWings = FloatData.NULL;
    protected FloatData dataShield = FloatData.NULL;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return texture.hasTexture(pass) || !Pass.isTexturePass(pass);
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float scale)
    {
        if (conditionals.evaluate(entity))
        {
            model.renderParts(entity, model.bipedBody, scale, anim ->
            {
                ItemStack stack = model.getArmorStack(entity);
                float wings = dataWings.get(entity, stack, 0);
                float shield = dataShield.get(entity, stack, 0);
                float s = 1 + wings * 0.5F;

                bindTexture(entity, model.armorSlot, texture, pass);
                MODEL.setAngles(entity, wings, shield);
                GL11.glPushMatrix();
                model.bipedBody.postRender(scale);
                MODEL.render(1, s, scale);
                MODEL.render(0, Math.max(s, 1 + shield * 0.5F), scale);
                GL11.glPopMatrix();
            });
        }
    }

    @Override
    public void postRenderArm(ModelBipedMultiLayer model, EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        if (conditionals.evaluate(player))
        {
            model.renderParts(player, model.bipedRightArm, 0.0625F, anim ->
            {
                float shield = dataShield.get(player, itemstack, 0);
                MODEL.resetAngles();
                MODEL.shapes[0][0][0].rotationPointX += 12;
                MODEL.shapes[0][0][0].rotationPointY -= 3;
                MODEL.shapes[0][0][0].rotationPointZ += 2;

                if (shield > 0)
                {
                    shield = 1 - FiskMath.curve(shield);

                    for (int i = 0; i < MODEL.shapes[0].length; ++i)
                    {
                        for (int j = 0; j < 2; ++j)
                        {
                            MODEL.shapes[0][i][j].rotateAngleX *= shield;
                            MODEL.shapes[0][i][j].rotateAngleY *= shield;
                            MODEL.shapes[0][i][j].rotateAngleZ *= shield;
                        }

                        MODEL.shapes[0][i][1].rotationPointY += 4 * (1 - shield);
                    }

                    shield = 1 - shield;
                    MODEL.shapes[0][3][0].rotateAngleY -= 0.1F * shield;
                    MODEL.shapes[0][4][0].rotateAngleY -= 0.3F * shield;
                    MODEL.shapes[0][5][0].rotateAngleY -= 0.2F * shield;
                    MODEL.shapes[0][6][0].rotateAngleY -= 0.1F * shield;
                    MODEL.shapes[0][7][0].rotateAngleY -= 0.1F * shield;
                }

                bindTexture(player, model.armorSlot, texture, pass);
                GL11.glPushMatrix();
                model.bipedRightArm.postRender(0.0625F);
                GL11.glRotatef(-90, 0, 0, 1);
                GL11.glRotatef(90, 1, 0, 0);
                MODEL.render(0, 1 + shield * 0.5F, 0.0625F);
                GL11.glPopMatrix();
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
        else if (name.equals("dataWings") || name.equals("data"))
        {
            dataWings = FloatData.read(in);
        }
        else if (name.equals("dataShield"))
        {
            dataShield = FloatData.read(in);
        }
        else
        {
            in.skipValue();
        }
    }
}
