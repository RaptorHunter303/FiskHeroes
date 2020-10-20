package com.fiskmods.heroes.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelJetpack extends ModelBase
{
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;
    public ModelRenderer shape4;
    public ModelRenderer shape5;
    public ModelRenderer shape6;
    public ModelRenderer shape7;
    public ModelRenderer shape8;
    public ModelRenderer shape9;

    public ModelJetpack()
    {
        textureWidth = 32;
        textureHeight = 16;
        shape8 = new ModelRenderer(this, 0, 10);
        shape8.mirror = true;
        shape8.setRotationPoint(0, 3, 2);
        shape8.addBox(-1, 0, -2, 2, 4, 2, 0);
        setRotateAngle(shape8, -0.4363323129985824F, 0, 0);
        shape1 = new ModelRenderer(this, 6, 0);
        shape1.setRotationPoint(0, 0, 2);
        shape1.addBox(-2.5F, 0, -0.5F, 5, 2, 1, 0);
        setRotateAngle(shape1, 0.6283185307179586F, 0, 0);
        shape2 = new ModelRenderer(this, 8, 3);
        shape2.setRotationPoint(0, 2, 0.5F);
        shape2.addBox(-2, 0, -1, 4, 3, 1, 0);
        setRotateAngle(shape2, -0.6283185307179586F, 0, 0);
        shape9 = new ModelRenderer(this, 0, 1);
        shape9.mirror = true;
        shape9.setRotationPoint(0, 0, 2);
        shape9.addBox(-1, -2, -2, 2, 2, 2, 0);
        setRotateAngle(shape9, 0.6283185307179586F, 0, 0);
        shape7 = new ModelRenderer(this, 0, 5);
        shape7.mirror = true;
        shape7.setRotationPoint(2.3F, 0.8F, -0.8F);
        shape7.addBox(-0.99F, 0, 0, 2, 3, 2, 0);
        setRotateAngle(shape7, -0.6283185307179586F, 0.2617993877991494F, -0.15707963267948966F);
        shape3 = new ModelRenderer(this, 8, 7);
        shape3.setRotationPoint(0, 3, -0.5F);
        shape3.addBox(-2, -0.5F, -1, 4, 4, 1, 0);
        setRotateAngle(shape3, -0.22689280275926282F, 0, 0);
        shape5 = new ModelRenderer(this, 0, 10);
        shape5.setRotationPoint(0, 3, 2);
        shape5.addBox(-1, 0, -2, 2, 4, 2, 0);
        setRotateAngle(shape5, -0.4363323129985824F, 0, 0);
        shape6 = new ModelRenderer(this, 0, 1);
        shape6.setRotationPoint(0, 0, 2);
        shape6.addBox(-1, -2, -2, 2, 2, 2, 0);
        setRotateAngle(shape6, 0.6283185307179586F, 0, 0);
        shape4 = new ModelRenderer(this, 0, 5);
        shape4.setRotationPoint(-2.3F, 0.8F, -0.8F);
        shape4.addBox(-1.01F, 0, 0, 2, 3, 2, 0);
        setRotateAngle(shape4, -0.6283185307179586F, -0.2617993877991494F, 0.15707963267948966F);
        shape7.addChild(shape8);
        shape1.addChild(shape2);
        shape7.addChild(shape9);
        shape1.addChild(shape7);
        shape2.addChild(shape3);
        shape4.addChild(shape5);
        shape4.addChild(shape6);
        shape1.addChild(shape4);
    }

    public void render(float scale)
    {
        shape1.render(scale);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
