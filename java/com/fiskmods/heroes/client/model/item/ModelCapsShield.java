package com.fiskmods.heroes.client.model.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.model.ShapeRenderer;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.Vec3;

public class ModelCapsShield extends ModelBase
{
    public ShapeRenderer shape;
    public ModelRenderer strap1;
    public ModelRenderer strap2;
    public ModelRenderer strapLoose1;
    public ModelRenderer strapLoose2;

    public ModelCapsShield()
    {
        textureWidth = 64;
        textureHeight = 32;
        shape = new ShapeRenderer(this);

        final float offsetY = -0.4F;
        final float scale = 0.375F;
        final float length = scale * 2.625F;
        final float texX = 0.5F;

        float res = 0.2F / scale;
        float thickness = scale * 0.3F;
        float angle = 360F / 5;

        shape.startBuilding(GL11.GL_TRIANGLES);
        Vec3 vec0 = Vec3.createVectorHelper(scale, offsetY - thickness, 0);

        for (int i = 0; i < 5; ++i)
        {
            Vec3 vec30 = VectorHelper.rotateAroundY(Vec3.createVectorHelper(length, vec0.yCoord + thickness / 4 * 3, 0), Math.toRadians(angle * i - 90));
            Vec3 vec31 = VectorHelper.rotateAroundY(vec0, Math.toRadians(angle * (i - 0.5F) - 90));
            Vec3 vec32 = VectorHelper.rotateAroundY(vec0, Math.toRadians(angle * (i + 0.5F) - 90));

            shape.addVertex(vec31, vec31.xCoord * res * 8 + 4, -vec31.zCoord * res * 8 + 28 + 0.5F);
            shape.addVertex(Vec3.createVectorHelper(0, vec0.yCoord, 0), 4, 28 + 0.5F);
            shape.addVertex(vec32, vec32.xCoord * res * 8 + 4, -vec32.zCoord * res * 8 + 28 + 0.5F);
            shape.build();
            shape.addVertex(vec30, vec30.xCoord * res * 8 + 4, -vec30.zCoord * res * 8 + 28 + 0.5F);
            shape.addVertex(vec31, vec31.xCoord * res * 8 + 4, -vec31.zCoord * res * 8 + 28 + 0.5F);
            shape.addVertex(vec32, vec32.xCoord * res * 8 + 4, -vec32.zCoord * res * 8 + 28 + 0.5F);
            shape.build();
        }

        shape.startBuildingQuads();

        for (int i = 0; i < 5; ++i)
        {
            Vec3 vec30 = VectorHelper.rotateAroundY(Vec3.createVectorHelper(length, vec0.yCoord + thickness / 4 * 3, 0), Math.toRadians(angle * i - 90));
            Vec3 vec31 = VectorHelper.rotateAroundY(vec0, Math.toRadians(angle * (i - 0.5F) - 90));
            Vec3 vec32 = VectorHelper.rotateAroundY(vec0, Math.toRadians(angle * (i + 0.5F) - 90));
            Vec3 vec33 = vec30.addVector(0, thickness / 2, 0);

            shape.addVertex(vec33, vec33.xCoord * res * 8 + 4, -vec33.zCoord * res * 8 + 28 + 0.5F);
            shape.addVertex(vec31.addVector(0, thickness, 0), vec31.xCoord * res * 8 + 4, -vec31.zCoord * res * 8 + 28 + 0.5F);
            shape.addVertex(vec31, vec31.xCoord * res * 8 + 4, -vec31.zCoord * res * 8 + 28 + 0.5F);
            shape.addVertex(vec30, vec30.xCoord * res * 8 + 4, -vec30.zCoord * res * 8 + 28 + 0.5F);
            shape.build();
            shape.addVertex(vec32, vec32.xCoord * res * 8 + 4, -vec32.zCoord * res * 8 + 28 + 0.5F);
            shape.addVertex(vec32.addVector(0, thickness, 0), vec32.xCoord * res * 8 + 4, -vec32.zCoord * res * 8 + 28 + 0.5F);
            shape.addVertex(vec33, vec33.xCoord * res * 8 + 4, -vec33.zCoord * res * 8 + 28 + 0.5F);
            shape.addVertex(vec30, vec30.xCoord * res * 8 + 4, -vec30.zCoord * res * 8 + 28 + 0.5F);
            shape.build();
        }

        final int corners = 24;
        final int curves = 3;

        vec0 = Vec3.createVectorHelper(1, offsetY, 0);
        angle = 360F / corners;
        thickness = 0.5F;
        res = 0.5F;

        final float res1 = 0.215F;
        final float f = thickness / 2;
        shape.startBuilding(GL11.GL_TRIANGLES);

        for (int i = 0; i < corners; ++i)
        {
            Vec3 vec30 = VectorHelper.rotateAroundY(vec0, Math.toRadians(angle * (i - 0.5F) - 90));
            Vec3 vec31 = VectorHelper.rotateAroundY(vec0, Math.toRadians(angle * (i + 0.5F) - 90));

            shape.addVertex(vec30, -vec30.xCoord * res * 8 + 12, -vec30.zCoord * res * 8 + 28);
            shape.addVertex(Vec3.createVectorHelper(0, offsetY, 0), 12, 28);
            shape.addVertex(vec31, -vec31.xCoord * res * 8 + 12, -vec31.zCoord * res * 8 + 28);
            shape.build();

            shape.addVertex(vec30.addVector(0, f, 0), vec30.xCoord * res1 * 24 + 12, -vec30.zCoord * res1 * 24 + 12);
            shape.addVertex(vec31.addVector(0, f, 0), vec31.xCoord * res1 * 24 + 12, -vec31.zCoord * res1 * 24 + 12);
            shape.addVertex(Vec3.createVectorHelper(0, offsetY + f, 0), 12, 12);
            shape.build();
        }

        shape.startBuildingQuads();

        for (int i = 0; i < corners; ++i)
        {
            double a0 = Math.toRadians(angle * (i - 0.5F) - 90);
            double a1 = Math.toRadians(angle * (i + 0.5F) - 90);
            Vec3 vec30 = VectorHelper.rotateAroundY(vec0.addVector(0, f, 0), a0);
            Vec3 vec31 = VectorHelper.rotateAroundY(vec0.addVector(0, f, 0), a1);

            for (int j = 1; j <= curves; ++j)
            {
                float f1 = (float) j / curves;
                shape.addVertex(vec30, vec30.xCoord * res1 * 24 + 12, -vec30.zCoord * res1 * 24 + 12);
                shape.addVertex(vec31, vec31.xCoord * res1 * 24 + 12, -vec31.zCoord * res1 * 24 + 12);

                vec30 = Vec3.createVectorHelper(1 + Math.sin(f1) * 1.5F, offsetY + f1 * f + f, 0);
                vec31 = VectorHelper.rotateAroundY(vec30, a1);
                vec30 = VectorHelper.rotateAroundY(vec30, a0);

                shape.addVertex(vec31, vec31.xCoord * res1 * 24 + 12, -vec31.zCoord * res1 * 24 + 12);
                shape.addVertex(vec30, vec30.xCoord * res1 * 24 + 12, -vec30.zCoord * res1 * 24 + 12);
                shape.mirrorFace().build();
            }

            vec30 = VectorHelper.rotateAroundY(vec0, a0);
            vec31 = VectorHelper.rotateAroundY(vec0, a1);
            float texY = 0;

            for (int j = 1; j <= curves; ++j)
            {
                float f1 = (float) j / curves;
                shape.addVertex(vec30, textureWidth - 1, texY * 15);
                shape.addVertex(vec31, textureWidth, texY * 15);

                texY += (float) (j + 1) / curves - f1;
                vec30 = Vec3.createVectorHelper(1 + Math.sin(f1) * 1.5F, offsetY + f1 * thickness, 0);
                vec31 = VectorHelper.rotateAroundY(vec30, a1);
                vec30 = VectorHelper.rotateAroundY(vec30, a0);

                shape.addVertex(vec31, textureWidth, texY * 15);
                shape.addVertex(vec30, textureWidth - 1, texY * 15);
                shape.build();
            }
        }

        strap1 = new ModelRenderer(this, 24, 11);
        strap1.addBox(-0.5F, 0, -2.5F, 1, 5, 5, 0);
        strap1.setRotationPoint(1.25F, -1.1F, 0);
        strap2 = new ModelRenderer(this, 24, 11);
        strap2.addBox(-0.5F, 0, -2.5F, 1, 5, 5, 0);
        strap2.setRotationPoint(-1.25F, -0.75F, 0);

        strapLoose1 = new ModelRenderer(this, 24, 0);
        strapLoose1.addBox(-0.5F, 0, -4, 1, 3, 8, 0);
        strapLoose1.setRotationPoint(2, -0.4F, 0);
        strapLoose2 = new ModelRenderer(this, 24, 0);
        strapLoose2.addBox(-0.5F, 0, -4, 1, 3, 8, 0);
        strapLoose2.setRotationPoint(-1.25F, -0.45F, 0);

        strap1.rotateAngleZ = -0.0873F;
        strap2.rotateAngleZ = -0.0873F;
        strapLoose1.rotateAngleZ = 1.4F;
        strapLoose2.rotateAngleZ = 1.3F;
    }

    public void render(boolean straps)
    {
        boolean flag = GL11.glGetBoolean(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_CULL_FACE);
        shape.render(0.0625F * 3.25F);

        if (!flag)
        {
            GL11.glDisable(GL11.GL_CULL_FACE);
        }

        if (straps)
        {
            strap1.render(0.0625F);
            strap2.render(0.0625F);
        }
        else
        {
            strapLoose1.render(0.0625F);
            strapLoose2.render(0.0625F);
        }
    }
}
