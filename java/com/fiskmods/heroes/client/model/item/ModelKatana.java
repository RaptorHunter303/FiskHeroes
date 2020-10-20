package com.fiskmods.heroes.client.model.item;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelKatana extends ModelBase
{
    public ModelRenderer handle;
    public ModelRenderer guard;
    public ModelRenderer blade1;
    public ModelRenderer blade2;
    public ModelRenderer blade4;
    public ModelRenderer blade5;
    public ModelRenderer blade3;

    public ModelKatana()
    {
        textureWidth = 32;
        textureHeight = 16;
        blade4 = new ModelRenderer(this, 8, 14);
        blade4.setRotationPoint(0, -10.1F, 0.89F);
        blade4.addBox(-0.5F, -1, -0.5F, 1, 1, 1, 0);
        setRotateAngle(blade4, 0.17453292519943295F, 0, 0);
        blade1 = new ModelRenderer(this, 8, 0);
        blade1.setRotationPoint(0, -7, 0);
        blade1.addBox(-0.5F, -10, -1.5F, 1, 10, 3, 0);
        blade2 = new ModelRenderer(this, 0, 14);
        blade2.setRotationPoint(0, -9.59F, -1.21F);
        blade2.addBox(-0.5F, -1, -0.5F, 1, 1, 1, 0);
        setRotateAngle(blade2, -0.9599310885968813F, 0, 0);
        blade5 = new ModelRenderer(this, 12, 14);
        blade5.setRotationPoint(0, -9.87F, 1.02F);
        blade5.addBox(-0.5F, -1, -0.5F, 1, 1, 1, 0);
        setRotateAngle(blade5, 0.2617993877991494F, 0, 0);
        guard = new ModelRenderer(this, 16, 0);
        guard.setRotationPoint(0, -6, 0);
        guard.addBox(-1, -1, -1.5F, 2, 1, 3, 0);
        handle = new ModelRenderer(this, 0, 0);
        handle.setRotationPoint(0, 0, 0);
        handle.addBox(-1, -6, -1, 2, 12, 2, 0);
        blade3 = new ModelRenderer(this, 4, 14);
        blade3.setRotationPoint(0, -0.91F, -0.01F);
        blade3.addBox(-0.5F, -1, -0.5F, 1, 1, 1, 0);
        setRotateAngle(blade3, -0.17453292519943295F, 0, 0);
        blade1.addChild(blade4);
        blade1.addChild(blade2);
        blade1.addChild(blade5);
        blade2.addChild(blade3);
    }

    public void render()
    {
        if (guard.isHidden)
        {
            handle.rotationPointY = -1;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(blade1.offsetX, blade1.offsetY, blade1.offsetZ);
        GL11.glTranslatef(blade1.rotationPointX * 0.0625F, blade1.rotationPointY * 0.0625F, blade1.rotationPointZ * 0.0625F);
        GL11.glScaled(0.5D, 3.2D, 0.6D);
        GL11.glTranslatef(-blade1.offsetX, -blade1.offsetY, -blade1.offsetZ);
        GL11.glTranslatef(-blade1.rotationPointX * 0.0625F, -blade1.rotationPointY * 0.0625F, -blade1.rotationPointZ * 0.0625F);
        blade1.render(0.0625F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(guard.offsetX, guard.offsetY, guard.offsetZ);
        GL11.glTranslatef(guard.rotationPointX * 0.0625F, guard.rotationPointY * 0.0625F, guard.rotationPointZ * 0.0625F);
        GL11.glScaled(1.1D, 1.0D, 1.1D);
        GL11.glTranslatef(-guard.offsetX, -guard.offsetY, -guard.offsetZ);
        GL11.glTranslatef(-guard.rotationPointX * 0.0625F, -guard.rotationPointY * 0.0625F, -guard.rotationPointZ * 0.0625F);
        guard.render(0.0625F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(handle.offsetX, handle.offsetY, handle.offsetZ);
        GL11.glTranslatef(handle.rotationPointX * 0.0625F, handle.rotationPointY * 0.0625F, handle.rotationPointZ * 0.0625F);
        GL11.glScaled(0.7D, 1.0D, 1.0D);
        GL11.glTranslatef(-handle.offsetX, -handle.offsetY, -handle.offsetZ);
        GL11.glTranslatef(-handle.rotationPointX * 0.0625F, -handle.rotationPointY * 0.0625F, -handle.rotationPointZ * 0.0625F);
        handle.render(0.0625F);
        GL11.glPopMatrix();
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
