package com.fiskmods.heroes.client.model.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.util.FiskServerUtils;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelBoStaff extends ModelBase
{
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;

    public ModelBoStaff()
    {
        textureWidth = 16;
        textureHeight = 16;
        shape1 = new ModelRenderer(this, 0, 0);
        shape1.setRotationPoint(0, 0, 0);
        shape1.addBox(-0.5F, 0, -0.5F, 1, 8, 1, 0);
        shape2 = new ModelRenderer(this, 4, 0);
        shape2.setRotationPoint(0, 8, 0);
        shape2.addBox(-0.5F, 0, -0.5F, 1, 8, 1, 0);
        shape3 = new ModelRenderer(this, 8, 0);
        shape3.setRotationPoint(0, 16, 0);
        shape3.addBox(-0.5F, 0, -0.5F, 1, 8, 1, 0);
    }

    public void render(float f)
    {
        shape2.rotationPointY = FiskServerUtils.interpolate(0.5F, 8, f);
        shape3.rotationPointY = FiskServerUtils.interpolate(1, 16, f);

        GL11.glPushMatrix();
        GL11.glTranslatef(shape2.offsetX, shape2.offsetY, shape2.offsetZ);
        GL11.glTranslatef(shape2.rotationPointX * 0.0625F, shape2.rotationPointY * 0.0625F, shape2.rotationPointZ * 0.0625F);
        GL11.glScaled(0.85, 1, 0.85);
        GL11.glTranslatef(-shape2.offsetX, -shape2.offsetY, -shape2.offsetZ);
        GL11.glTranslatef(-shape2.rotationPointX * 0.0625F, -shape2.rotationPointY * 0.0625F, -shape2.rotationPointZ * 0.0625F);
        shape2.render(0.0625F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(shape3.offsetX, shape3.offsetY, shape3.offsetZ);
        GL11.glTranslatef(shape3.rotationPointX * 0.0625F, shape3.rotationPointY * 0.0625F, shape3.rotationPointZ * 0.0625F);
        GL11.glScaled(0.7, 1, 0.7);
        GL11.glTranslatef(-shape3.offsetX, -shape3.offsetY, -shape3.offsetZ);
        GL11.glTranslatef(-shape3.rotationPointX * 0.0625F, -shape3.rotationPointY * 0.0625F, -shape3.rotationPointZ * 0.0625F);
        shape3.render(0.0625F);
        GL11.glPopMatrix();
        shape1.render(0.0625F);
    }
}
