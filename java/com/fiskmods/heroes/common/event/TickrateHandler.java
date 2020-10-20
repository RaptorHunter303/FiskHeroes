package com.fiskmods.heroes.common.event;

import com.fiskmods.heroes.common.Tickrate;
import com.fiskmods.heroes.common.data.world.SHMapData;
import com.fiskmods.heroes.util.SpeedsterHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public enum TickrateHandler
{
    INSTANCE;

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event)
    {
        if (event.phase == Phase.END)
        {
            tick(event.world, event.side);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.isGamePaused())
        {
            Tickrate.updateClientTickrate(20);
        }
        else if (mc.theWorld != null)
        {
            tick(mc.theWorld, Side.CLIENT);
        }
    }

    private void tick(World world, Side side)
    {
        SHMapData data = SHMapData.get(world);
        float tickrate = 20;
        float slow = 4;

        if (data.forceSlow > 0)
        {
            tickrate -= slow;
            tickrate *= 1 - data.forceSlow;
            tickrate += slow;
        }
        else if (SpeedsterHelper.areAllPlayersSlowMotion(world))
        {
            tickrate = slow;
        }

        Tickrate.updateTickrate(side, tickrate);
    }
}
