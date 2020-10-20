package com.fiskmods.heroes;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fiskmods.heroes.asm.SHLoadingPlugin;
import com.fiskmods.heroes.client.SHFileResourcePack;
import com.fiskmods.heroes.client.SHFolderResourcePack;
import com.fiskmods.heroes.common.config.RuleHandler;
import com.fiskmods.heroes.common.hero.LegacyHeroManager;
import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.WorldAccessContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class FiskHeroesCore extends DummyModContainer implements WorldAccessContainer
{
    public static final String MODID = "fiskheroescore";
    public static final String NAME = "Fisk's Superheroes Core";

    public FiskHeroesCore()
    {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = MODID;
        meta.name = NAME;
        meta.parent = FiskHeroes.MODID;
        meta.version = FiskHeroes.VERSION;
        meta.authorList = Arrays.asList("FiskFille");
    }

    @Override
    public Set<ArtifactVersion> getRequirements()
    {
        Set<ArtifactVersion> set = new HashSet<>();
        set.add(VersionParser.parseVersionReference("Forge@[10.13.4.1558,)"));
        return set;
    }

    @Override
    public List<ArtifactVersion> getDependencies()
    {
        return new LinkedList<>(getRequirements());
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange()
    {
        return VersionParser.parseRange("[1.7.10]");
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }

    @Override
    public File getSource()
    {
        return SHLoadingPlugin.source;
    }

    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return getSource().isDirectory() ? SHFolderResourcePack.class : SHFileResourcePack.class;
    }

    @Override
    public String getGuiClassName()
    {
        return "com.fiskmods.heroes.client.gui.SHGuiFactory";
    }

    @Override
    public Object getMod()
    {
        return this;
    }

    @Override
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info)
    {
        NBTTagCompound tag = new NBTTagCompound();
        LegacyHeroManager.INSTANCE.writeToNBT(tag);
        RuleHandler.INSTANCE.writeToNBT(tag);

        return tag;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound tag)
    {
        LegacyHeroManager.INSTANCE.readFromNBT(tag);
        RuleHandler.INSTANCE.readFromNBT(tag);
    }
}
