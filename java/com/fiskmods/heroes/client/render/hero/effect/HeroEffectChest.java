package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.JsonHeroRenderer;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.model.ShapeRenderer;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;

public class HeroEffectChest extends HeroEffect
{
    protected float extrude = 0;
    protected float yOffset = 0;

    protected ShapeRenderer[] base;
    protected ShapeRenderer[] overlay;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return pass != GLOW || defaultTex[GLOW] != null;
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float scale)
    {
        if (base != null && overlay != null && conditionals.evaluate(entity))
        {
            model.renderParts(entity, model.bipedBody, scale, anim ->
            {
                int glint = pass == ENCHANTMENT ? 1 : 0;
                float s = 1.1F;

                renderer.resetTexture(pass);
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_CULL_FACE);
                model.bipedBody.postRender(scale);
                base[glint].render(scale);
                GL11.glTranslatef(model.bipedBody.offsetX, model.bipedBody.offsetY, model.bipedBody.offsetZ);
                GL11.glTranslatef(model.bipedBody.rotationPointX * scale, model.bipedBody.rotationPointY * scale, model.bipedBody.rotationPointZ * scale);
                GL11.glScalef(s, s, s);
                GL11.glTranslatef(-model.bipedBody.offsetX, -model.bipedBody.offsetY, -model.bipedBody.offsetZ);
                GL11.glTranslatef(-model.bipedBody.rotationPointX * scale, -model.bipedBody.rotationPointY * scale, -model.bipedBody.rotationPointZ * scale);
                GL11.glTranslatef(0, -0.5F * scale, 0);
                overlay[glint].render(scale);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glPopMatrix();
            });
        }
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("extrude") && next == JsonToken.NUMBER)
        {
            extrude = (float) in.nextDouble();
        }
        else if (name.equals("offset") && next == JsonToken.NUMBER)
        {
            yOffset = (float) in.nextDouble();
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

        if (extrude != 0)
        {
            base = new ShapeRenderer[] {new ShapeRenderer(64, 64), new ShapeRenderer(64, 64)};
            overlay = new ShapeRenderer[] {new ShapeRenderer(64, 64), new ShapeRenderer(64, 64)};

            for (int glint = 0; glint < 2; ++glint)
            {
                float x = 0;
                float y = yOffset;
                float defTexWidth = 6;
                float defTexX = 20 + (8 - defTexWidth) / 2;
                float defTexY = y + (glint == 1 ? 4 : 20);
                float texX = defTexX;
                float texY = defTexY;
                float texWidth = defTexWidth;
                float texHeight = 2;

                for (int layer = 0; layer < 2; ++layer)
                {
                    ShapeRenderer renderer = new ShapeRenderer[] {base[glint], overlay[glint]}[layer];
                    texY = defTexY;
                    texX = defTexX;
                    texWidth = defTexWidth;
                    y = yOffset;

                    if (layer == 1)
                    {
                        texY += 16;
                    }

                    renderer.startBuildingQuads();
                    renderer.addVertex(texWidth / 2, y, -2, texWidth + texX, texY);
                    renderer.addVertex(-texWidth / 2, y, -2, texX, texY);
                    renderer.addVertex(-texWidth / 2, texHeight + y, -2 - extrude, texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2, texHeight + y, -2 - extrude, texWidth + texX, texHeight + texY);
                    renderer.build();
                    texWidth = 4 - defTexWidth / 2;
                    x = -(texWidth + defTexWidth) / 2;
                    texX = 20;
                    renderer.startBuildingQuads();
                    renderer.addVertex(texWidth / 2 + x, y, -2, texWidth + texX, texY);
                    renderer.addVertex(-texWidth / 2 + x, y, -2, texX, texY);
                    renderer.addVertex(-texWidth / 2 + x, texHeight + y, -2, texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2 + x, texHeight + y, -2 - extrude, texWidth + texX, texHeight + texY);
                    renderer.build();
                    texX = defTexX;
                    texWidth = defTexWidth;
                    texWidth = 4 - defTexWidth / 2;
                    x = (texWidth + defTexWidth) / 2;
                    texX = 28 - (8 - defTexWidth) / 2;
                    renderer.startBuildingQuads();
                    renderer.addVertex(-texWidth / 2 + x, y, -2, texX, texY);
                    renderer.addVertex(-texWidth / 2 + x, texHeight + y, -2 - extrude, texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2 + x, texHeight + y, -2, texWidth + texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2 + x, y, -2, texWidth + texX, texY);
                    renderer.build();
                    texX = defTexX;
                    texWidth = defTexWidth;
                    y += 2;
                    texY += 2;
                    renderer.startBuildingQuads();
                    renderer.addVertex(texWidth / 2, y, -2 - extrude, texWidth + texX, texY);
                    renderer.addVertex(-texWidth / 2, y, -2 - extrude, texX, texY);
                    renderer.addVertex(-texWidth / 2, texHeight + y, -2 - extrude, texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2, texHeight + y, -2 - extrude, texWidth + texX, texHeight + texY);
                    renderer.build();
                    texWidth = 4 - defTexWidth / 2;
                    x = -(texWidth + defTexWidth) / 2;
                    texX = 20;
                    renderer.startBuildingQuads();
                    renderer.addVertex(texWidth / 2 + x, y, -2 - extrude, texWidth + texX, texY);
                    renderer.addVertex(-texWidth / 2 + x, y, -2, texX, texY);
                    renderer.addVertex(-texWidth / 2 + x, texHeight + y, -2, texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2 + x, texHeight + y, -2 - extrude, texWidth + texX, texHeight + texY);
                    renderer.build();
                    texX = defTexX;
                    texWidth = defTexWidth;
                    texWidth = 4 - defTexWidth / 2;
                    x = (texWidth + defTexWidth) / 2;
                    texX = 28 - (8 - defTexWidth) / 2;
                    renderer.startBuildingQuads();
                    renderer.addVertex(-texWidth / 2 + x, y, -2 - extrude, texX, texY);
                    renderer.addVertex(-texWidth / 2 + x, texHeight + y, -2 - extrude, texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2 + x, texHeight + y, -2, texWidth + texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2 + x, y, -2, texWidth + texX, texY);
                    renderer.build();
                    texX = defTexX;
                    texWidth = defTexWidth;
                    y += 2;
                    texY += 2;
                    renderer.startBuildingQuads();
                    renderer.addVertex(texWidth / 2, y, -2 - extrude, texWidth + texX, texY);
                    renderer.addVertex(-texWidth / 2, y, -2 - extrude, texX, texY);
                    renderer.addVertex(-texWidth / 2, texHeight + y, -2, texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2, texHeight + y, -2, texWidth + texX, texHeight + texY);
                    renderer.build();
                    texWidth = 4 - defTexWidth / 2;
                    x = -(texWidth + defTexWidth) / 2;
                    texX = 20;
                    renderer.startBuildingQuads();
                    renderer.addVertex(-texWidth / 2 + x, y, -2, texX, texY);
                    renderer.addVertex(-texWidth / 2 + x, texHeight + y, -2, texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2 + x, texHeight + y, -2, texWidth + texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2 + x, y, -2 - extrude, texWidth + texX, texY);
                    renderer.build();
                    texX = defTexX;
                    texWidth = defTexWidth;
                    texWidth = 4 - defTexWidth / 2;
                    x = (texWidth + defTexWidth) / 2;
                    texX = 28 - (8 - defTexWidth) / 2;
                    renderer.startBuildingQuads();
                    renderer.addVertex(texWidth / 2 + x, y, -2, texWidth + texX, texY);
                    renderer.addVertex(-texWidth / 2 + x, y, -2 - extrude, texX, texY);
                    renderer.addVertex(-texWidth / 2 + x, texHeight + y, -2, texX, texHeight + texY);
                    renderer.addVertex(texWidth / 2 + x, texHeight + y, -2, texWidth + texX, texHeight + texY);
                    renderer.build();
                }
            }
        }
    }

    public float getExtrude()
    {
        return extrude;
    }

    public float getYOffset()
    {
        return yOffset;
    }
}
