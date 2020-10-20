package com.fiskmods.heroes.gameboii.batfish;

import java.awt.Color;
import java.awt.Graphics2D;

import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.graphics.GameboiiFont;
import com.fiskmods.heroes.gameboii.graphics.Screen;

public class ScreenPause extends Screen
{
    private final Screen prevScreen;

    public ScreenPause(Screen screen)
    {
        prevScreen = screen;
    }

    @Override
    public void initScreen()
    {
        int x = width / 2 - 200;
        int y = 140;

        new Button(x, y + 45 * 0, 400, 40, "Resume", () -> Gameboii.displayScreen(prevScreen));
        new Button(x, y + 45 * 1, 400, 40, "Shop", () -> Gameboii.displayScreen(new ScreenShopMain()));
        new Button(x, y + 45 * 2, 400, 40, "Options", () -> Gameboii.displayScreen(new ScreenOptions(prevScreen)));
        new Button(x, y + 45 * 3, 400, 40, "Quit to Title", () ->
        {
            Batfish.INSTANCE.player.reset();
            Gameboii.displayScreen(null);
        });

        addConsoleButton(ConsoleButton.X, "Select", this::pressButton);
    }

    @Override
    public void draw(Graphics2D g2d)
    {
        if (prevScreen != null)
        {
            prevScreen.draw(g2d);
            g2d.setColor(new Color(0, 0, 0, 0.5F));
            g2d.fillRect(0, 0, width, height);
        }
        else
        {
            drawDefaultBackground(g2d);
        }

        String s = "Paused";
        g2d.setFont(GameboiiFont.SHOP_TITLE);
        fontRenderer.drawStringWithShadow(s, (width - fontRenderer.getStringWidth(s)) / 2, 70, 0xFFFFFF, 0);
        super.draw(g2d);
    }
}
