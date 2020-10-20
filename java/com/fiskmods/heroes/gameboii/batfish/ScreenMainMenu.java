package com.fiskmods.heroes.gameboii.batfish;

import java.awt.Graphics2D;

import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.graphics.GameboiiFont;
import com.fiskmods.heroes.gameboii.graphics.Screen;

public class ScreenMainMenu extends Screen
{
    @Override
    public void initScreen()
    {
        int x = width / 2 - 200;
        int y = 184;

        new Button(x, y + 45 * 0, 400, 40, "Start Game", () -> Gameboii.displayScreen(new ScreenIngame(true)));
        new Button(x, y + 45 * 1, 400, 40, "Shop", () -> Gameboii.displayScreen(new ScreenShopMain()));
        new Button(x, y + 45 * 2, 400, 40, "Options", () -> Gameboii.displayScreen(new ScreenOptions(null)));
        new Button(x, y + 45 * 3, 400, 40, "Quit", () -> Gameboii.quit());

        addConsoleButton(ConsoleButton.X, "Select", this::pressButton);
    }

    @Override
    public void draw(Graphics2D g2d)
    {
        drawDefaultBackground(g2d);
        drawCenteredImage(g2d, BatfishGraphics.logo, width / 2, 80, 436, 84);

        g2d.setFont(GameboiiFont.BUTTON_TEXT);
        String s = "Copyright FiskFille 2014";
        fontRenderer.drawString(s, width - fontRenderer.getStringWidth(s) - 15, height - 10, 0xffffff);

//        g2d.setFont(GameboiiFont.DEFAULT);
//        s = SHFormatHelper.formatNumber(BatfishData.totalCoins);
//
//        int color = 0xF8AD20;
//        int color0 = 0xFFE649;
//        int color1 = 0x9D6100;
//
////        fontRenderer.drawStringWithShadow(s, width / 2 - 155 + 2, 160 + 2, color1, 0);
//        fontRenderer.drawStringWithShadow(s, width / 2 - 155, 160, 0xFFCD21, 0x3F2700, 2);
//        drawCenteredImage(g2d, BatfishGraphics.coin, width / 2 - 180, 151, 32, 32);
        drawCoinCount(g2d, width / 2 - 180, 151, Batfish.INSTANCE.player.totalCoins, false);
        super.draw(g2d);
    }
}
