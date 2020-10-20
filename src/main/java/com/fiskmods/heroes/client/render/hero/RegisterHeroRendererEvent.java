package com.fiskmods.heroes.client.render.hero;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.resources.IResourceManager;

public class RegisterHeroRendererEvent extends Event
{
    public final IResourceManager resourceManager;

    public RegisterHeroRendererEvent(IResourceManager manager)
    {
        resourceManager = manager;
    }
}
