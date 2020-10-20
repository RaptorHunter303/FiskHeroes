package com.fiskmods.heroes.gameboii.batfish;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.lwjgl.input.Keyboard;

import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.batfish.level.BatfishPlayer;
import com.fiskmods.heroes.gameboii.batfish.level.BatfishPlayer.Skin;
import com.fiskmods.heroes.gameboii.graphics.GameboiiFont;
import com.fiskmods.heroes.gameboii.graphics.Screen;

public class ScreenShopMain extends Screen
{
    private final Screen prevScreen;

    public Skin selectedSkin;

    public ScreenShopMain()
    {
        selectedSkin = Batfish.INSTANCE.player.getSkin();
        prevScreen = Batfish.INSTANCE.currentScreen;
    }

    @Override
    public void initScreen()
    {
        addConsoleButton(ConsoleButton.X, () -> Batfish.INSTANCE.player.hasSkin(selectedSkin) ? "Select" : "Buy", () ->
        {
            if (Batfish.INSTANCE.player.hasSkin(selectedSkin))
            {
                Batfish.INSTANCE.player.setSkin(selectedSkin);
            }
            else if (Batfish.INSTANCE.player.unlock(selectedSkin))
            {
                Gameboii.playSound(BatfishSounds.COIN, 1, 1);
            }
        });
        addConsoleButton(ConsoleButton.Z, "Back", () -> Gameboii.displayScreen(prevScreen));
    }

    @Override
    public void draw(Graphics2D g2d)
    {
        drawDefaultBackground(g2d);

        String s = "Shop";
        g2d.setFont(GameboiiFont.SHOP_TITLE);
        fontRenderer.drawStringWithShadow(s, (width - fontRenderer.getStringWidth(s)) / 2, 50, 0xFFFFFF, 0);

        BatfishPlayer player = Batfish.INSTANCE.player;
        int srcX1 = 0;
        int srcX2 = srcX1 + 20;

        for (int i = selectedSkin.ordinal; i > 0; --i)
        {
            Skin skin = Skin.SKINS[i - 1];
            Rectangle rect = new Rectangle(width / 2 - (selectedSkin.ordinal - i + 1) * 80, height / 3, 80, 80);

            if (player.isSkinAvailable(skin))
            {
                drawCenteredImage(g2d, skin.getResource(), rect.x, rect.y + (20 - skin.height) * 4, rect.width, rect.height, srcX1, 0, srcX2, 20);
            }
            else
            {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(rect.x - rect.width / 2, rect.y + (20 - skin.height) * 4 - rect.height / 2, rect.width, rect.height);
            }

            if (skin == player.getSkin())
            {
                g2d.setColor(Color.BLUE);
                g2d.fillRect(rect.x - rect.width / 2, rect.y + rect.height / 2 + 5, rect.width, 5);
            }
        }

        for (int i = selectedSkin.ordinal + 1; i < Skin.SKINS.length; ++i)
        {
            Skin skin = Skin.SKINS[i];
            Rectangle rect = new Rectangle(width / 2 + (i - selectedSkin.ordinal) * 80, height / 3, 80, 80);

            if (player.isSkinAvailable(skin))
            {
                drawCenteredImage(g2d, skin.getResource(), rect.x, rect.y + (20 - skin.height) * 4, rect.width, rect.height, srcX1, 0, srcX2, 20);
            }
            else
            {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(rect.x - rect.width / 2, rect.y + (20 - skin.height) * 4 - rect.height / 2, rect.width, rect.height);
            }

            if (skin == player.getSkin())
            {
                g2d.setColor(Color.BLUE);
                g2d.fillRect(rect.x - rect.width / 2, rect.y + rect.height / 2 + 5, rect.width, 5);
            }
        }

        drawCenteredImage(g2d, selectedSkin.getResource(), width / 2, height / 3 + (20 - selectedSkin.height) * 8, 160, 160, srcX1, 0, srcX2, 20);
        drawCoinCount(g2d, 34, 34, player.totalCoins, false);
//        drawImage(g2d, player.skin.getResource(), 10, 80, 80, 80);

        s = selectedSkin.name;
        g2d.setFont(GameboiiFont.DEFAULT);
        fontRenderer.drawStringWithShadow(s, (width - fontRenderer.getStringWidth(s)) / 2, height / 3 + 120, 0xFFFFFF, 0, 1);
//        g2d.setFont(BatfishFont.DEFAULT);
//        fontRenderer.drawStringWithShadow(String.format("%s %s/%s", selectedSkin.name, selectedSkin.ordinal + 1, Skin.values().length), width / 2 - 80, height / 2 - 130, 0xffffff, 0);

        if (!player.hasSkin(selectedSkin))
        {
            g2d.setFont(GameboiiFont.DEFAULT);
            drawCoinCount(g2d, width / 2, height / 3 + 165, selectedSkin.price, true);
        }

        super.draw(g2d);
    }

    @Override
    public void keyTyped(char character, int key)
    {
        super.keyTyped(character, key);

        if (key == Keyboard.KEY_LEFT || key == Keyboard.KEY_A)
        {
            cycleSkin(-1);
        }
        else if (key == Keyboard.KEY_RIGHT || key == Keyboard.KEY_D)
        {
            cycleSkin(1);
        }
    }

    private void cycleSkin(int offset)
    {
        int i = selectedSkin.ordinal + offset;

        if (i < 0)
        {
            i = Skin.SKINS.length - 1;
        }
        else if (i > Skin.SKINS.length - 1)
        {
            i = 0;
        }

        selectedSkin = Skin.SKINS[i];

        if (!Batfish.INSTANCE.player.isSkinAvailable(selectedSkin))
        {
            cycleSkin(offset);
        }
    }
}
