package com.fiskmods.heroes.client.render.hero.effect;

import com.fiskmods.heroes.FiskHeroes;

public class HeroEffectManager
{
    public static void register()
    {
        register("cape", HeroEffectCape.class);
        register("chest", HeroEffectChest.class);
        register("trail", HeroEffectTrail.class);
        register("sombrero", HeroEffectSombrero.class);
        register("flames", HeroEffectFlames.class);
        register("overlay", HeroEffectOverlay.class);
        register("wings", HeroEffectWings.class);
        register("ears", HeroEffectEars.class);
        register("metal_heat", HeroEffectMetalHeat.class);
        register("archery", HeroEffectArchery.class);
        register("vibration", HeroEffectVibration.class);
        register("deadpool_sheath", HeroEffectDeadpoolSheath.class);
        register("prometheus_sheath", HeroEffectPrometheusSheath.class);
        register("heat_vision", HeroEffectHeatVision.class);
        register("propelled_flight", HeroEffectPropelledFlight.class);
        register("lightning_attack", HeroEffectLightningAttack.class);
        register("antennae", HeroEffectAntennae.class);
        register("shield", HeroEffectShield.class);
        register("opening_mask", HeroEffectOpeningMask.class);
        register("invisibility", HeroEffectInvisibility.class);
        register("energy_projection", HeroEffectEnergyProj.class);
        register("teleportation", HeroEffectTeleportation.class);
        register("particle_cloud", HeroEffectParticleCloud.class);
        register("energy_bolt", HeroEffectEnergyBolt.class);
        register("spell", HeroEffectSpell.class);
        register("arm_animation", HeroEffectArmAnimation.class);
        register("repulsor_blast", HeroEffectRepulsorBlast.class);
        register("spellcasting", HeroEffectSpellcasting.class);
        register("jetpack", HeroEffectJetpack.class);
    }

    private static void register(String key, Class<? extends HeroEffect> value)
    {
        HeroEffect.register(FiskHeroes.MODID + ":" + key, value);
    }
}
