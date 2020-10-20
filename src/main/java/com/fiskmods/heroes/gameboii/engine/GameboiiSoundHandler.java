package com.fiskmods.heroes.gameboii.engine;

import java.nio.ByteBuffer;

import com.fiskmods.heroes.gameboii.IGameboiiSaveObject;
import com.fiskmods.heroes.gameboii.engine.GameboiiSound.Category;

public class GameboiiSoundHandler implements IGameboiiSaveObject
{
    private float[] volumes = new float[Category.values().length];

    public GameboiiSoundHandler()
    {
        for (int i = 0; i < volumes.length; ++i)
        {
            volumes[i] = 1;
        }
    }

    @Override
    public void read(ByteBuffer buf, int protocol)
    {
        for (int i = 0; i < volumes.length; ++i)
        {
            volumes[i] = (buf.get() & 0xFF) / 255F;
        }
    }

    @Override
    public void write(ByteBuffer buf)
    {
        for (float volume : volumes)
        {
            buf.put((byte) (volume * 255));
        }
    }

    public float getVolume(Category category)
    {
        return volumes[category.ordinal()];
    }

    public void setVolume(Category category, float volume)
    {
        volumes[category.ordinal()] = volume;
    }
}
