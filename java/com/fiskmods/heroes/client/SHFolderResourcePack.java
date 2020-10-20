package com.fiskmods.heroes.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import com.fiskmods.heroes.pack.IHeroResourcePack;

import cpw.mods.fml.client.FMLFolderResourcePack;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.util.ResourceLocation;

public class SHFolderResourcePack extends FMLFolderResourcePack implements IHeroResourcePack
{
    public SHFolderResourcePack(ModContainer container)
    {
        super(container);
    }

    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException
    {
        return getInputStream(location, () -> super.getInputStream(location));
    }

    @Override
    public boolean resourceExists(ResourceLocation location)
    {
        return IHeroResourcePack.super.resourceExists(location) || super.resourceExists(location);
    }

    @Override
    public Set getResourceDomains()
    {
        return getResourceDomains(super.getResourceDomains());
    }
}
