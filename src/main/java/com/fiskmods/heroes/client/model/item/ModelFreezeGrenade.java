package com.fiskmods.heroes.client.model.item;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelFreezeGrenade extends ModelBase
{
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape6;
    public ModelRenderer shape10;
    public ModelRenderer shape11;
    public ModelRenderer shape3;
    public ModelRenderer shape4;
    public ModelRenderer shape5;
    public ModelRenderer shape7;
    public ModelRenderer shape8;
    public ModelRenderer shape9;

    public ModelFreezeGrenade()
    {
        textureWidth = 64;
        textureHeight = 32;
        shape2 = new ModelRenderer(this, 0, 11);
        shape2.setRotationPoint(-1.5F, 0.0F, 0.0F);
        shape2.addBox(-0.8F, -5.5F, -1.0F, 1, 12, 2, 0.0F);
        shape9 = new ModelRenderer(this, 10, 15);
        shape9.setRotationPoint(0.0F, -2.7F, 0.0F);
        shape9.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        shape5 = new ModelRenderer(this, 9, 0);
        shape5.setRotationPoint(-1.0F, 3.0F, 0.0F);
        shape5.addBox(0.0F, 0.0F, -1.0F, 1, 1, 2, 0.0F);
        setRotateAngle(shape5, 0.0F, 0.0F, -0.7853981633974483F);
        shape4 = new ModelRenderer(this, 9, 0);
        shape4.setRotationPoint(-1.0F, -3.0F, 0.0F);
        shape4.addBox(0.0F, -1.0F, -1.0F, 1, 1, 2, 0.0F);
        setRotateAngle(shape4, 0.0F, 0.0F, 0.7853981633974483F);
        shape11 = new ModelRenderer(this, 0, 25);
        shape11.setRotationPoint(0.0F, -0.4F, 0.0F);
        shape11.addBox(0.0F, 0.0F, -1.0F, 2, 3, 2, 0.0F);
        shape10 = new ModelRenderer(this, 6, 18);
        shape10.setRotationPoint(0.0F, -0.4F, 0.0F);
        shape10.addBox(-1.0F, 0.0F, -2.0F, 2, 3, 4, 0.0F);
        shape7 = new ModelRenderer(this, 12, 0);
        shape7.setRotationPoint(0.0F, -1.2F, 0.0F);
        shape7.addBox(-1.5F, -0.5F, -1.5F, 3, 1, 3, 0.0F);
        shape3 = new ModelRenderer(this, 6, 11);
        shape3.setRotationPoint(-0.5F, -0.5F, 0.0F);
        shape3.addBox(-1.0F, -3.0F, -0.5F, 1, 6, 1, 0.0F);
        shape1 = new ModelRenderer(this, 0, 0);
        shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape1.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        shape8 = new ModelRenderer(this, 10, 9);
        shape8.setRotationPoint(0.0F, -2.0F, 0.0F);
        shape8.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        shape6 = new ModelRenderer(this, 12, 4);
        shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape6.addBox(-1.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F);
        shape1.addChild(shape2);
        shape8.addChild(shape9);
        shape3.addChild(shape5);
        shape3.addChild(shape4);
        shape1.addChild(shape11);
        shape1.addChild(shape10);
        shape6.addChild(shape7);
        shape2.addChild(shape3);
        shape6.addChild(shape8);
        shape1.addChild(shape6);
    }

    public void render()
    {
        shape1.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
