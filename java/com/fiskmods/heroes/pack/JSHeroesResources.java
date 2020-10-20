package com.fiskmods.heroes.pack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.client.SHResourceHandler;

import cpw.mods.fml.common.ProgressManager;
import cpw.mods.fml.common.ProgressManager.ProgressBar;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;

@SideOnly(Side.CLIENT)
public enum JSHeroesResources
{
    INSTANCE;

    private static Minecraft mc = Minecraft.getMinecraft();
    private static int lastWorld;

    public static boolean doClientSync()
    {
        int world = mc.theWorld.hashCode();

        if (lastWorld != world)
        {
            lastWorld = world;
            return true;
        }

        return false;
    }

    public static void syncResources(HeroPackSerializer serializer)
    {
        if (!JSHeroesEngine.INSTANCE.requiresResourceReload)
        {
            JSHeroesEngine.INSTANCE.packResources = serializer;
            JSHeroesEngine.INSTANCE.reloadResources();
        }
    }

    private void reload(List<IResourcePack> packs)
    {
        SimpleReloadableResourceManager manager = (SimpleReloadableResourceManager) mc.getResourceManager();
        ProgressBar bar = ProgressManager.push("Loading Resources", packs.size() + 1, true);
        Iterator<IResourcePack> iter = packs.iterator();

        SHReflection.clearResources(manager);

        for (IResourcePack pack; iter.hasNext() && (pack = iter.next()) != null;)
        {
            bar.step(pack.getPackName());
            manager.reloadResourcePack(pack);
        }

        bar.step("Reloading listeners");
        SHResourceHandler.INSTANCE.onResourceManagerReload(manager, true);
        ProgressManager.pop(bar);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase == Phase.END)
        {
            if (JSHeroesEngine.INSTANCE.requiresResourceReload)
            {
                ResourcePackRepository repo = mc.getResourcePackRepository();
                List defPacks = SHReflection.defaultResourcePacksField.get(mc);

                Iterator<ResourcePackRepository.Entry> iter = repo.getRepositoryEntries().iterator();
                List<IResourcePack> packs = new ArrayList<>(defPacks);

                while (iter.hasNext())
                {
                    packs.add(iter.next().getResourcePack());
                }

                if (repo.func_148530_e() != null)
                {
                    packs.add(repo.func_148530_e());
                }

                try
                {
                    reload(packs);
                }
                catch (RuntimeException e)
                {
                    JSHeroesEngine.LOGGER.info("Caught error stitching, removing all assigned resourcepacks", e);
                    packs.clear();
                    packs.addAll(defPacks);
                    repo.func_148527_a(Collections.emptyList());
                    reload(packs);
                    mc.gameSettings.resourcePacks.clear();
                    mc.gameSettings.saveOptions();
                }

                JSHeroesEngine.INSTANCE.requiresResourceReload = false;
            }
        }
    }
}
