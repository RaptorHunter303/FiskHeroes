package com.fiskmods.heroes.common.hero.modifier;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.equipment.EnumEquipment;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;

public class AbilityEquipment extends Ability
{
    public static final String KEY_SWITCH = "UTILITY_SWITCH";
    public static final String KEY_RESET = "UTILITY_RESET";

    protected List<EnumEquipment> types = new ArrayList<>();

    public AbilityEquipment(int tier)
    {
        super(tier);
    }

    public AbilityEquipment add(EnumEquipment... gadgets)
    {
        types.addAll(ImmutableList.copyOf(gadgets));
        return this;
    }

    public boolean includes(EnumEquipment gadget)
    {
        return types.contains(gadget);
    }

    public EnumEquipment[] get()
    {
        return types.toArray(new EnumEquipment[types.size()]);
    }

    @Override
    public boolean renderIcon(EntityPlayer player)
    {
        return SHData.UTILITY_BELT_TYPE.get(player) > -1;
    }
}
