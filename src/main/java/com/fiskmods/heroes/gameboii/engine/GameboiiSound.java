package com.fiskmods.heroes.gameboii.engine;

import com.fiskmods.heroes.gameboii.Gameboii;

public class GameboiiSound
{
    public final String sound;
    public final Category category;

    public GameboiiSound(String sound, Category category)
    {
        this.sound = sound;
        this.category = category;
    }

    public enum Category
    {
        EFFECT("Sounds"),
        MUSIC("Music");

        public final String name;

        Category(String name)
        {
            this.name = name;
        }

        public float getVolume()
        {
            GameboiiSoundHandler handler = Gameboii.getSoundHandler();
            return handler != null ? handler.getVolume(this) : 1;
        }

        public void setVolume(float volume)
        {
            GameboiiSoundHandler handler = Gameboii.getSoundHandler();

            if (handler != null)
            {
                handler.setVolume(this, volume);
            }
        }
    }
}
