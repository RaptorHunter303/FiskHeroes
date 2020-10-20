package com.fiskmods.heroes.gameboii.batfish;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.batfish.level.BatfishLevel;
import com.fiskmods.heroes.gameboii.batfish.level.BatfishPlayer;
import com.fiskmods.heroes.gameboii.batfish.level.PowerupObject;
import com.fiskmods.heroes.gameboii.batfish.level.PowerupObject.Powerup;
import com.fiskmods.heroes.gameboii.batfish.level.PowerupObject.Type;
import com.fiskmods.heroes.gameboii.batfish.level.PowerupObject.UseType;
import com.fiskmods.heroes.gameboii.graphics.FilteredLevelCanvas;
import com.fiskmods.heroes.gameboii.graphics.GameboiiFont;
import com.fiskmods.heroes.gameboii.graphics.Screen;
import com.fiskmods.heroes.gameboii.level.LevelObject;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;

public class ScreenIngame extends Screen
{
    private static final Comparator<LevelObject> RENDER_ORDER = Comparator.comparingInt(t -> t.depthPlane());

    public static final boolean DEBUG = false;
    public static final int SCALE = 3;

    private final BatfishLevel level;
    private FilteredLevelCanvas canvas;

    private boolean dialoguePrompt;

    public ScreenIngame(boolean newGame)
    {
        level = new BatfishLevel().reset();
        dialoguePrompt = newGame;
    }

    @Override
    public void initScreen()
    {
        canvas = new FilteredLevelCanvas(width, height, this::filterPixels);
        Powerup p = Batfish.INSTANCE.player.getPowerup(Type.WORLD);

        if (p != null && p.time > 0)
        {
            addConsoleButton(ConsoleButton.C, "Use 'World'", () -> useWorld(Batfish.INSTANCE.player));
        }

        addConsoleButton(ConsoleButton.Z, "Pause", () -> Gameboii.displayScreen(new ScreenPause(this)));
    }

    private void useWorld(BatfishPlayer player)
    {
        Powerup p = player.getPowerup(Type.WORLD);

        if (p != null && p.time > 0)
        {
            --p.time;
            Batfish.INSTANCE.worldPowerup = 100;
            Gameboii.playSound(BatfishSounds.WORLD, 1, 1);
            onOpenScreen();
        }
    }

    @Override
    public void update()
    {
        level.onUpdate();

        if (dialoguePrompt)
        {
            Batfish.INSTANCE.player.startDialogue(BatfishDialogue.PRO_1, true);
            dialoguePrompt = false;
        }
    }

    @Override
    public void draw(Graphics2D g2d)
    {
        float prevRenderTick = ClientEventHandler.renderTick;

        if (Gameboii.getScreen() != this)
        {
            ClientEventHandler.renderTick = 1;
        }

        BatfishPlayer p = Batfish.INSTANCE.player;
        drawLevel(canvas.graphics, p);
        g2d.drawImage(canvas.image, canvas, 0, 0);
        drawHUD(g2d, p);

        super.draw(g2d);
        ClientEventHandler.renderTick = prevRenderTick;
    }

    public void drawLevel(Graphics2D g2d, BatfishPlayer p)
    {
        double altitude = SHRenderHelper.interpolate(p.posY, p.prevPosY);
        g2d.setColor(new Color(BatfishGraphics.sky_gradient[0]));
        g2d.fillRect(0, 0, width, height);

        if (BatfishGraphics.sky_gradient.length > 0)
        {
            double heightPos = altitude * 64 / 9 / Batfish.SPACE_ALTITUDE;
            int i = Math.min(MathHelper.floor_double(heightPos), BatfishGraphics.sky_gradient.length - 1);

            if (i > 1)
            {
                g2d.setColor(new Color(BatfishGraphics.sky_gradient[i]));
                g2d.fillRect(0, 0, width, height);
            }

            Random rand = new Random();

            for (i = 0; i < height; ++i)
            {
                rand.setSeed(MathHelper.floor_double(heightPos / 64 * 9 * Batfish.SPACE_ALTITUDE / 8 - i) * 0xB00B1E5);

                for (int x = 0; x < width; ++x)
                {
                    if (rand.nextFloat() < 0.001)
                    {
                        int opacity = MathHelper.clamp_int((int) (Math.min(heightPos / 100, 1) * rand.nextInt(200) * rand.nextDouble()), 0, 255);

                        if (opacity > 0)
                        {
                            g2d.setColor(new Color(opacity << 24 | 0x00FFFFFF, true));
                            g2d.fillRect(x - 1, i - 1, 2, 2 + MathHelper.floor_double(Math.max(p.motionY, 0) * 0.5));
                        }
                    }
                }
            }
        }

        for (LevelObject obj : level.objects.stream().sorted(RENDER_ORDER).collect(Collectors.toList()))
        {
            int x = getScreenPosX(width, SHRenderHelper.interpolate(obj.posX, obj.prevPosX) - obj.width / 2);
            int y = getScreenPosY(height, altitude, SHRenderHelper.interpolate(obj.posY, obj.prevPosY));

            if (DEBUG)
            {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, obj.width * SCALE, obj.height * SCALE);
            }

            obj.draw(g2d, this, x, y, width, height, SCALE);
        }
    }

    public void drawHUD(Graphics2D g2d, BatfishPlayer p)
    {
        List<PowerupObject.Powerup> powerups = new ArrayList<>();
        int x = 70;

        drawCoinCount(g2d, x, 30, p.currentCoins, false);

        for (PowerupObject.Type type : PowerupObject.Type.values())
        {
            PowerupObject.Powerup powerup = p.getPowerup(type);

            if (powerup != null)
            {
                powerups.add(powerup);
            }
        }

        g2d.setFont(GameboiiFont.BUTTON_TEXT);
        fontRenderer.drawStringWithShadow(String.format("%.1f m", p.posY / 9), x - 16, 74, 0xFFFFFF, 0);
        powerups.sort(Comparator.comparing(t -> -t.time));
        int w = 32;

        for (int i = 0; i < powerups.size(); ++i)
        {
            PowerupObject.Powerup powerup = powerups.get(i);
            int srcY = powerup.type.ordinal() * 8;
            int y = 120 + (w + 10) * i;

            drawCenteredImage(g2d, BatfishGraphics.powerups, x, y, w, w, 0, srcY, 8, srcY + 8);
            fontRenderer.drawStringWithShadow(powerup.type.useType == UseType.STACKABLE ? "x" + powerup.time : StringUtils.ticksToElapsedTime(powerup.time), x + w / 2 + 10, y + 8, 0xFFFFFF, 0);
        }
    }

    public void filterPixels(int[] pixels)
    {
//        BatfishPlayer p = Batfish.INSTANCE.player;
//        double altitude = SHRenderHelper.interpolate(p.posY, p.prevPosY);
//        double heightPos = altitude / 1000;
//        double f = Math.max(1 - heightPos, 0.5);
//
//        for (int i = 0; i < pixels.length; ++i)
//        {
//            int rgb = pixels[i];
//            int r = (rgb & 0xFF0000) >> 16;
//            int g = (rgb & 0xFF00) >> 8;
//            int b = rgb & 0xFF;
//
//            r *= f;
//            g *= f;
//            b *= f;
//            pixels[i] = r << 16 | g << 8 | b;
//        }

        int p = Batfish.INSTANCE.worldPowerup;

        if (p > 0)
        {
            float m = 100 / 2;
            double f = Math.min(Math.sin(Math.PI * (p - m / 2) / m) + 1, 1);

            if (f > 0)
            {
                BatfishPlayer player = Batfish.INSTANCE.player;
                int cx = getScreenPosX(width, SHRenderHelper.interpolate(player.posX, player.prevPosX));
                int cy = getScreenPosY(height, player.height / 2, 0);

                for (int i = 0; i < pixels.length; ++i)
                {
                    int rgb = pixels[i];
                    int r = (rgb & 0xFF0000) >> 16;
                    int g = (rgb & 0xFF00) >> 8;
                    int b = rgb & 0xFF;
                    int x = i % width - cx;
                    int y = i / width - cy;
                    double d = Math.max((1 - Math.abs(Math.sin(Math.sqrt(x * x + y * y) / height - f))) * Math.sin(f * Math.PI), 0);

                    r = (int) FiskServerUtils.interpolate(r, 255 - r, d);
                    g = (int) FiskServerUtils.interpolate(g, 255 - g, d);
                    b = (int) FiskServerUtils.interpolate(b, 255 - b, d);
                    pixels[i] = r << 16 | g << 8 | b;
                }
            }
        }
    }

    public static int getScreenPosX(int width, double minX)
    {
        return width / 2 + (int) Math.round(minX) * SCALE;
    }

    public static int getScreenPosY(int height, double playerY, double maxY)
    {
        return height / 3 * 2 + (int) Math.round(playerY - maxY) * SCALE;
    }
}
