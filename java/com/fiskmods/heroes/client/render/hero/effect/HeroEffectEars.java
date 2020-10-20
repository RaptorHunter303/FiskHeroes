package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.JsonHeroRenderer;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.model.ShapeRenderer;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;

public class HeroEffectEars extends HeroEffect
{
    protected float angle = 0;
    protected float inset = 0;

    protected ShapeRenderer[] rightEar;
    protected ShapeRenderer[] leftEar;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return pass != GLOW || defaultTex[pass] != null;
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float scale)
    {
        if (rightEar != null && leftEar != null && conditionals.evaluate(entity))
        {
            model.renderParts(entity, model.bipedHead, scale, anim ->
            {
                int glint = pass == ENCHANTMENT ? 1 : 0;
                renderer.resetTexture(pass);

                GL11.glPushMatrix();
                model.bipedHead.postRender(scale);
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.251F + inset, -0.5F, -0.25F);
                GL11.glRotatef(-angle, 0, 1, 0);
                rightEar[glint].render(scale);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef(0.251F - inset, -0.5F, -0.25F);
                GL11.glRotatef(angle, 0, 1, 0);
                leftEar[glint].render(scale);
                GL11.glPopMatrix();
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
        else if (name.equals("inset") && next == JsonToken.NUMBER)
        {
            inset = (float) in.nextDouble();
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
        rightEar = new ShapeRenderer[] {new ShapeRenderer(64, 64), new ShapeRenderer(64, 64)};
        leftEar = new ShapeRenderer[] {new ShapeRenderer(64, 64), new ShapeRenderer(64, 64)};

        for (int glint = 0; glint < 2; ++glint)
        {
            int texXR = 24;
            int texYR = 0;
            int texXL = 32;
            int texYL = 0;

            if (glint == 1)
            {
                texXR = 32;
                texYR = 8;
                texXL = 48;
                texYL = 8;
            }

            rightEar[glint].startBuildingQuads();
            rightEar[glint].addVertex(0, 0, 8, texXR, texYR);
            rightEar[glint].addVertex(0, 8, 8, texXR, texYR + 8);
            rightEar[glint].addVertex(0, 8, 0, texXR + 8, texYR + 8);
            rightEar[glint].addVertex(0, 0, 0, texXR + 8, texYR);
            rightEar[glint].build();
            leftEar[glint].startBuildingQuads();
            leftEar[glint].addVertex(0, 8, 0, texXL, texYL + 8);
            leftEar[glint].addVertex(0, 8, 8, texXL + 8, texYL + 8);
            leftEar[glint].addVertex(0, 0, 8, texXL + 8, texYL);
            leftEar[glint].addVertex(0, 0, 0, texXL, texYL);
            leftEar[glint].build();
        }
    }
}
