package com.fiskmods.heroes.common.move;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

public class MoveEntry
{
    final Map<Double, Map<String, Number>> modifiers;

    public final Move move;
    public final ResourceLocation icon;

    MoveSet parent;

    MoveEntry(Move move, String icon, Map<Double, Map<String, Number>> modifiers)
    {
        this.move = move;
        this.modifiers = modifiers;
        this.icon = new ResourceLocation(icon);
    }

    void put(double after, String key, Number value)
    {
        modifiers.computeIfAbsent(after, k -> new HashMap<>()).put(key, value);
    }

    public MoveSet getParent()
    {
        return parent;
    }

    @Override
    public String toString()
    {
        return String.format("{\"%s\", modifiers=%s, icon=%s}", move, modifiers, icon);
    }
}
