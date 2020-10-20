package com.fiskmods.heroes.gameboii.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class GameboiiFont
{
    public static Font BUTTON_TEXT = new Font("Calibri", Font.PLAIN, 26);
    public static Font SHOP_TITLE = new Font("Maiandra GD", Font.PLAIN, 50);

    public static Font LOADING = new Font("Arial Black", Font.ITALIC, 30);
    public static Font DEFAULT = new Font("Arial Black", Font.BOLD, 30);
    public static Font GAME_OVER = new Font("Arial Black", Font.PLAIN, 50);

    private final Graphics2D graphics;

    public GameboiiFont(Graphics2D g2d)
    {
        graphics = g2d;
    }

    public void drawString(AbstractFormattedText text, int x, int y, Color color, Font font)
    {
        text.draw(this, graphics, x, y, color, font);
    }

    public void drawString(String text, int x, int y, int color)
    {
//        drawString(new FormattedText(text).setColor(new Color(color)), x, y, null);
        graphics.setColor(new Color(color));
        graphics.drawString(text, x, y);
    }

    public void drawStringWithShadow(String text, int x, int y, int foregroundColor, int backgroundColor, int borderSize)
    {
        graphics.setColor(new Color(backgroundColor));
        graphics.drawString(text, x + borderSize, y);
        graphics.drawString(text, x - borderSize, y);
        graphics.drawString(text, x, y + borderSize);
        graphics.drawString(text, x, y - borderSize);
        drawString(text, x, y, foregroundColor);
    }

    public void drawStringWithShadow(String text, int x, int y, int foregroundColor, int backgroundColor)
    {
        drawStringWithShadow(text, x, y, foregroundColor, backgroundColor, 1);
    }

    public int getStringWidth(String text, Font font)
    {
        FontMetrics fm = graphics.getFontMetrics(font != null ? font : graphics.getFont());
        Rectangle2D rect = fm.getStringBounds(text, graphics);

        return (int) rect.getWidth();
    }

    public int getStringWidth(String text)
    {
        return getStringWidth(text, null);
    }
}
