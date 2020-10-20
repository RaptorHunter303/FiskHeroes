package com.fiskmods.heroes.client.model.item;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelBoxingGlove extends ModelBase
{
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;
    public ModelRenderer shape6;
    public ModelRenderer shape4;
    public ModelRenderer shape5;
    public ModelRenderer shape7;

    public ModelBoxingGlove()
    {
        textureWidth = 64;
        textureHeight = 32;
        shape2 = new ModelRenderer(this, 46, 0);
        shape2.setRotationPoint(0.0F, -3.0F, 0.0F);
        shape2.addBox(-2.0F, -4.0F, -2.5F, 4, 4, 5, 0.0F);
        shape7 = new ModelRenderer(this, 43, 0);
        shape7.setRotationPoint(0.0F, -2.0F, -1.0F);
        shape7.addBox(-1.0F, -3.0F, 0.0F, 2, 3, 2, 0.0F);
        setRotateAngle(shape7, -0.7853981633974483F, 0.0F, 0.0F);
        shape6 = new ModelRenderer(this, 46, 9);
        shape6.setRotationPoint(-1.0F, 0.0F, -1.5F);
        shape6.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
        setRotateAngle(shape6, 0.6981317007977318F, 0.3490658503988659F, -0.3490658503988659F);
        shape1 = new ModelRenderer(this, 50, 9);
        shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape1.addBox(-1.5F, -3.0F, -2.0F, 3, 3, 4, 0.0F);
        shape3 = new ModelRenderer(this, 50, 16);
        shape3.setRotationPoint(2.0F, -4.0F, 0.0F);
        shape3.addBox(-2.0F, -2.0F, -2.5F, 2, 2, 5, 0.0F);
        setRotateAngle(shape3, 0.0F, 0.0F, -0.6981317007977318F);
        shape5 = new ModelRenderer(this, 50, 23);
        shape5.setRotationPoint(0.0F, -2.0F, 0.0F);
        shape5.addBox(-2.0F, -2.0F, -2.5F, 2, 2, 5, 0.0F);
        setRotateAngle(shape5, 0.0F, 0.0F, -0.8028514559173915F);
        shape4 = new ModelRenderer(this, 43, 13);
        shape4.setRotationPoint(0.0F, -2.0F, 0.0F);
        shape4.addBox(-1.0F, -2.0F, -2.5F, 1, 2, 5, 0.0F);
        setRotateAngle(shape4, 0.0F, 0.0F, -0.7853981633974483F);
        shape1.addChild(shape2);
        shape6.addChild(shape7);
        shape2.addChild(shape6);
        shape2.addChild(shape3);
        shape4.addChild(shape5);
        shape3.addChild(shape4);
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
