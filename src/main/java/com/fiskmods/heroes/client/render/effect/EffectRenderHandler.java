package com.fiskmods.heroes.client.render.effect;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import com.fiskmods.heroes.client.render.effect.Effect.Entry;
import com.google.common.collect.Sets;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public enum EffectRenderHandler
{
    INSTANCE;

    private final WeakHashMap<Entity, WeakReference<EffectEntity>> entities = new WeakHashMap<>();
    private final WeakHashMap<Entity, List<Entry>> entries = new WeakHashMap<>();

    public static List<Entry> get(Entity anchor)
    {
        return INSTANCE.entries.computeIfAbsent(anchor, e -> new ArrayList<>());
    }

    public static void add(Entity anchor, Effect effect, int ticks)
    {
        List<Entry> list = get(anchor);

        if (list.isEmpty())
        {
            INSTANCE.entities.put(anchor, new WeakReference<>(new EffectEntity(anchor.worldObj, anchor)));
        }

        list.add(new Entry(ticks, effect));
    }

    public static void kill(Entity anchor, Predicate<Entry> pred)
    {
        get(anchor).removeIf(pred);

        if (get(anchor).isEmpty() && INSTANCE.entities.containsKey(anchor))
        {
            WeakReference<EffectEntity> w = INSTANCE.entities.get(anchor);
            EffectEntity e;

            if (w != null && (e = w.get()) != null)
            {
                e.setDead();
            }

            INSTANCE.entities.remove(anchor);
        }
    }

    public static void kill(Entity anchor, Entry entry)
    {
        kill(anchor, entry::equals);
    }

    public static void register()
    {
        FMLCommonHandler.instance().bus().register(INSTANCE);
        RenderingRegistry.registerEntityRenderingHandler(EffectEntity.class, new RenderEffectEntity());
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (event.phase == TickEvent.Phase.END && mc.theWorld != null && !mc.isGamePaused())
        {
//            ((List<Entity>) mc.theWorld.loadedEntityList).stream().filter(ArmorTracker::isTracking).forEach(entity ->
//            {
//                kill(entity, t -> t.tick(entity));
//            });

            for (Entity entity : Sets.newHashSet(entities.keySet()))
            {
                kill(entity, t -> t.tick(entity));
            }
        }
    }
}
