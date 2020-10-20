package com.fiskmods.heroes.common.entity.attribute;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public class AttributeStepHeight extends ArmorAttribute
{
    public AttributeStepHeight(String unlocalizedName, double defaultValue, boolean additive)
    {
        super(unlocalizedName, defaultValue, additive);
    }

    public AttributeStepHeight(String unlocalizedName, boolean additive)
    {
        super(unlocalizedName, additive);
    }

    @Override
    public void reset(EntityPlayer player, IAttributeInstance instance, UUID uuid)
    {
        super.reset(player, instance, uuid);
        player.stepHeight = 0.5F;
    }
}
