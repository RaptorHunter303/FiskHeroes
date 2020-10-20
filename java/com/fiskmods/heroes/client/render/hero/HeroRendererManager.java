package com.fiskmods.heroes.client.render.hero;

import com.fiskmods.heroes.FiskHeroes;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public enum HeroRendererManager
{
    INSTANCE;

    @SubscribeEvent
    public void onRegister(RegisterHeroRendererEvent event)
    {
        // FIXME: Migrate these to JSON
        register("martian_manhunter", new HeroRendererMartianManhunter());
        register("martian_manhunter_comics", new HeroRendererMartianManhunter());
    }

    private void register(String key, HeroRenderer value)
    {
        HeroRenderer.register(FiskHeroes.MODID + ":" + key, value);
    }
}
