package com.fiskmods.heroes.client.model.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

/**
 * @author TheOrion
 */
public class ModelQuiver extends ModelBiped
{
    public ModelRenderer pad;
    public ModelRenderer baseRight;
    public ModelRenderer strap1;
    public ModelRenderer strap5;
    public ModelRenderer baseFront;
    public ModelRenderer baseStrap;
    public ModelRenderer baseStrap_1;
    public ModelRenderer baseLeft;
    public ModelRenderer baseBack;
    public ModelRenderer baseBottom;
    public ModelRenderer strap2;
    public ModelRenderer strap3;
    public ModelRenderer shape4;

    public ModelQuiver()
    {
        textureWidth = 32;
        textureHeight = 32;
        bipedBody = new ModelRenderer(this, 36, 32);
        bipedBody.addBox(-4.0F, 0.0F, -2.0F, 0, 0, 0, 0);
        strap5 = new ModelRenderer(this, 13, 27);
        strap5.setRotationPoint(0.0F, 3.9F, -3.8F);
        strap5.addBox(0.0F, 0.0F, 0.0F, 5, 1, 4, 0.0F);
        setRotateAngle(strap5, 0.0F, 0.03490658503988659F, -0.12217304763960307F);
        baseRight = new ModelRenderer(this, 0, 15);
        baseRight.setRotationPoint(1.8F, 0.9F, 0.7F);
        baseRight.addBox(-1.5F, 0.0F, 0.5F, 1, 9, 2, 0.0F);
        setRotateAngle(baseRight, 0.0F, 0.0F, -0.4363323129985824F);
        baseBack = new ModelRenderer(this, 6, 16);
        baseBack.setRotationPoint(-0.8F, 0.0F, 2.2F);
        baseBack.addBox(0.0F, 0.0F, 0.0F, 2, 9, 1, 0.0F);
        baseStrap = new ModelRenderer(this, 0, 12);
        baseStrap.mirror = true;
        baseStrap.setRotationPoint(-2.0F, 1.0F, 0.0F);
        baseStrap.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        baseStrap_1 = new ModelRenderer(this, 0, 12);
        baseStrap_1.setRotationPoint(1.3F, 1.0F, 0.0F);
        baseStrap_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        strap2 = new ModelRenderer(this, 5, 0);
        strap2.setRotationPoint(5.0F, 1.0F, 0.0F);
        strap2.addBox(0.0F, -1.0F, -4.0F, 1, 1, 5, 0.0F);
        setRotateAngle(strap2, 0.0F, 0.017453292519943295F, -0.6283185307179586F);
        baseBottom = new ModelRenderer(this, 0, 0);
        baseBottom.setRotationPoint(-1.0F, 8.7F, 1.6F);
        baseBottom.addBox(0.0F, 0.0F, -1.0F, 2, 1, 2, 0.0F);
        strap1 = new ModelRenderer(this, 0, 6);
        strap1.setRotationPoint(4.0F, 2.5F, 0.0F);
        strap1.addBox(-4.0F, 0.0F, -4.0F, 9, 1, 5, 0.0F);
        setRotateAngle(strap1, 0.0F, 0.0F, 0.6283185307179586F);
        baseLeft = new ModelRenderer(this, 0, 15);
        baseLeft.mirror = true;
        baseLeft.setRotationPoint(0.9F, 0.0F, 0.5F);
        baseLeft.addBox(0.0F, 0.0F, 0.0F, 1, 9, 2, 0.0F);
        pad = new ModelRenderer(this, 0, 26);
        pad.setRotationPoint(-4.3F, -0.1F, 1.5F);
        pad.addBox(-0.1F, -0.1F, 0.1F, 5, 5, 1, 0.0F);
        shape4 = new ModelRenderer(this, 21, 4);
        shape4.setRotationPoint(0.0F, 0.0F, -4.3F);
        shape4.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F);
        strap3 = new ModelRenderer(this, 18, 1);
        strap3.setRotationPoint(-4.0F, 1.0F, 0.1F);
        strap3.addBox(-1.0F, -1.0F, -4.0F, 1, 1, 4, 0.0F);
        setRotateAngle(strap3, 0.017453292519943295F, -0.017453292519943295F, 0.9599310885968813F);
        baseFront = new ModelRenderer(this, 12, 16);
        baseFront.setRotationPoint(0.2F, 0.0F, -0.1F);
        baseFront.addBox(-1.0F, 0.0F, 0.0F, 2, 9, 1, 0.0F);
        pad.addChild(strap5);
        pad.addChild(baseRight);
        baseRight.addChild(baseBack);
        baseRight.addChild(baseStrap);
        baseRight.addChild(baseStrap_1);
        strap1.addChild(strap2);
        baseFront.addChild(baseBottom);
        pad.addChild(strap1);
        baseRight.addChild(baseLeft);
        strap3.addChild(shape4);
        strap1.addChild(strap3);
        baseRight.addChild(baseFront);
        bipedBody.addChild(pad);
    }

    public void render()
    {
        bipedBody.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
