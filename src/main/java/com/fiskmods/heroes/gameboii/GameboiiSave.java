package com.fiskmods.heroes.gameboii;

public abstract class GameboiiSave
{
    public abstract void saveData(byte[] data, GameboiiCartridge cartridge) throws Exception;

    public abstract byte[] loadData(GameboiiCartridge cartridge) throws Exception;
}
