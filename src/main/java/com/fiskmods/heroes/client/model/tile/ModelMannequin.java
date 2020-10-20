package com.fiskmods.heroes.client.model.tile;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelMannequin extends ModelBiped
{
    public ModelMannequin(float f, boolean mirror)
    {
        this(0, f, mirror);
    }

    public ModelMannequin(float scale, float f, boolean mirror)
    {
        this(scale, 0, f, 64, 32, mirror);
    }

    public ModelMannequin(float scale, float f, float f1, int width, int height, boolean mirror)
    {
        super(scale, f, width, height);
        textureWidth = width;
        textureHeight = height;
        bipedCloak = new ModelRenderer(this, 0, 0);
        bipedCloak.addBox(-5, 0, -1, 10, 16, 1, scale);
        bipedEars = new ModelRenderer(this, 24, 0);
        bipedEars.addBox(-3, -6, -1, 6, 6, 1, scale);
        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHead.addBox(-4, -8, -4, 8, 8, 8, scale);
        bipedHead.setRotationPoint(0, 0 + f, 0);
        bipedHeadwear = new ModelRenderer(this, 32, 0);
        bipedHeadwear.addBox(-4, -8, -4, 8, 8, 8, scale + f1);
        bipedHeadwear.setRotationPoint(0, 0 + f, 0);
        bipedBody = new ModelRenderer(this, 16, 16);
        bipedBody.addBox(-4, 0, -2, 8, 12, 4, scale);
        bipedBody.setRotationPoint(0, 0 + f, 0);
        bipedRightArm = new ModelRenderer(this, 40, 16);
        bipedRightArm.addBox(-3, -2, -2, 4, 12, 4, scale);
        bipedRightArm.setRotationPoint(-5, 2 + f, 0);
        bipedLeftArm = new ModelRenderer(this, 40, 16);
        bipedLeftArm.mirror = mirror;
        bipedLeftArm.addBox(-1, -2, -2, 4, 12, 4, scale);
        bipedLeftArm.setRotationPoint(5, 2 + f, 0);
        bipedRightLeg = new ModelRenderer(this, 0, 16);
        bipedRightLeg.addBox(-2, 0, -2, 4, 12, 4, scale);
        bipedRightLeg.setRotationPoint(-1.9F, 12 + f, 0);
        bipedLeftLeg = new ModelRenderer(this, 0, 16);
        bipedLeftLeg.mirror = mirror;
        bipedLeftLeg.addBox(-2, 0, -2, 4, 12, 4, scale);
        bipedLeftLeg.setRotationPoint(1.9F, 12 + f, 0);
    }
}
