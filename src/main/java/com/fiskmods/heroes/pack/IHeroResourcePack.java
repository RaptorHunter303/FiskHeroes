package com.fiskmods.heroes.pack;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import net.minecraft.util.ResourceLocation;

public interface IHeroResourcePack
{
    default InputStream getInputStream(ResourceLocation location, Callable<InputStream> c) throws IOException
    {
        if (JSHeroesEngine.INSTANCE.packResources != null && JSHeroesEngine.INSTANCE.packResources.getAssets().containsKey(location))
        {
            return callIO(JSHeroesEngine.INSTANCE.packResources.getAssets().get(location));
        }

        return callIO(c);
    }

    static <T> T callIO(Callable<T> c) throws IOException
    {
        try
        {
            return c.call();
        }
        catch (IOException | RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    default boolean resourceExists(ResourceLocation location)
    {
        return JSHeroesEngine.INSTANCE.packResources != null && JSHeroesEngine.INSTANCE.packResources.getAssets().containsKey(location);
    }

    default Set getResourceDomains(Set domains)
    {
        if (JSHeroesEngine.INSTANCE.packResources != null)
        {
            Set set = new HashSet<>(JSHeroesEngine.INSTANCE.packResources.getResourceDomains());

            if (domains != null)
            {
                set.addAll(domains);
            }

            return set;
        }

        return domains;
    }
}
