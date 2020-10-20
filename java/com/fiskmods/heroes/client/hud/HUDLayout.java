package com.fiskmods.heroes.client.hud;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.fiskmods.heroes.client.gui.GuiGameboii;
import com.fiskmods.heroes.common.Pair;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public abstract class HUDLayout extends Gui
{
    protected Minecraft mc = Minecraft.getMinecraft();

    private final Map<Class, Pair<HUDElement, HUDBounds>> elements = new HashMap<>();

    public void put(HUDElement element, Function<ScreenInfo, Integer> x, Function<ScreenInfo, Integer> y, Function<ScreenInfo, Integer> w, Function<ScreenInfo, Integer> h)
    {
        put(element, new HUDBounds(x, y, w, h));
    }

    public void put(HUDElement element, Function<ScreenInfo, Integer> x, Function<ScreenInfo, Integer> y)
    {
        put(element, x, y, null, null);
    }

    public void put(HUDElement element, HUDBounds bounds)
    {
        elements.put(element.getClass(), Pair.of(element, bounds));
    }

    public boolean hasElement(Class<? extends HUDElement> c)
    {
        return elements.containsKey(c);
    }

    public <T extends HUDElement> T getElement(Class<T> c)
    {
        if (!hasElement(c))
        {
            return null;
        }

        return (T) elements.get(c).getKey();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event)
    {
        ScreenInfo i = new ScreenInfo(event.resolution);
        elements.values().stream().filter(e -> e.getKey().isVisible(event.type)).forEach(p ->
        {
            p.getKey().setBounds(p.getValue(), i);
            p.getKey().preRender(event.type, i, event.mouseX, event.mouseY, event.partialTicks);
        });

        if (mc.currentScreen instanceof GuiGameboii && event.type == ElementType.CROSSHAIRS)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event)
    {
        ScreenInfo i = new ScreenInfo(event.resolution);
        elements.values().stream().filter(p -> p.getKey().isVisible(event.type)).forEach(e ->
        {
            e.getKey().setBounds(e.getValue(), i);
            e.getKey().postRender(event.type, i, event.mouseX, event.mouseY, event.partialTicks);
        });
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase == Phase.END && mc.thePlayer != null)
        {
            elements.values().forEach(p -> p.getKey().updateTick());
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event)
    {
        if (mc.currentScreen == null && mc.thePlayer != null)
        {
            elements.values().forEach(p -> p.getKey().keyPress());
        }
    }
}
