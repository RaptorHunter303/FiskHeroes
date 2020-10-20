package com.fiskmods.heroes.common.entity.attribute;

import com.fiskmods.heroes.pack.JSHero.Attrib;

public interface IAttributeContainer
{
    void add(ArmorAttribute attribute, double amount, int operation);

    default void add(Attrib attrib)
    {
        add(attrib.getAttribute(), attrib.value, attrib.operator);
    }
}
