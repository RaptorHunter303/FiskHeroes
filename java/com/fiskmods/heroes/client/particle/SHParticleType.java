package com.fiskmods.heroes.client.particle;

import java.util.concurrent.Callable;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public enum SHParticleType
{
    FLAME(() -> EntitySHFlameFX.class),
    SHORT_FLAME(() -> EntitySHShortFlameFX.class),
    FREEZE_RAY(() -> EntitySHFreezeRayFX.class),
    REPULSOR_BLAST(() -> EntitySHRepulsorBlastFX.class),
    QUANTUM_PARTICLE(() -> EntitySHQuantumParticleFX.class),
    BLUE_FLAME(() -> EntitySHBlueFlameFX.class),
    ICICLE_BREAK(() -> EntitySHIcicleBreakFX.class),
    FREEZE_SMOKE(() -> EntitySHFreezeSmokeFX.class),
    THICK_SMOKE(() -> EntitySHThickSmokeFX.class),
    SUBATOMIC_CHARGE(() -> EntitySHSubatomicChargeFX.class),
    SHADOW_SMOKE(() -> EntitySHShadowSmokeFX.class),
    ETERNIUM_AURA(() -> EntitySHEterniumAuraFX.class);

    final Callable<Class> particleClass;

    SHParticleType(Callable<Class> c)
    {
        particleClass = c;
    }

    public void spawn(double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        Side side = FMLCommonHandler.instance().getSide();

        if (side.isClient())
        {
            SHParticlesClient.spawnParticleClient(this, x, y, z, motionX, motionY, motionZ);
        }
    }
}
