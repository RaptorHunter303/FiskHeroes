package com.fiskmods.heroes.client.sound;

import com.fiskmods.heroes.common.event.ClientEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.util.ResourceLocation;

public class PositionedSoundSarcastic extends SoundSH implements ITickableSound
{
    private final float pitch;

    public PositionedSoundSarcastic(ResourceLocation location, float volume, float pitch, boolean loop, int i, AttenuationType attenuationType, float x, float y, float z)
    {
        super(location, volume, pitch, loop, i, attenuationType, x, y, z);
        this.pitch = pitch;
    }

    @Override
    public boolean isDonePlaying()
    {
        return false;
    }

    @Override
    public void update()
    {
        float t = Minecraft.getMinecraft().thePlayer.ticksExisted + ClientEventHandler.renderTick;
        field_147663_c = pitch + (float) Math.sin(t * 1.5F) * 0.2F;
    }
}
