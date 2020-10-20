package com.fiskmods.heroes.pack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.pack.HeroPack.HeroPackException;
import com.fiskmods.heroes.util.FileHelper;
import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;

public class HeroPackSerializer extends AbstractHeroPackSerializer implements IAssetSnooper
{
    public final HeroPackSnooper snooper;
    private Map<Object, Object>[] data = new Map[2];

    public HeroPackSerializer(HeroPackSnooper snooper)
    {
        this.snooper = snooper;
    }

    public HeroPackSerializer()
    {
        this(new HeroPackSnooper());
    }

    @Override
    void putData(byte fileType, Object key, Object value)
    {
        if (data[fileType] == null)
        {
            data[fileType] = new HashMap<>();
        }

        data[fileType].put(key, value);
    }

    @Override
    void accept(String domain, ZipFile zip, ZipEntry zipentry) throws IOException
    {
        snooper.accept(domain, zip, zipentry);
    }

    @Override
    void accept(String domain, File file) throws IOException
    {
        snooper.accept(domain, file);
    }

    @Override
    public void toBytes(ByteBuf buf) throws IOException
    {
        snooper.toBytes(buf);
        buf.writeBytes(FileHelper.compress(HeroPack.GSON.toJson(data), StandardCharsets.UTF_8));
    }

    @Override
    public void fromBytes(ByteBuf buf) throws IOException
    {
        snooper.fromBytes(buf);

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        data = HeroPack.GSON.fromJson(FileHelper.decompress(bytes, StandardCharsets.UTF_8), Map[].class);
    }

    @Override
    public int reload(Map iterations, Map remaps, Map<ResourceLocation, Hero> heroes, Set<ResourceLocation> nonnull) throws HeroPackException
    {
        if (data[PACK] != null)
        {
            for (Map.Entry e : data[PACK].entrySet())
            {
                Map map = HeroPack.GSON.fromJson(String.valueOf(e.getValue()), Map.class);
                Object obj = map.get("alts");

                if (obj instanceof Map)
                {
                    Hero.REGISTRY.processIterations((Map) obj, iterations);
                }

                obj = map.get("remap");

                if (obj instanceof Map)
                {
                    remaps.putAll((Map) obj);
                }
            }

            if (data[HERO] != null)
            {
                for (Map.Entry e : data[HERO].entrySet())
                {
                    ResourceLocation id = new ResourceLocation(String.valueOf(e.getKey()));

                    if (!nonnull.contains(id))
                    {
                        String raw = String.valueOf(e.getValue());

                        try
                        {
                            heroes.put(id, Preconditions.checkNotNull(JSHeroesEngine.INSTANCE.loadHeroRaw(id, raw)));
                        }
                        catch (Exception e1)
                        {
                            throw new HeroPackException("Client-side loading Hero " + id + " failed!", e1);
                        }
                    }
                }
            }

            return data[PACK].size();
        }

        return 0;
    }

    @Override
    public Map<ResourceLocation, Callable<InputStream>> getAssets()
    {
        return snooper.getAssets();
    }

    @Override
    public Set<String> getResourceDomains()
    {
        return snooper.getResourceDomains();
    }
}
