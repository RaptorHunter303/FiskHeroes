package com.fiskmods.heroes.client.model.item;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelSmokePellet extends ModelBase
{
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;
    public ModelRenderer shape4;
    public ModelRenderer shape5;
    public ModelRenderer shape6;
    public ModelRenderer shape7;

    public ModelSmokePellet()
    {
        textureWidth = 64;
        textureHeight = 32;
        shape4 = new ModelRenderer(this, 8, 0);
        shape4.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape4.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 1, 0.0F);
        setRotateAngle(shape4, 0.0F, -1.5707963267948966F, 0.0F);
        shape3 = new ModelRenderer(this, 24, 0);
        shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape3.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 1, 0.0F);
        setRotateAngle(shape3, 0.0F, 1.5707963267948966F, 0.0F);
        shape7 = new ModelRenderer(this, 20, 4);
        shape7.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape7.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 1, 0.0F);
        setRotateAngle(shape7, 1.5707963267948966F, 0.0F, 0.0F);
        shape6 = new ModelRenderer(this, 12, 4);
        shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape6.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 1, 0.0F);
        setRotateAngle(shape6, -1.5707963267948966F, 0.0F, 0.0F);
        shape1 = new ModelRenderer(this, 0, 4);
        shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape1.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
        shape2 = new ModelRenderer(this, 0, 0);
        shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape2.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 1, 0.0F);
        shape5 = new ModelRenderer(this, 16, 0);
        shape5.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape5.addBox(-1.5F, -1.5F, -2.5F, 3, 3, 1, 0.0F);
        setRotateAngle(shape5, 0.0F, 3.141592653589793F, 0.0F);
        shape1.addChild(shape4);
        shape1.addChild(shape3);
        shape1.addChild(shape7);
        shape1.addChild(shape6);
        shape1.addChild(shape2);
        shape1.addChild(shape5);
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
