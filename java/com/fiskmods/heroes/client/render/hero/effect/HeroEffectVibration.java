package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;

public class HeroEffectVibration extends HeroEffect
{
    protected String texture = "missingno";

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return Pass.isTexturePass(pass);
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float f5)
    {
        if (entity.isEntityAlive())
        {
            if (defaultTex[pass] != null && conditionals.evaluate(entity))
            {
                int q = 2;
                int layers = 10 * q;
                float smear = 0.03F / q;
                float spread = 0.075F;
                float a = 1F / layers;
                float t = 18;

                if (pass == GLOW)
                {
                    smear /= 2;
                    layers *= 3;
                    a *= 1.2;
                }

                renderer.resetTexture(pass);
                GL11.glDepthMask(false);

                for (int i = 0; i < layers; ++i)
                {
                    float f6 = (float) Math.sin((f2 - i * smear) * t) * spread;
                    float f7 = (float) Math.cos((f2 - i * smear) * t * 2) * spread;

                    GL11.glPushMatrix();
                    GL11.glColor4f(1, 1, 1, a);
                    GL11.glTranslatef(pass == GLOW ? f6 * 1.5F : f6, 0, -f7 / 2);
                    model.renderBody(entity, pass, f, f1, f2, f3, f4, f5);
                    GL11.glPopMatrix();
                }

                GL11.glColor4f(1, 1, 1, 1);
                GL11.glDepthMask(true);

                // int q = 3;
                // int layers = 10 * q;
                // float smear = 0.0125F / q;
                // float spread = 0.05F * 1;
                // float t = 20;
                //
                // // float a = 1F / layers;
                // float da = 0.8F;
                // float a = da;
                //
                // if (pass == GLOW)
                // {
                // // smear /= 2;
                // // layers *= 2;
                // // a = da = 0.9F;
                // // a *= 1.2;
                // }
                //
                // resetTexture(pass);
                // GL11.glDepthMask(false);
                //
                // for (int i = 0; i < layers; ++i)
                // {
                // float f6 = (float) Math.sin((f2 - i * smear) * t) * spread;
                // float f7 = (float) Math.cos((f2 - i * smear) * t * 2) * spread;
                // float f8 = -(float) Math.sin((f2 - i * smear) * t) * spread;
                //
                // GL11.glPushMatrix();
                // GL11.glColor4f(1, 1, 1, a + 1F / layers / 2);
                // GL11.glTranslatef(pass == GLOW ? f6 * 1.5F : f6, 0, -f7 / 2);
                // model.renderBody(entity, pass, f, f1, f2, f3, f4, f5);
                // GL11.glPopMatrix();
                // // GL11.glPushMatrix();
                // // GL11.glTranslatef(pass == GLOW ? f8 * 1.5F : f8, 0, f7 / 2);
                // // model.renderBody(entity, pass, f, f1, f2, f3, f4, f5);
                // // GL11.glPopMatrix();
                // a *= da;
                // }
                //
                // GL11.glColor4f(1, 1, 1, 1);
                // GL11.glDepthMask(true);
            }
        }
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("texture") && next == JsonToken.STRING)
        {
            texture = in.nextString();
        }
        else
        {
            in.skipValue();
        }
    }
}
