package com.fiskmods.heroes.client.model.item;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelGrapplingGun extends ModelBase
{
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape5;
    public ModelRenderer shape6;
    public ModelRenderer shape7;
    public ModelRenderer shape3;
    public ModelRenderer shape4;
    public ModelRenderer shape8;
    public ModelRenderer shape10;
    public ModelRenderer shape11;
    public ModelRenderer shape9;
    public ModelRenderer shape12;
    public ModelRenderer shape13;
    public ModelRenderer shape14;
    public ModelRenderer shape15;

    public ModelGrapplingGun()
    {
        textureWidth = 64;
        textureHeight = 32;
        shape2 = new ModelRenderer(this, 0, 12);
        shape2.setRotationPoint(0.0F, 5.0F, 0.4F);
        shape2.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 1, 0.0F);
        shape5 = new ModelRenderer(this, 7, 6);
        shape5.setRotationPoint(0.0F, -0.5F, -0.3F);
        shape5.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        setRotateAngle(shape5, -0.33161255787892263F, 0.0F, 0.0F);
        shape14 = new ModelRenderer(this, 10, 0);
        shape14.mirror = true;
        shape14.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape14.addBox(-0.8F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        shape9 = new ModelRenderer(this, 15, 10);
        shape9.setRotationPoint(0.0F, 0.0F, -2.0F);
        shape9.addBox(-1.0F, -2.0F, -7.0F, 2, 2, 7, 0.0F);
        setRotateAngle(shape9, -0.33161255787892263F, 0.0F, 0.0F);
        shape4 = new ModelRenderer(this, 6, 0);
        shape4.setRotationPoint(0.0F, 0.0F, -1.0F);
        shape4.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 1, 0.0F);
        setRotateAngle(shape4, -0.3490658503988659F, 0.0F, 0.0F);
        shape7 = new ModelRenderer(this, 6, 4);
        shape7.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape7.addBox(-1.5F, -3.0F, -3.9F, 3, 3, 5, 0.0F);
        setRotateAngle(shape7, -0.6981317007977318F, 0.0F, 0.0F);
        shape8 = new ModelRenderer(this, 0, 21);
        shape8.setRotationPoint(0.0F, 0.0F, 0.5F);
        shape8.addBox(-1.0F, -1.0F, -2.0F, 2, 1, 2, 0.0F);
        setRotateAngle(shape8, 0.33161255787892263F, 0.0F, 0.0F);
        shape1 = new ModelRenderer(this, 0, 0);
        shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape1.addBox(-1.0F, 0.0F, 0.0F, 2, 5, 2, 0.0F);
        setRotateAngle(shape1, 0.6981317007977318F, 0.0F, 0.0F);
        shape3 = new ModelRenderer(this, 17, 6);
        shape3.setRotationPoint(0.0F, -2.0F, -1.0F);
        shape3.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 1, 0.0F);
        setRotateAngle(shape3, -0.33161255787892263F, 0.0F, 0.0F);
        shape11 = new ModelRenderer(this, 14, 12);
        shape11.setRotationPoint(0.0F, -3.7F, 0.7F);
        shape11.addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2, 0.0F);
        setRotateAngle(shape11, 0.7155849933176751F, 0.0F, 0.0F);
        shape13 = new ModelRenderer(this, 10, 0);
        shape13.setRotationPoint(0.0F, 0.3F, -1.3F);
        shape13.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        setRotateAngle(shape13, 0.8552113334772213F, 0.0F, 0.0F);
        shape10 = new ModelRenderer(this, 0, 15);
        shape10.setRotationPoint(0.0F, -2.45F, -3.3F);
        shape10.addBox(-1.0F, -1.0F, -4.0F, 2, 2, 4, 0.0F);
        shape12 = new ModelRenderer(this, 8, 12);
        shape12.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape12.addBox(-1.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F);
        setRotateAngle(shape12, 0.8552113334772213F, 0.0F, 0.0F);
        shape15 = new ModelRenderer(this, 10, 0);
        shape15.setRotationPoint(0.0F, 0.0F, 0.0F);
        shape15.addBox(-1.2F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        shape6 = new ModelRenderer(this, 0, 7);
        shape6.setRotationPoint(0.0F, 4.55F, 2.0F);
        shape6.addBox(-1.0F, -4.0F, -1.0F, 2, 4, 1, 0.0F);
        setRotateAngle(shape6, -0.17453292519943295F, 0.0F, 0.0F);
        shape1.addChild(shape2);
        shape1.addChild(shape5);
        shape13.addChild(shape14);
        shape8.addChild(shape9);
        shape2.addChild(shape4);
        shape1.addChild(shape7);
        shape7.addChild(shape8);
        shape2.addChild(shape3);
        shape7.addChild(shape11);
        shape12.addChild(shape13);
        shape7.addChild(shape10);
        shape11.addChild(shape12);
        shape14.addChild(shape15);
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
