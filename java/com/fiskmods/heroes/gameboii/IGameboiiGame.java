package com.fiskmods.heroes.gameboii;

import com.fiskmods.heroes.gameboii.engine.GameboiiSoundHandler;
import com.fiskmods.heroes.gameboii.graphics.Screen;

public interface IGameboiiGame
{
    void register();

    void init(int width, int height);

    void readSaveData(byte[] data) throws Exception;

    byte[] writeSaveData() throws Exception;

    void keyTyped(char character, int key);

    void draw(float partialTicks);

    void tick();

    void quit();

    int getWidth();

    int getHeight();

    Screen getScreen();

    void displayScreen(Screen screen);

    GameboiiSoundHandler getSoundHandler();
}
