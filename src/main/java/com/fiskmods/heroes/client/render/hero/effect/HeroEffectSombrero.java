package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.MultiTexture;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.model.ModelSombrero;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.common.data.SHData;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class HeroEffectSombrero extends HeroEffect
{
    protected static final ModelSombrero MODEL = new ModelSombrero();

    protected MultiTexture texture = MultiTexture.NULL;

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
            model.renderParts(entity, model.bipedHead, scale, anim ->
            {
                bindTexture(entity, model.armorSlot, texture, pass);

                GL11.glPushMatrix();
                model.bipedHead.postRender(scale);
                GL11.glTranslatef(0.0F, -scale * 6, 0.0F);

                float t = SHData.HAT_TIP.interpolate(entity);
                float f6 = MathHelper.sin((float) ((1 - t) * Math.PI));

                for (int i = 0; i < 5; ++i)
                {
                    f6 *= f6;
                }

                GL11.glRotatef(10 * f6, 1, 0, 0);
                MODEL.render(scale);
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
        else
        {
            in.skipValue();
        }
    }
}
