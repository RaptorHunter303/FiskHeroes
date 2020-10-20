package com.fiskmods.heroes.client.render.equipment;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;

@Cancelable
public class RenderEquipmentEvent extends Event
{
    public final EntityPlayer player;
    public final EquipmentRenderer renderer;
    public float xOffset;
    public float yOffset;
    public float zOffset;

    public RenderEquipmentEvent(EntityPlayer player, EquipmentRenderer renderer, float xOffset, float yOffset, float zOffset)
    {
        this.player = player;
        this.renderer = renderer;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    public RenderEquipmentEvent(EntityPlayer player, EquipmentRenderer renderer, float[] offset)
    {
        this(player, renderer, offset[0], offset[1], offset[2]);
    }
}
