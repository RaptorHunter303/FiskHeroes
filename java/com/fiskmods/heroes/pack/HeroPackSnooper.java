package com.fiskmods.heroes.pack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.pack.HeroPack.HeroPackException;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;

public class HeroPackSnooper extends AbstractHeroPackSerializer implements IAssetSnooper
{
    private final Map<ResourceLocation, Callable<InputStream>> assets = new HashMap<>();
    private final Set<String> domains = new HashSet<>();

    @Override
    public Map<ResourceLocation, Callable<InputStream>> getAssets()
    {
        return assets;
    }

    @Override
    public Set<String> getResourceDomains()
    {
        return domains;
    }

    @Override
    void putData(byte fileType, Object key, Object value)
    {
    }

    @Override
    void accept(String domain, ZipFile zip, ZipEntry zipentry) throws IOException
    {
        storeData(toLocation(domain, zipentry), zip.getInputStream(zipentry));
    }

    @Override
    void accept(String domain, File file) throws IOException
    {
        explore(domain, file, new File(file, "models/"));
        explore(domain, file, new File(file, "textures/"));
    }

    private void explore(String domain, File root, File file) throws IOException
    {
        if (file.isDirectory())
        {
            File[] files = file.listFiles();

            if (files != null)
            {
                for (File file1 : files)
                {
                    explore(domain, root, file1);
                }
            }
        }
        else if (file.isFile())
        {
            String s = file.getPath().substring(root.getPath().length() + 1);
            s = s.replace(File.separatorChar, '/');

            storeData(new ResourceLocation(domain, s), new FileInputStream(file));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) throws IOException
    {
        try
        {
            buf.writeShort(assets.size());

            for (Map.Entry<ResourceLocation, Callable<InputStream>> e : assets.entrySet())
            {
                InputStream in = e.getValue().call();
                byte[] bytes = IOUtils.toByteArray(in);

                ByteBufUtils.writeUTF8String(buf, e.getKey().toString());
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
                in.close();
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) throws IOException
    {
        int size = buf.readShort();

        for (int i = 0; i < size; ++i)
        {
            ResourceLocation location = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
            storeData(location, buf.readBytes(buf.readInt()).array());
        }
    }

    @Override
    public int reload(Map iterations, Map remaps, Map<ResourceLocation, Hero> heroes, Set<ResourceLocation> nonnull) throws HeroPackException
    {
        return 0;
    }
}
