package com.fiskmods.heroes.pack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import net.minecraft.util.ResourceLocation;

public interface IAssetSnooper
{
    Pattern PATTERN = Pattern.compile("^assets\\/([a-z_0-9]+)\\/.+\\..+$");

    Map<ResourceLocation, Callable<InputStream>> getAssets();

    Set<String> getResourceDomains();

    default void storeData(ResourceLocation location, InputStream in) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int b = 0;

        while ((b = in.read(buffer)) > 0)
        {
            out.write(buffer, 0, b);
        }

        storeData(location, out.toByteArray());
    }

    default void storeData(ResourceLocation location, byte[] bytes) throws IOException
    {
        getAssets().put(location, () -> new ByteArrayInputStream(bytes));
        getResourceDomains().add(location.getPath());
        JSHeroesEngine.LOGGER.debug("Detecting HeroPack assets: {}", location);
    }

    default ResourceLocation toLocation(String domain, ZipEntry zipentry)
    {
        return new ResourceLocation(domain, zipentry.getName().substring(("assets/" + domain).length() + 1));
    }

    interface IOConsumer<T>
    {
        void accept(T t) throws IOException;
    }
}
