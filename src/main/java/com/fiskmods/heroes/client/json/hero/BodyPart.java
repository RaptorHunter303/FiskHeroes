package com.fiskmods.heroes.client.json.hero;

import java.util.function.Function;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public enum BodyPart
{
    HEAD("head", m -> m.bipedHead),
    HEADWEAR("headwear", m -> m.bipedHeadwear),
    BODY("body", m -> m.bipedBody),
    RIGHT_ARM("rightArm", m -> m.bipedRightArm),
    LEFT_ARM("leftArm", m -> m.bipedLeftArm),
    RIGHT_LEG("rightLeg", m -> m.bipedRightLeg),
    LEFT_LEG("leftLeg", m -> m.bipedLeftLeg);

    public final String name;
    private final Function<ModelBiped, ModelRenderer> part;

    BodyPart(String s, Function<ModelBiped, ModelRenderer> func)
    {
        name = s;
        part = func;
    }

    public ModelRenderer getPart(ModelBiped model)
    {
        return part.apply(model);
    }

    public static BodyPart get(String name)
    {
        for (BodyPart part : values())
        {
            if (name.equals(part.name))
            {
                return part;
            }
        }

        return null;
    }
}
