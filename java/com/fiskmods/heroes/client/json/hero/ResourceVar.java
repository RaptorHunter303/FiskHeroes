package com.fiskmods.heroes.client.json.hero;

import java.io.IOException;

import com.google.common.collect.ImmutableSet;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ResourceVar implements IResourceVar
{
    public final String path;
    public final ResourceLocation resource;

    public ResourceVar(String texture)
    {
        path = texture;
        resource = new ResourceLocation(TexturePath.format(texture));
    }

    @Override
    public void load(TextureManager manager)
    {
        manager.bindTexture(resource);
    }

    @Override
    public ResourceLocation get(Entity entity, ItemStack stack)
    {
        return resource;
    }

    @Override
    public ImmutableSet<ResourceLocation> getResources()
    {
        return ImmutableSet.of(resource);
    }

    public static ResourceVar read(JsonReader in) throws IOException
    {
        return in.peek() == JsonToken.STRING ? new ResourceVar(in.nextString()) : null;
    }
}
