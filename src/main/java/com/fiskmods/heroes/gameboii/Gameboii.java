package com.fiskmods.heroes.gameboii;

import org.lwjgl.input.Keyboard;

import com.fiskmods.heroes.client.sound.MovingSoundGameboii;
import com.fiskmods.heroes.common.gameboii.GameboiiSaveType;
import com.fiskmods.heroes.common.network.MessageGameboii;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.gameboii.engine.GameboiiSound;
import com.fiskmods.heroes.gameboii.engine.GameboiiSoundHandler;
import com.fiskmods.heroes.gameboii.graphics.Screen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

public class Gameboii
{
    @SideOnly(Side.CLIENT)
    private static IGameboiiGame currentGame;
    private static GameboiiCartridge currentCartridge;

    private static GameboiiSaveType saveFormat;

    @SideOnly(Side.CLIENT)
    public static IGameboiiGame get(GameboiiCartridge cartridge)
    {
        return cartridge != null ? (IGameboiiGame) cartridge.supplier.get() : null;
    }

    @SideOnly(Side.CLIENT)
    private static boolean set(GameboiiCartridge cartridge)
    {
        if (currentCartridge != cartridge)
        {
            currentCartridge = cartridge;
            currentGame = get(cartridge);

            return true;
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void start(GameboiiCartridge cartridge, GameboiiSaveType saveType, int width, int height)
    {
        boolean load = set(cartridge);
        currentGame.init(width, height);

        if (load)
        {
            saveFormat = saveType;
            SHNetworkManager.wrapper.sendToServer(new MessageGameboii.RequestStats(cartridge, saveType));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void save()
    {
        if (currentCartridge != null)
        {
            try
            {
                byte[] data = currentGame.writeSaveData();
                SHNetworkManager.wrapper.sendToServer(new MessageGameboii.Save(saveFormat, currentCartridge, data));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void quit(IGameboiiGame game)
    {
        save();
        game.quit();
        currentCartridge = null;
        currentGame = null;
    }

    @SideOnly(Side.CLIENT)
    public static void quit()
    {
        Minecraft.getMinecraft().thePlayer.closeScreen();
    }

    @SideOnly(Side.CLIENT)
    public static int getWidth()
    {
        return currentGame != null ? currentGame.getWidth() : 0;
    }

    @SideOnly(Side.CLIENT)
    public static int getHeight()
    {
        return currentGame != null ? currentGame.getHeight() : 0;
    }

    @SideOnly(Side.CLIENT)
    public static Screen getScreen()
    {
        return currentGame != null ? currentGame.getScreen() : null;
    }

    @SideOnly(Side.CLIENT)
    public static void displayScreen(Screen screen)
    {
        if (currentCartridge != null)
        {
            currentGame.displayScreen(screen);
        }
    }

    @SideOnly(Side.CLIENT)
    public static GameboiiSoundHandler getSoundHandler()
    {
        return currentGame != null ? currentGame.getSoundHandler() : null;
    }

    @SideOnly(Side.CLIENT)
    public static void playSound(GameboiiSound sound, float volume, float pitch, boolean loop)
    {
        if (currentCartridge != null)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.getSoundHandler().playSound(new MovingSoundGameboii(mc.thePlayer, sound, volume, pitch, loop));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playSound(GameboiiSound sound, float volume, float pitch)
    {
        playSound(sound, volume, pitch, false);
    }

    @SideOnly(Side.CLIENT)
    public static boolean keyLeftPressed()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
    }

    @SideOnly(Side.CLIENT)
    public static boolean keyRightPressed()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
    }

    @SideOnly(Side.CLIENT)
    public static boolean keyUpPressed()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
    }

    @SideOnly(Side.CLIENT)
    public static boolean keyDownPressed()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
    }

    @SideOnly(Side.CLIENT)
    public static boolean keyShiftPressed()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }
}
