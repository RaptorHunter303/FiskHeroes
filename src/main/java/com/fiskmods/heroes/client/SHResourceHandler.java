package com.fiskmods.heroes.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.client.json.cloud.JsonCloud;
import com.fiskmods.heroes.client.json.hero.JsonHeroRenderer;
import com.fiskmods.heroes.client.json.hero.ResourceVarTx;
import com.fiskmods.heroes.client.json.shape.JsonShape;
import com.fiskmods.heroes.client.json.trail.JsonTrail;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.RegisterHeroRendererEvent;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.item.ItemMetahumanLog;
import com.fiskmods.heroes.pack.HeroTextureMap;
import com.fiskmods.heroes.pack.JSHeroesEngine;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ProgressManager;
import cpw.mods.fml.common.ProgressManager.ProgressBar;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public enum SHResourceHandler implements IResourceManagerReloadListener
{
    INSTANCE;

    private static final ResourceLocation MISSING_MODEL_LOC = new ResourceLocation(FiskHeroes.MODID, "missing_model");
    private static final String MISSING_MODEL = "{\"texture\":{\"default\":\"missingno\"},\"showModel\":{\"head\":[\"HELMET\"],\"headwear\":[\"HELMET\"],\"body\":[\"CHESTPLATE\"],\"rightArm\":[\"CHESTPLATE\"],\"leftArm\":[\"CHESTPLATE\"],\"rightLeg\":[\"LEGGINGS\",\"BOOTS\"],\"leftLeg\":[\"LEGGINGS\",\"BOOTS\"]},\"fixHatLayer\":[\"HELMET\"]}";
    private static final String MISSING_TRAIL = "{\"fade\":10,\"lightning\":{\"color\":0xFF4D00,\"density\":6,\"differ\":0.435}}";

    private final WeakHashMap<Entity, Map<ItemStack, Map<ResourceVarTx, ResourceLocation>>> resourceValues = new WeakHashMap<>();
    private final Set<ResourceLocation> resources = new HashSet<>();

    private JsonHeroRenderer missingRenderer;
    private JsonTrail trailV9;

    public boolean freezeLoading = true;
    public boolean initialized;

    public static void listen(ResourceLocation location)
    {
        INSTANCE.resources.add(location);
    }

    public static void listen(Collection<ResourceLocation> locations)
    {
        INSTANCE.resources.addAll(locations);
    }

    public static ResourceLocation compute(ResourceVarTx tx, Entity entity, ItemStack stack)
    {
        return INSTANCE.resourceValues.computeIfAbsent(entity, k -> new HashMap<>()).computeIfAbsent(stack, k -> new HashMap<>()).computeIfAbsent(tx, k -> k.compute(entity, stack));
    }

    public static void register()
    {
        FMLCommonHandler.instance().bus().register(SHResourceHandler.INSTANCE);
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(SHResourceHandler.INSTANCE);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase == Phase.END)
        {
            resourceValues.values().forEach(Map::clear);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager manager)
    {
        onResourceManagerReload(manager, false);
    }

    public void onResourceManagerReload(IResourceManager manager, boolean packs)
    {
        if (freezeLoading)
        {
            return;
        }
        else if (!initialized)
        {
            initialized = true;
            packs = true;
        }

        if (packs)
        {
            clearCaches();
            clearTextures();

            loadModels(manager);
            loadTextures(manager);
            loadV9Trail(manager);

            if (HeroTextureMap.getInstance() != null)
            {
                try
                {
                    HeroTextureMap.getInstance().loadTexture(manager);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            clearCaches();
        }
//        else
//        {
//            JSHeroesEngine.INSTANCE.reloadResources();
//        }

        ItemMetahumanLog.clearCache(FiskHeroes.MODID);
    }

    public void loadModels(IResourceManager manager)
    {
        HeroRenderer.REGISTRY.clear();
        MinecraftForge.EVENT_BUS.post(new RegisterHeroRendererEvent(manager));

        Gson gson = JsonHeroRenderer.GSON_FACTORY.create();
        ProgressBar bar = ProgressManager.push("Loading Hero models", HeroIteration.getKeys().size() + 1, true);

        bar.step("Default Model");
        HeroRenderer.DEFAULT.json = loadDefaultModel(gson, manager);

        for (HeroIteration iter : HeroIteration.getValues())
        {
            HeroRenderer renderer = HeroRenderer.get(iter);
            ResourceLocation name = iter.getRegistryName();
            Thread loader = new Thread(() ->
            {
                try
                {
                    if (renderer != HeroRenderer.DEFAULT)
                    {
                        renderer.json = JsonHeroRenderer.read(gson, manager, name);
                    }
                }
                catch (IOException e)
                {
                    renderer.json = null;
                    e.printStackTrace();
                }
            }, "Hero Model Loader");

            bar.step(iter.toString());
            loader.start();

            try
            {
                loader.join(SHConfig.heroRendererTimeout);
            }
            catch (InterruptedException e)
            {
            }

            if (loader.isAlive())
            {
                JSHeroesEngine.LOGGER.warn("Hero model loader thread '{}' took too long to respond - infinite loop?", name);
                loader.interrupt();
            }

            if (renderer.json == null)
            {
                JSHeroesEngine.LOGGER.warn("Using fallback Hero model for {}", name);
                renderer.json = missingRenderer;
            }
        }

        ProgressManager.pop(bar);
    }

    public JsonHeroRenderer loadDefaultModel(Gson gson, IResourceManager manager)
    {
        if (missingRenderer == null)
        {
            try
            {
                missingRenderer = Preconditions.checkNotNull(JsonHeroRenderer.read(gson, manager, new StringReader(MISSING_MODEL), MISSING_MODEL_LOC));
            }
            catch (Exception e)
            {
                throw new RuntimeException("An error was caught while reading the fallback Hero model, something has gone horribly, horribly wrong! (Please contact your local FiskFille.)", e);
            }
        }

        return missingRenderer;
    }

    public void loadTextures(IResourceManager manager)
    {
        ProgressBar bar = ProgressManager.push("Loading Hero textures", HeroRenderer.REGISTRY.size(), true);

        for (Map.Entry<String, HeroRenderer> e : HeroRenderer.REGISTRY.entrySet())
        {
            bar.step(e.getKey());

            try
            {
                e.getValue().loadTextures(manager);
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }

        ProgressManager.pop(bar);
    }

    public void loadV9Trail(IResourceManager manager)
    {
        ResourceLocation name = new ResourceLocation(FiskHeroes.MODID, "velocity_nine");
        trailV9 = null;

        try
        {
            trailV9 = JsonTrail.read(JsonTrail.GSON_FACTORY.create(), manager, name);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            JSHeroesEngine.LOGGER.warn("Using fallback trail renderer for {}", name);

            try
            {
                trailV9 = JsonTrail.read(JsonTrail.GSON_FACTORY.create(), manager, new StringReader(MISSING_TRAIL), name);
            }
            catch (Exception e1)
            {
                throw new RuntimeException("An error was caught while reading the fallback trail renderer, something has gone horribly, horribly wrong! (Please contact your local FiskFille.)", e1);
            }
        }
    }

    public void clearCaches()
    {
        JsonHeroRenderer.CACHE.clear();
        JsonTrail.CACHE.clear();
        JsonCloud.CACHE.clear();
        JsonShape.CACHE.clear();
    }

    public void clearTextures()
    {
        Map map = SHReflection.mapTextureObjectsField.get(Minecraft.getMinecraft().getTextureManager());

        for (ResourceLocation location : resources)
        {
            map.remove(location);
        }

        resources.clear();
    }

    public static JsonHeroRenderer getDefaultRenderer()
    {
        return INSTANCE.missingRenderer;
    }

    public static JsonTrail getV9Trail()
    {
        return INSTANCE.trailV9;
    }
}
