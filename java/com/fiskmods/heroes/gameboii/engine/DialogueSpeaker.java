package com.fiskmods.heroes.gameboii.engine;

import java.util.function.Supplier;

import com.fiskmods.heroes.gameboii.batfish.level.BatfishPlayer;
import com.fiskmods.heroes.gameboii.graphics.Resource;

public class DialogueSpeaker
{
    public final String name;
    public final Supplier<Resource> resource;

    public DialogueSpeaker(String name, Supplier<Resource> resource)
    {
        this.name = name;
        this.resource = resource;
    }

    public DialogueSpeaker(String name, Resource resource)
    {
        this(name, () -> resource);
    }

    public DialogueSpeaker(BatfishPlayer.Skin skin)
    {
        this(skin.name, skin.getResource());
    }
}
