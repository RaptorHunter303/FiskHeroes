package com.fiskmods.heroes.common.world.structure;

import net.minecraft.world.World;

public enum StructureType
{
    NEXUS("eternity_nexus", StructureNexus::new, 48, 64);

    public static final String[] NAMES;

    public final String key;
    private final Constructor constructor;

    public int minDistance;
    public int maxDistance;

    private StructureType(String s, Constructor c, int min, int max)
    {
        key = s;
        constructor = c;
        minDistance = min;
        maxDistance = max;
    }

    public Structure construct(World world)
    {
        return constructor.construct(world);
    }

    static
    {
        NAMES = new String[values().length];

        for (int i = 0; i < NAMES.length; ++i)
        {
            NAMES[i] = values()[i].key;
        }
    }

    private interface Constructor
    {
        Structure construct(World world);
    }
}
