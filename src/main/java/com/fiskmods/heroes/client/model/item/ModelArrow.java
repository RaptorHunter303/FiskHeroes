package com.fiskmods.heroes.client.model.item;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelArrow extends ModelBase
{
    public ModelRenderer stick;
    public ModelRenderer featherMain1;
    public ModelRenderer featherMain2;
    public ModelRenderer featherMain3;
    public ModelRenderer arrowHeadBase;
    public ModelRenderer featherTop1;
    public ModelRenderer featherOuter1;
    public ModelRenderer featherTop2;
    public ModelRenderer featherOuter2;
    public ModelRenderer featherTop3;
    public ModelRenderer featherOuter3;
    public ModelRenderer arrowHeadTip;
    public ModelRenderer arrowHeadSide1;
    public ModelRenderer arrowHeadSide2;
    public ModelRenderer arrowHeadMiddle3;
    public ModelRenderer arrowHeadFront;
    public ModelRenderer arrowHeadBack;
    public ModelRenderer arrowHeadLowerSide1;
    public ModelRenderer arrowHeadMiddle1;
    public ModelRenderer arrowHeadLowerSide2;
    public ModelRenderer arrowHeadMiddle2;

    public ModelArrow()
    {
        textureWidth = 64;
        textureHeight = 32;
        featherOuter2 = new ModelRenderer(this, 4, 17);
        featherOuter2.setRotationPoint(2.6F, 0.0F, 0.0F);
        featherOuter2.addBox(-0.9F, 0.0F, -0.5F, 2, 6, 1, 0.0F);
        setRotateAngle(featherOuter2, 0.0F, 0.0F, -0.296705972839036F);
        featherTop3 = new ModelRenderer(this, 4, 15);
        featherTop3.setRotationPoint(1.95F, 0.15F, 0.0F);
        featherTop3.addBox(-1.6F, -1.0F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(featherTop3, 0.0F, 0.0F, 0.3490658503988659F);
        featherTop1 = new ModelRenderer(this, 4, 15);
        featherTop1.setRotationPoint(1.95F, 0.15F, 0.0F);
        featherTop1.addBox(-1.6F, -1.0F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(featherTop1, 0.0F, 0.0F, 0.3490658503988659F);
        arrowHeadFront = new ModelRenderer(this, 4, 5);
        arrowHeadFront.setRotationPoint(0.0F, -2.85F, 0.0F);
        arrowHeadFront.addBox(-0.5F, -5.0F, -1.0F, 1, 5, 1, 0.0F);
        setRotateAngle(arrowHeadFront, -0.09599310885968812F, 0.0F, 0.0F);
        arrowHeadBase = new ModelRenderer(this, 4, 0);
        arrowHeadBase.setRotationPoint(0.0F, -9.0F, 0.0F);
        arrowHeadBase.addBox(-0.5F, -3.0F, -1.0F, 1, 3, 2, 0.0F);
        arrowHeadMiddle3 = new ModelRenderer(this, 8, 12);
        arrowHeadMiddle3.setRotationPoint(0.0F, -1.0F, 0.0F);
        arrowHeadMiddle3.addBox(-1.0F, -2.0F, -0.5F, 2, 2, 1, 0.0F);
        arrowHeadTip = new ModelRenderer(this, 8, 0);
        arrowHeadTip.setRotationPoint(0.0F, -7.8F, 0.0F);
        arrowHeadTip.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        setRotateAngle(arrowHeadTip, 0.0F, 0.0F, 0.7853981633974483F);
        arrowHeadLowerSide2 = new ModelRenderer(this, 4, 11);
        arrowHeadLowerSide2.setRotationPoint(0.28F, -0.45F, 0.0F);
        arrowHeadLowerSide2.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(arrowHeadLowerSide2, 0.0F, 0.0F, 1.2217304763960306F);
        arrowHeadSide1 = new ModelRenderer(this, 8, 5);
        arrowHeadSide1.setRotationPoint(-2.3F, -2.0F, 0.0F);
        arrowHeadSide1.addBox(-0.5F, -6.0F, -0.5F, 1, 6, 1, 0.0F);
        setRotateAngle(arrowHeadSide1, 0.0F, 0.0F, 0.3490658503988659F);
        arrowHeadSide2 = new ModelRenderer(this, 8, 5);
        arrowHeadSide2.setRotationPoint(2.3F, -2.0F, 0.0F);
        arrowHeadSide2.addBox(-0.5F, -6.0F, -0.5F, 1, 6, 1, 0.0F);
        setRotateAngle(arrowHeadSide2, 0.0F, 0.0F, -0.3490658503988659F);
        featherMain2 = new ModelRenderer(this, 4, 24);
        featherMain2.setRotationPoint(0.0F, 4.0F, 0.0F);
        featherMain2.addBox(0.65F, -0.3F, -0.5F, 3, 7, 1, 0.0F);
        setRotateAngle(featherMain2, 0.0F, 2.0943951023931953F, 0.0F);
        featherMain1 = new ModelRenderer(this, 4, 24);
        featherMain1.setRotationPoint(0.0F, 4.0F, 0.0F);
        featherMain1.addBox(0.65F, -0.3F, -0.5F, 3, 7, 1, 0.0F);
        featherOuter3 = new ModelRenderer(this, 4, 17);
        featherOuter3.setRotationPoint(2.6F, 0.0F, 0.0F);
        featherOuter3.addBox(-0.9F, 0.0F, -0.5F, 2, 6, 1, 0.0F);
        setRotateAngle(featherOuter3, 0.0F, 0.0F, -0.296705972839036F);
        arrowHeadMiddle1 = new ModelRenderer(this, 11, 1);
        arrowHeadMiddle1.setRotationPoint(1.0F, 0.0F, 0.0F);
        arrowHeadMiddle1.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, 0.0F);
        setRotateAngle(arrowHeadMiddle1, 0.0F, -0.06981317007977318F, -0.06981317007977318F);
        arrowHeadMiddle2 = new ModelRenderer(this, 11, 1);
        arrowHeadMiddle2.setRotationPoint(-1.0F, 0.0F, 0.0F);
        arrowHeadMiddle2.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, 0.0F);
        setRotateAngle(arrowHeadMiddle2, 0.0F, -0.06981317007977318F, 0.06981317007977318F);
        arrowHeadBack = new ModelRenderer(this, 4, 5);
        arrowHeadBack.setRotationPoint(0.0F, -2.85F, 0.0F);
        arrowHeadBack.addBox(-0.5F, -5.0F, 0.0F, 1, 5, 1, 0.0F);
        setRotateAngle(arrowHeadBack, 0.09599310885968812F, 0.0F, 0.0F);
        featherMain3 = new ModelRenderer(this, 4, 24);
        featherMain3.setRotationPoint(0.0F, 4.0F, 0.0F);
        featherMain3.addBox(0.65F, -0.3F, -0.5F, 3, 7, 1, 0.0F);
        setRotateAngle(featherMain3, 0.0F, -2.0943951023931953F, 0.0F);
        featherTop2 = new ModelRenderer(this, 4, 15);
        featherTop2.setRotationPoint(1.95F, 0.15F, 0.0F);
        featherTop2.addBox(-1.6F, -1.0F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(featherTop2, 0.0F, 0.0F, 0.3490658503988659F);
        arrowHeadLowerSide1 = new ModelRenderer(this, 4, 11);
        arrowHeadLowerSide1.setRotationPoint(-0.22F, -0.45F, 0.0F);
        arrowHeadLowerSide1.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(arrowHeadLowerSide1, 0.0F, 0.0F, -1.2217304763960306F);
        stick = new ModelRenderer(this, 0, 0);
        stick.setRotationPoint(0.0F, 0.0F, 0.0F);
        stick.addBox(-0.5F, -9.0F, -0.5F, 1, 18, 1, 0.0F);
        featherOuter1 = new ModelRenderer(this, 4, 17);
        featherOuter1.setRotationPoint(2.6F, 0.0F, 0.0F);
        featherOuter1.addBox(-0.9F, 0.0F, -0.5F, 2, 6, 1, 0.0F);
        setRotateAngle(featherOuter1, 0.0F, 0.0F, -0.296705972839036F);
        featherMain2.addChild(featherOuter2);
        featherMain3.addChild(featherTop3);
        featherMain1.addChild(featherTop1);
        arrowHeadBase.addChild(arrowHeadFront);
        arrowHeadBase.addChild(arrowHeadMiddle3);
        arrowHeadBase.addChild(arrowHeadTip);
        arrowHeadSide2.addChild(arrowHeadLowerSide2);
        arrowHeadBase.addChild(arrowHeadSide1);
        arrowHeadBase.addChild(arrowHeadSide2);
        featherMain3.addChild(featherOuter3);
        arrowHeadSide1.addChild(arrowHeadMiddle1);
        arrowHeadSide2.addChild(arrowHeadMiddle2);
        arrowHeadBase.addChild(arrowHeadBack);
        featherMain2.addChild(featherTop2);
        arrowHeadSide1.addChild(arrowHeadLowerSide1);
        featherMain1.addChild(featherOuter1);
    }

    public void render(float scale)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(arrowHeadBase.offsetX, arrowHeadBase.offsetY, arrowHeadBase.offsetZ);
        GL11.glTranslatef(arrowHeadBase.rotationPointX * scale, arrowHeadBase.rotationPointY * scale, arrowHeadBase.rotationPointZ * scale);
        GL11.glScaled(0.3, 0.4, 0.15);
        GL11.glTranslatef(-arrowHeadBase.offsetX, -arrowHeadBase.offsetY, -arrowHeadBase.offsetZ);
        GL11.glTranslatef(-arrowHeadBase.rotationPointX * scale, -arrowHeadBase.rotationPointY * scale, -arrowHeadBase.rotationPointZ * scale);
        arrowHeadBase.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(featherMain2.offsetX, featherMain2.offsetY, featherMain2.offsetZ);
        GL11.glTranslatef(featherMain2.rotationPointX * scale, featherMain2.rotationPointY * scale, featherMain2.rotationPointZ * scale);
        GL11.glScaled(0.2, 0.5, 0.2);
        GL11.glTranslatef(-featherMain2.offsetX, -featherMain2.offsetY, -featherMain2.offsetZ);
        GL11.glTranslatef(-featherMain2.rotationPointX * scale, -featherMain2.rotationPointY * scale, -featherMain2.rotationPointZ * scale);
        featherMain2.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(featherMain1.offsetX, featherMain1.offsetY, featherMain1.offsetZ);
        GL11.glTranslatef(featherMain1.rotationPointX * scale, featherMain1.rotationPointY * scale, featherMain1.rotationPointZ * scale);
        GL11.glScaled(0.2, 0.5, 0.2);
        GL11.glTranslatef(-featherMain1.offsetX, -featherMain1.offsetY, -featherMain1.offsetZ);
        GL11.glTranslatef(-featherMain1.rotationPointX * scale, -featherMain1.rotationPointY * scale, -featherMain1.rotationPointZ * scale);
        featherMain1.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(featherMain3.offsetX, featherMain3.offsetY, featherMain3.offsetZ);
        GL11.glTranslatef(featherMain3.rotationPointX * scale, featherMain3.rotationPointY * scale, featherMain3.rotationPointZ * scale);
        GL11.glScaled(0.2, 0.5, 0.2);
        GL11.glTranslatef(-featherMain3.offsetX, -featherMain3.offsetY, -featherMain3.offsetZ);
        GL11.glTranslatef(-featherMain3.rotationPointX * scale, -featherMain3.rotationPointY * scale, -featherMain3.rotationPointZ * scale);
        featherMain3.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(stick.offsetX, stick.offsetY, stick.offsetZ);
        GL11.glTranslatef(stick.rotationPointX * scale, stick.rotationPointY * scale, stick.rotationPointZ * scale);
        GL11.glScaled(0.3, 1.0, 0.3);
        GL11.glTranslatef(-stick.offsetX, -stick.offsetY, -stick.offsetZ);
        GL11.glTranslatef(-stick.rotationPointX * scale, -stick.rotationPointY * scale, -stick.rotationPointZ * scale);
        stick.render(scale);
        GL11.glPopMatrix();
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
