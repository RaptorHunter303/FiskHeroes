package com.fiskmods.heroes.client.json.hero;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IResourceVar
{
    void load(TextureManager manager);

    ResourceLocation get(Entity entity, ItemStack stack);

    ImmutableSet<ResourceLocation> getResources();
}
