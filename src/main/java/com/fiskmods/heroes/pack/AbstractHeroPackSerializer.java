package com.fiskmods.heroes.pack;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.pack.HeroPack.HeroPackException;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractHeroPackSerializer
{
    static final byte PACK = 0;
    static final byte HERO = 1;

    abstract void putData(byte fileType, Object key, Object value);

    abstract void accept(String domain, ZipFile zip, ZipEntry zipentry) throws IOException;

    abstract void accept(String domain, File file) throws IOException;

    public abstract void toBytes(ByteBuf buf) throws IOException;

    public abstract void fromBytes(ByteBuf buf) throws IOException;

    public abstract int reload(Map iterations, Map remaps, Map<ResourceLocation, Hero> heroes, Set<ResourceLocation> nonnull) throws HeroPackException;
}
