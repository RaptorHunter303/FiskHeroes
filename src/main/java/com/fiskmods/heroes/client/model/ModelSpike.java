package com.fiskmods.heroes.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;

public class ModelSpike extends ModelBase
{
    private ShapeRenderer spike;

    public ModelSpike(float width, float sharpness, float texScale)
    {
        float maxU = texScale;
        float maxV = texScale / width;
        float length = 1 - sharpness;

        float s = maxV * (length - sharpness);
        float w = width / 2;

        textureWidth = 1;
        textureHeight = 1;
        spike = new ShapeRenderer(this);
        spike.startBuildingQuads();
        spike.addVertex(w, 0, w, maxU, maxU);
        spike.addVertex(-w, 0, w, 0, maxU);
        spike.addVertex(-w, 0, -w, 0, 0);
        spike.addVertex(w, 0, -w, maxU, 0);
        spike.build();
        spike.addVertex(-w, 0, -w, 0, 0);
        spike.addVertex(-w, length, -w, 0, maxV * length);
        spike.addVertex(w, length, -w, maxU, maxV * length);
        spike.addVertex(w, 0, -w, maxU, 0);
        spike.build();
        spike.addVertex(w, length, w, maxU, maxV * length);
        spike.addVertex(-w, length, w, 0, maxV * length);
        spike.addVertex(-w, 0, w, 0, 0);
        spike.addVertex(w, 0, w, maxU, 0);
        spike.build();
        spike.addVertex(-w, length, w, maxU, maxV * length);
        spike.addVertex(-w, length, -w, 0, maxV * length);
        spike.addVertex(-w, 0, -w, 0, 0);
        spike.addVertex(-w, 0, w, maxU, 0);
        spike.build();
        spike.addVertex(w, 0, -w, 0, 0);
        spike.addVertex(w, length, -w, 0, maxV * length);
        spike.addVertex(w, length, w, maxU, maxV * length);
        spike.addVertex(w, 0, w, maxU, 0);
        spike.build();

        spike.startBuilding(GL11.GL_TRIANGLES);
        spike.addVertex(-w, length, -w, 0, s);
        spike.addVertex(0, 1, 0, maxU / 2, 1);
        spike.addVertex(w, length, -w, maxU, s);
        spike.build();
        spike.addVertex(0, 1, 0, maxU / 2, 1);
        spike.addVertex(-w, length, w, 0, s);
        spike.addVertex(w, length, w, maxU, s);
        spike.build();
        spike.addVertex(0, 1, 0, maxU / 2, 1);
        spike.addVertex(-w, length, -w, 0, s);
        spike.addVertex(-w, length, w, maxU, s);
        spike.build();
        spike.addVertex(w, length, -w, 0, s);
        spike.addVertex(0, 1, 0, maxU / 2, 1);
        spike.addVertex(w, length, w, maxU, s);
        spike.build();
    }

    public void render(float scale)
    {
        spike.render(scale);
    }
}
