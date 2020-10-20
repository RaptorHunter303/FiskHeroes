package com.fiskmods.heroes.common.equipment;

import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraft.client.Minecraft;

public enum EquipmentHandler
{
    INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();

    public static int[] useCooldown = new int[EnumEquipment.values().length];
    public static int[] prevUseCooldown = new int[useCooldown.length];
    public static int[] timesUsed = new int[useCooldown.length];

    public static final int FADE_MAX = 25;
    public static int fade;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            for (int i = 0; i < useCooldown.length; ++i)
            {
                prevUseCooldown[i] = useCooldown[i];

                if (useCooldown[i] > 0)
                {
                    --useCooldown[i];
                }

                if (useCooldown[i] == 0 && prevUseCooldown[i] == 1)
                {
                    timesUsed[i] = 0;
                }
            }

            if (fade > 0)
            {
                --fade;
            }
        }
    }

    public static float getCooldownCompletion(EnumEquipment type)
    {
        return 1 - SHRenderHelper.interpolate(useCooldown[type.ordinal()], prevUseCooldown[type.ordinal()]) / type.useCooldown;
    }
}
