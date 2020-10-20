package com.fiskmods.heroes.client;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;

public class SHPlayerControllerMP extends PlayerControllerMP
{
    public SHPlayerControllerMP(Minecraft mc, NetHandlerPlayClient nethandlerplayclient)
    {
        super(mc, nethandlerplayclient);
    }

    @Override
    public float getBlockReachDistance()
    {
        return SHHelper.getReachDistance(FiskHeroes.proxy.getPlayer(), super.getBlockReachDistance());
    }
}
