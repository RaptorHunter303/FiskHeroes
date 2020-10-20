package com.fiskmods.heroes.util;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.common.util.EnumHelper;

public class SHEnumHelper extends EnumHelper
{
    private static Class[][] commonTypes = {{ItemRenderType.class}, {EnumEnchantmentType.class}};

    public static final ItemRenderType EQUIPPED_OFFHAND = addItemRenderType("EQUIPPED_OFFHAND");
    public static final ItemRenderType EQUIPPED_FIRST_PERSON_OFFHAND = addItemRenderType("EQUIPPED_FIRST_PERSON_OFFHAND");

    public static final EnumEnchantmentType ENCH_SHIELD = addEnchantmentType("FISK_SHIELD");
    public static final EnumEnchantmentType ENCH_QUIVER = addEnchantmentType("FISK_QUIVER");

    public static ItemRenderType addItemRenderType(String name)
    {
        return addEnum(ItemRenderType.class, name);
    }

    public static EnumEnchantmentType addEnchantmentType(String name)
    {
        return addEnum(EnumEnchantmentType.class, name);
    }

    public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName, Object... paramValues)
    {
        return addEnum(commonTypes, enumType, enumName, paramValues);
    }
}
