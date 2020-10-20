package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.JsonHeroRenderer;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.model.ShapeRenderer;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;

public class HeroEffectAntennae extends HeroEffect
{
    protected float angle = 0;
    protected float offset = 0;
    protected int segments = 8;

    protected ShapeRenderer[] antennae;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return pass != GLOW || defaultTex[pass] != null;
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float scale)
    {
        if (antennae != null && conditionals.evaluate(entity))
        {
            model.renderParts(entity, model.bipedHead, scale, anim ->
            {
                renderer.resetTexture(pass);

                GL11.glPushMatrix();
                model.bipedHead.postRender(scale);
                GL11.glTranslatef(-0.25F, -0.5F, -offset);
                antennae[0].render(scale);
                GL11.glPopMatrix();
            });
        }
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("angle") && next == JsonToken.NUMBER)
        {
            angle = (float) in.nextDouble();
        }
        else if (name.equals("offset") && next == JsonToken.NUMBER)
        {
            offset = (float) in.nextDouble();
        }
        else if (name.equals("segments") && next == JsonToken.NUMBER)
        {
            segments = (int) in.nextDouble();
        }
        else
        {
            in.skipValue();
        }
    }

    @Override
    public void init(JsonHeroRenderer json)
    {
        super.init(json);
        float a = (float) Math.toRadians(angle / segments);
        float f = 8F / segments;

        antennae = new ShapeRenderer[segments];

        for (int i = 0; i < segments; ++i)
        {
            int texX = 56;
            float texY = 8 - f * i;

            antennae[i] = new ShapeRenderer(64, 64);
            antennae[i].startBuildingQuads();
            antennae[i].addVertex(8, -f, 0, texX, texY - f);
            antennae[i].addVertex(8, 0, 0, texX, texY);
            antennae[i].addVertex(0, 0, 0, texX + 8, texY);
            antennae[i].addVertex(0, -f, 0, texX + 8, texY - f);
            antennae[i].build();

            antennae[i].rotateAngleX = a;

            if (i > 0)
            {
                antennae[i].rotationPointY = -f;
                antennae[i - 1].addChild(antennae[i]);
            }
        }
    }
}
