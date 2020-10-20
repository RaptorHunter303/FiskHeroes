package com.fiskmods.heroes.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

public class DrawnShape
{
    public PositionTextureVertex[] vertexPositions;
    public int vertexMode;

    public DrawnShape(int mode, PositionTextureVertex... vertices)
    {
        vertexPositions = vertices;
        vertexMode = mode;
    }

    public DrawnShape(int mode, float u, float v, Vec3... vectors)
    {
        vertexPositions = new PositionTextureVertex[vectors.length];
        vertexMode = mode;

        for (int i = 0; i < vectors.length; ++i)
        {
            vertexPositions[i] = new PositionTextureVertex(vectors[i], u, v);
        }
    }

    public DrawnShape(int mode, Vec3... vectors)
    {
        this(mode, 0, 0, vectors);
    }

    public void draw(Tessellator tessellator, float scale)
    {
        boolean flag = (vertexMode & GL11.GL_LIGHTING) == GL11.GL_LIGHTING;
        tessellator.startDrawing(vertexMode & ~GL11.GL_LIGHTING);

        if (vertexPositions.length > 2 && !flag)
        {
            Vec3 vec3 = vertexPositions[1].vector3D.subtract(vertexPositions[0].vector3D);
            Vec3 vec31 = vertexPositions[1].vector3D.subtract(vertexPositions[2].vector3D);
            Vec3 vec32 = vec31.crossProduct(vec3).normalize();
            tessellator.setNormal((float) vec32.xCoord, (float) vec32.yCoord, (float) vec32.zCoord);
        }

        for (PositionTextureVertex vertex : vertexPositions)
        {
            tessellator.addVertexWithUV((float) vertex.vector3D.xCoord * scale, (float) vertex.vector3D.yCoord * scale, (float) vertex.vector3D.zCoord * scale, vertex.texturePositionX, vertex.texturePositionY);
        }

        tessellator.draw();
    }
}
