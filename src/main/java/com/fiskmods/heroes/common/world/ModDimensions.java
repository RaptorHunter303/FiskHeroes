package com.fiskmods.heroes.common.world;

import net.minecraftforge.common.DimensionManager;

public class ModDimensions
{
    public static final int QUANTUM_REALM_ID = 31;

    public static void register()
    {
        DimensionManager.registerProviderType(QUANTUM_REALM_ID, WorldProviderQuantumRealm.class, true);
        DimensionManager.registerDimension(QUANTUM_REALM_ID, QUANTUM_REALM_ID);
    }
}
