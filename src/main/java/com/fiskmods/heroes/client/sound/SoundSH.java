package com.fiskmods.heroes.client.sound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class SoundSH extends PositionedSound
{
    public static SoundSH makeSound(ResourceLocation resourceLocation, boolean loop)
    {
        SoundSH sound = new SoundSH(resourceLocation, 1.0F, 1.0F, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
        sound.repeat = loop;
        return sound;
    }

    public static SoundSH makeSound(ResourceLocation resourceLocation, boolean loop, float volume, float pitch)
    {
        return new SoundSH(resourceLocation, volume, pitch, loop, 0, AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
    }

    public static SoundSH makeSound(ResourceLocation resourceLocation, boolean loop, float x, float y, float z)
    {
        return new SoundSH(resourceLocation, 1.0F, 1.0F, loop, 0, AttenuationType.LINEAR, x, y, z);
    }

    public static SoundSH makeSound(ResourceLocation resourceLocation, boolean loop, float x, float y, float z, float volume, float pitch, ISound.AttenuationType attenuationType)
    {
        return new SoundSH(resourceLocation, volume, pitch, loop, 0, attenuationType, x, y, z);
    }

    public SoundSH(ResourceLocation location, float volume, float pitch, boolean loop, int repeatDelay, ISound.AttenuationType attenuationType, float x, float y, float z)
    {
        super(location);
        this.volume = volume;
        field_147663_c = pitch;
        xPosF = x;
        yPosF = y;
        zPosF = z;
        repeat = loop;
        field_147665_h = repeatDelay;
        field_147666_i = attenuationType;
    }
}
