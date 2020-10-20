package com.fiskmods.heroes.common.hero;

import cpw.mods.fml.common.eventhandler.Event;

public class RegisterHeroesEvent extends Event
{
    public final HeroRegistry heroRegistry;

    public RegisterHeroesEvent(HeroRegistry registry)
    {
        heroRegistry = registry;
    }
}
