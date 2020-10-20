package com.fiskmods.heroes.gameboii.batfish;

import java.nio.ByteBuffer;

import com.fiskmods.heroes.gameboii.Abstract2DGame;
import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.batfish.level.BatfishPlayer;
import com.fiskmods.heroes.gameboii.graphics.Screen;

public class Batfish extends Abstract2DGame
{
    public static final Batfish INSTANCE = new Batfish();

    public static final double DIFFICULTY_SPEED = 1000;
    public static final double SPACE_ALTITUDE = 8000;

    public BatfishPlayer player;

    public int titleThemeTicks = -1;
    public int worldPowerup;

    private boolean launched;

    public Batfish()
    {
        super(BatfishGraphics.INSTANCE, 1024, 1);
    }

    @Override
    public void read(ByteBuffer buf, int protocol)
    {
        player.read(buf, protocol);
    }

    @Override
    public void write(ByteBuffer buf)
    {
        player.write(buf);
    }

    @Override
    public void preInit(int width, int height)
    {
        if (player == null)
        {
            player = new BatfishPlayer();
        }
    }

    @Override
    public void postInit(int width, int height)
    {
    }

    @Override
    public void tick()
    {
        super.tick();

        if (titleThemeTicks > 0)
        {
            --titleThemeTicks;
        }
        else if (titleThemeTicks == 0)
        {
            Gameboii.playSound(BatfishSounds.TITLE, 1, 1);
            titleThemeTicks = 1160;
        }

        if (worldPowerup > 0)
        {
            --worldPowerup;
        }
    }

    @Override
    public void quit()
    {
        super.quit();
        player = null;
        launched = false;
        titleThemeTicks = -1;
        worldPowerup = 0;
    }

    @Override
    public Screen displayMenuScreen()
    {
        if (!launched)
        {
            launched = true;
            return new ScreenLoading();
        }

        return new ScreenMainMenu();
    }
}
