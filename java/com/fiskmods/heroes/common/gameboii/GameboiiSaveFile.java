package com.fiskmods.heroes.common.gameboii;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;

import com.fiskmods.heroes.gameboii.GameboiiCartridge;
import com.fiskmods.heroes.gameboii.GameboiiSave;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;

public class GameboiiSaveFile extends GameboiiSave
{
    public final EntityPlayer player;

    public GameboiiSaveFile(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public void saveData(byte[] data, GameboiiCartridge cartridge) throws Exception
    {
        File file = getSaveFile(cartridge);

        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        IOUtils.write(data, new FileOutputStream(file));
    }

    @Override
    public byte[] loadData(GameboiiCartridge cartridge) throws Exception
    {
        File file = getSaveFile(cartridge);

        if (file.exists())
        {
            byte[] data = IOUtils.toByteArray(new FileInputStream(file));
            return data;
        }

        return null;
    }

    private File getSaveFile(GameboiiCartridge cartridge)
    {
        return new File(DimensionManager.getCurrentSaveRootDirectory() + "/fiskutils/gameboii/" + cartridge.id + "/" + player.getUniqueID().toString() + ".boii");
    }
}
