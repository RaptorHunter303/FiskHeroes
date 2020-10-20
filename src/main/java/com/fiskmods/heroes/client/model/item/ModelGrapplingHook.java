package com.fiskmods.heroes.client.model.item;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelGrapplingHook extends ModelBase
{
    public ModelRenderer main;
    public ModelRenderer extension1_1;
    public ModelRenderer extension2_1;
    public ModelRenderer extension3_1;
    public ModelRenderer extension1_2;
    public ModelRenderer extension1_3;
    public ModelRenderer extension2_2;
    public ModelRenderer extension2_3;
    public ModelRenderer extension3_2;
    public ModelRenderer extension3_3;

    public ModelGrapplingHook()
    {
        textureWidth = 64;
        textureHeight = 32;
        extension3_1 = new ModelRenderer(this, 4, 0);
        extension3_1.setRotationPoint(0.0F, 1.7F, 0.0F);
        extension3_1.addBox(-0.5F, -1.0F, -4.0F, 1, 1, 4, 0.0F);
        setRotateAngle(extension3_1, 0.0F, -2.0943951023931953F, 0.0F);
        extension3_2 = new ModelRenderer(this, 0, 8);
        extension3_2.setRotationPoint(0.5F, -0.8F, -4.0F);
        extension3_2.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(extension3_2, 0.5235987755982988F, 0.6108652381980153F, 0.5235987755982988F);
        extension3_3 = new ModelRenderer(this, 0, 8);
        extension3_3.mirror = true;
        extension3_3.setRotationPoint(-0.5F, -0.8F, -4.0F);
        extension3_3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(extension3_3, 0.5235987755982988F, -0.6108652381980153F, -0.5235987755982988F);
        extension1_2 = new ModelRenderer(this, 0, 8);
        extension1_2.setRotationPoint(0.5F, -0.8F, -4.0F);
        extension1_2.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(extension1_2, 0.5235987755982988F, 0.6108652381980153F, 0.5235987755982988F);
        extension1_3 = new ModelRenderer(this, 0, 8);
        extension1_3.mirror = true;
        extension1_3.setRotationPoint(-0.5F, -0.8F, -4.0F);
        extension1_3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(extension1_3, 0.5235987755982988F, -0.6108652381980153F, -0.5235987755982988F);
        extension2_2 = new ModelRenderer(this, 0, 8);
        extension2_2.setRotationPoint(0.5F, -0.8F, -4.0F);
        extension2_2.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(extension2_2, 0.5235987755982988F, 0.6108652381980153F, 0.5235987755982988F);
        extension2_1 = new ModelRenderer(this, 4, 0);
        extension2_1.setRotationPoint(0.0F, 1.7F, 0.0F);
        extension2_1.addBox(-0.5F, -1.0F, -4.0F, 1, 1, 4, 0.0F);
        setRotateAngle(extension2_1, 0.0F, 2.0943951023931953F, 0.0F);
        main = new ModelRenderer(this, 0, 0);
        main.setRotationPoint(0.0F, 0.0F, 0.0F);
        main.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
        extension2_3 = new ModelRenderer(this, 0, 8);
        extension2_3.mirror = true;
        extension2_3.setRotationPoint(-0.5F, -0.8F, -4.0F);
        extension2_3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(extension2_3, 0.5235987755982988F, -0.6108652381980153F, -0.5235987755982988F);
        extension1_1 = new ModelRenderer(this, 4, 0);
        extension1_1.setRotationPoint(0.0F, 1.7F, 0.0F);
        extension1_1.addBox(-0.5F, -1.0F, -4.0F, 1, 1, 4, 0.0F);
        main.addChild(extension3_1);
        extension3_1.addChild(extension3_2);
        extension3_1.addChild(extension3_3);
        extension1_1.addChild(extension1_2);
        extension1_1.addChild(extension1_3);
        extension2_1.addChild(extension2_2);
        main.addChild(extension2_1);
        extension2_1.addChild(extension2_3);
        main.addChild(extension1_1);
    }

    public void render(float grappleTimer)
    {
        ModelRenderer[] extensions1 = {extension1_1, extension2_1, extension3_1};
        ModelRenderer[] extensions2 = {extension1_2, extension2_2, extension3_2};
        ModelRenderer[] extensions3 = {extension1_3, extension2_3, extension3_3};
        float f = 1 - grappleTimer;

        for (int i = 0; i < extensions1.length; ++i)
        {
            extensions1[i].rotateAngleX = 0.5F + 1.2F * f;
        }

        main.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
