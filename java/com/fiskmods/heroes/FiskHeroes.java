package com.fiskmods.heroes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fiskmods.heroes.common.command.CommandFiskHeroes;
import com.fiskmods.heroes.common.command.CommandForceSlow;
import com.fiskmods.heroes.common.command.CommandSeeMartians;
import com.fiskmods.heroes.common.command.CommandSpeedXP;
import com.fiskmods.heroes.common.command.CommandStatEffect;
import com.fiskmods.heroes.common.command.CommandSuit;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.LegacyHeroManager;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.proxy.CommonProxy;
import com.fiskmods.heroes.pack.JSHeroesEngine;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

@Mod(modid = FiskHeroes.MODID, name = FiskHeroes.NAME, version = FiskHeroes.VERSION, guiFactory = "com.fiskmods.heroes.client.gui.SHGuiFactory", dependencies = "required-after:Forge@[10.13.4.1558,)")
public class FiskHeroes
{
    public static final String MODID = "fiskheroes";
    public static final String VERSION = "${version}";
    public static final String NAME = "Fisk's Superheroes";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final CreativeTabs TAB_SUITS = new CreativeTabFiskHeroes();
    public static final CreativeTabs TAB_ITEMS = new CreativeTabFiskHeroesItems();
    public static final CreativeTabs TAB_ARCHERY = new CreativeTabArchery();

    @SidedProxy(clientSide = "com.fiskmods.heroes.common.proxy.ClientProxy", serverSide = "com.fiskmods.heroes.common.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static boolean isBattlegearLoaded;

    @EventHandler
    public void construction(FMLConstructionEvent event)
    {
        try
        {
            Hero.REGISTRY.construct(event);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        SHConfig.register(event.getSuggestedConfigurationFile());
        isBattlegearLoaded = Loader.isModLoaded("battlegear2");
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandSpeedXP());
        event.registerServerCommand(new CommandSeeMartians());
        event.registerServerCommand(new CommandSuit());
        event.registerServerCommand(new CommandStatEffect());
        event.registerServerCommand(new CommandFiskHeroes());
        event.registerServerCommand(new CommandForceSlow());

        JSHeroesEngine.INSTANCE.init(Side.SERVER, event.getServer());
    }

    @EventHandler
    public void missingMappings(FMLMissingMappingsEvent event)
    {
        LegacyHeroManager.INSTANCE.missingMappings(event);

        for (MissingMapping mapping : event.get())
        {
            // 1.1.0
            remap(mapping, "the_flash_ring", ModItems.flashRing);

            // 1.1.4
            discard(mapping, "reverse_flash_emblem");

            // 1.1.5
            discard(mapping, "the_flash_emblem");
            discard(mapping, "flash_emblem");
        }
    }

    private void remap(MissingMapping mapping, String name, Object obj)
    {
        if (mapping.name.equals(FiskHeroes.MODID + ":" + name))
        {
            if (obj instanceof Block)
            {
                mapping.remap((Block) obj);
            }
            else
            {
                mapping.remap((Item) obj);
            }
        }
    }

    private void discard(MissingMapping mapping, String name)
    {
        if (mapping.name.equals(FiskHeroes.MODID + ":" + name))
        {
            mapping.ignore();
        }
    }
}
