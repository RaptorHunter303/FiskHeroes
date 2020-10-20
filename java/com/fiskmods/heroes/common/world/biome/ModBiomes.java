package com.fiskmods.heroes.common.world.biome;

import net.minecraft.world.biome.BiomeGenBase;

public class ModBiomes
{
    public static final int QUANTUM_REALM_ID = 254;

    public static final BiomeGenBase QUANTUM_REALM = new BiomeGenQuantumRealm(QUANTUM_REALM_ID).setColor(0xFF00FF).setBiomeName("Quantum Realm").setDisableRain();
}
