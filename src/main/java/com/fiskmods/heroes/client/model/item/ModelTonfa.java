package com.fiskmods.heroes.client.model.item;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelTonfa extends ModelBase
{
    public ModelRenderer handle;
    public ModelRenderer stick;

    public ModelTonfa()
    {
        textureWidth = 32;
        textureHeight = 16;
        handle = new ModelRenderer(this, 0, 0);
        handle.setRotationPoint(0.0F, 0.0F, 0.0F);
        handle.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, 0.0F);
        stick = new ModelRenderer(this, 0, 0);
        stick.setRotationPoint(0.0F, 0.0F, 0.0F);
        stick.addBox(-0.5F, 0.0F, -7.5F, 1, 1, 11, 0.0F);
        handle.addChild(stick);
    }

    public void render()
    {
        handle.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
