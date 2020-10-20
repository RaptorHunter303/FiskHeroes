package com.fiskmods.heroes.client.model.item;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelSwordSheath extends ModelBase
{
    public ModelRenderer main;
    public ModelRenderer rightSheath1;
    public ModelRenderer leftSheath1;
    public ModelRenderer rightSheath2;
    public ModelRenderer leftSheath2;

    public ModelSwordSheath()
    {
        textureWidth = 64;
        textureHeight = 64;
        leftSheath1 = new ModelRenderer(this, 0, 0);
        leftSheath1.setRotationPoint(1, 4, 2.5F);
        leftSheath1.addBox(-0.9F, -11, -0.5F, 2, 36, 1, 0);
        setRotateAngle(leftSheath1, 0, 0, 0.5235987755982988F);
        rightSheath1 = new ModelRenderer(this, 0, 0);
        rightSheath1.setRotationPoint(-1, 4, 2.5F);
        rightSheath1.addBox(-0.9F, -11, -0.5F, 2, 36, 1, 0);
        setRotateAngle(rightSheath1, 0, 0, -0.5235987755982988F);
        main = new ModelRenderer(this, 6, 0);
        main.setRotationPoint(0, 4, 2);
        main.addBox(-1.5F, -2, 0, 3, 4, 1, 0);
        rightSheath2 = new ModelRenderer(this, 0, 0);
        rightSheath2.setRotationPoint(0, 0, 0);
        rightSheath2.addBox(-1.1F, -11, -0.5F, 2, 36, 1, 0);
        leftSheath2 = new ModelRenderer(this, 0, 0);
        leftSheath2.setRotationPoint(0, 0, 0);
        leftSheath2.addBox(-1.1F, -11, -0.5F, 2, 36, 1, 0);
        rightSheath1.addChild(rightSheath2);
        leftSheath1.addChild(leftSheath2);
    }

    public void render(float scale)
    {
        main.render(scale);
        GL11.glPushMatrix();
        GL11.glTranslatef(rightSheath1.offsetX, rightSheath1.offsetY, rightSheath1.offsetZ);
        GL11.glTranslatef(rightSheath1.rotationPointX * scale, rightSheath1.rotationPointY * scale, rightSheath1.rotationPointZ * scale);
        GL11.glScaled(0.6D, 0.6D, 0.6D);
        GL11.glTranslatef(-rightSheath1.offsetX, -rightSheath1.offsetY, -rightSheath1.offsetZ);
        GL11.glTranslatef(-rightSheath1.rotationPointX * scale, -rightSheath1.rotationPointY * scale, -rightSheath1.rotationPointZ * scale);
        rightSheath1.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(leftSheath1.offsetX, leftSheath1.offsetY, leftSheath1.offsetZ);
        GL11.glTranslatef(leftSheath1.rotationPointX * scale, leftSheath1.rotationPointY * scale, leftSheath1.rotationPointZ * scale);
        GL11.glScaled(0.6D, 0.6D, 0.6D);
        GL11.glTranslatef(-leftSheath1.offsetX, -leftSheath1.offsetY, -leftSheath1.offsetZ);
        GL11.glTranslatef(-leftSheath1.rotationPointX * scale, -leftSheath1.rotationPointY * scale, -leftSheath1.rotationPointZ * scale);
        leftSheath1.render(scale);
        GL11.glPopMatrix();
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
