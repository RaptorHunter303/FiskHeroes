package com.fiskmods.heroes.client.model;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

public class ShapeRenderer
{
    public float textureWidth;
    public float textureHeight;

    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;

    private boolean compiled;
    private int displayList;
    public boolean showModel;
    public boolean isHidden;

    public List<DrawnShape> shapeList;
    public List<ShapeRenderer> childModels;

    public List<PositionTextureVertex> vertexList;
    public int vertexMode;

    public float offsetX;
    public float offsetY;
    public float offsetZ;

    public ShapeRenderer(int width, int height)
    {
        showModel = true;
        shapeList = new ArrayList<>();
        setTextureSize(width, height);
    }

    public ShapeRenderer(ModelBase model)
    {
        this(model.textureWidth, model.textureHeight);
    }

    public void addChild(ShapeRenderer renderer)
    {
        if (childModels == null)
        {
            childModels = new ArrayList<>();
        }

        childModels.add(renderer);
    }

    public void startBuilding(int mode)
    {
        vertexList = new ArrayList<>();
        vertexMode = mode;
    }

    public void startBuildingQuads()
    {
        startBuilding(GL11.GL_QUADS);
    }

    public ShapeRenderer mirrorFace()
    {
        if (vertexList.size() == 3)
        {
            PositionTextureVertex v = vertexList.get(0);
            vertexList.set(0, vertexList.get(2));
            vertexList.set(2, v);
        }
        else if (vertexList.size() > 2)
        {
            PositionTextureVertex v = vertexList.get(0);
            vertexList.set(0, vertexList.get(vertexList.size() - 2));
            vertexList.set(vertexList.size() - 2, v);
        }

        return this;
    }

    public void build()
    {
        addShape(vertexList.toArray(new PositionTextureVertex[0]));
        vertexList.clear();
    }

    public void addVertex(float x, float y, float z, float textureX, float textureY)
    {
        addVertex(new PositionTextureVertex(x, y, z, textureX, textureY));
    }

    public void addVertex(Vec3 vector, float textureX, float textureY)
    {
        addVertex(new PositionTextureVertex(vector, textureX, textureY));
    }

    public void addVertex(Vec3 vector, double textureX, double textureY)
    {
        addVertex(vector, (float) textureX, (float) textureY);
    }

    public void addVertex(PositionTextureVertex vertex)
    {
        vertexList.add(vertex);
    }

    public void addShape(PositionTextureVertex... vertices)
    {
        for (PositionTextureVertex vertex : vertices)
        {
            vertex.texturePositionX /= textureWidth;
            vertex.texturePositionY /= textureHeight;
        }

        shapeList.add(new DrawnShape(vertexMode, vertices));
    }

    public void addShape(float textureX, float textureY, Vec3... vectors)
    {
        shapeList.add(new DrawnShape(vertexMode, textureX / textureWidth, textureY / textureHeight, vectors));
    }

    public void addShape(Vec3... vectors)
    {
        shapeList.add(new DrawnShape(vertexMode, vectors));
    }

    public void setRotationPoint(float x, float y, float z)
    {
        rotationPointX = x;
        rotationPointY = y;
        rotationPointZ = z;
    }

    @SideOnly(Side.CLIENT)
    public void render(float scale)
    {
        if (!isHidden)
        {
            if (showModel)
            {
                if (!compiled)
                {
                    compileDisplayList(scale);
                }

                GL11.glTranslatef(offsetX, offsetY, offsetZ);
                int i;

                if (rotateAngleX == 0.0F && rotateAngleY == 0.0F && rotateAngleZ == 0.0F)
                {
                    if (rotationPointX == 0.0F && rotationPointY == 0.0F && rotationPointZ == 0.0F)
                    {
                        GL11.glCallList(displayList);

                        if (childModels != null)
                        {
                            for (i = 0; i < childModels.size(); ++i)
                            {
                                childModels.get(i).render(scale);
                            }
                        }
                    }
                    else
                    {
                        GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
                        GL11.glCallList(displayList);

                        if (childModels != null)
                        {
                            for (i = 0; i < childModels.size(); ++i)
                            {
                                childModels.get(i).render(scale);
                            }
                        }

                        GL11.glTranslatef(-rotationPointX * scale, -rotationPointY * scale, -rotationPointZ * scale);
                    }
                }
                else
                {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

                    if (rotateAngleZ != 0.0F)
                    {
                        GL11.glRotatef(rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (rotateAngleY != 0.0F)
                    {
                        GL11.glRotatef(rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (rotateAngleX != 0.0F)
                    {
                        GL11.glRotatef(rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                    }

                    GL11.glCallList(displayList);

                    if (childModels != null)
                    {
                        for (i = 0; i < childModels.size(); ++i)
                        {
                            childModels.get(i).render(scale);
                        }
                    }

                    GL11.glPopMatrix();
                }

                GL11.glTranslatef(-offsetX, -offsetY, -offsetZ);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderWithRotation(float scale)
    {
        if (!isHidden)
        {
            if (showModel)
            {
                if (!compiled)
                {
                    compileDisplayList(scale);
                }

                GL11.glPushMatrix();
                GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

                if (rotateAngleY != 0.0F)
                {
                    GL11.glRotatef(rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (rotateAngleX != 0.0F)
                {
                    GL11.glRotatef(rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                }

                if (rotateAngleZ != 0.0F)
                {
                    GL11.glRotatef(rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                GL11.glCallList(displayList);
                GL11.glPopMatrix();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void postRender(float scale)
    {
        if (!isHidden)
        {
            if (showModel)
            {
                if (!compiled)
                {
                    compileDisplayList(scale);
                }

                if (rotateAngleX == 0.0F && rotateAngleY == 0.0F && rotateAngleZ == 0.0F)
                {
                    if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F)
                    {
                        GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
                    }
                }
                else
                {
                    GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

                    if (rotateAngleZ != 0.0F)
                    {
                        GL11.glRotatef(rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (rotateAngleY != 0.0F)
                    {
                        GL11.glRotatef(rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (rotateAngleX != 0.0F)
                    {
                        GL11.glRotatef(rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void compileDisplayList(float scale)
    {
        displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.instance;

        for (DrawnShape element : shapeList)
        {
            element.draw(tessellator, scale);
        }

        GL11.glEndList();
        compiled = true;
    }

    public ShapeRenderer setTextureSize(int width, int height)
    {
        textureWidth = width;
        textureHeight = height;
        return this;
    }
}
