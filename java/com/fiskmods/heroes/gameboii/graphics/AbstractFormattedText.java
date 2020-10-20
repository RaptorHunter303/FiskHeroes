package com.fiskmods.heroes.gameboii.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public abstract class AbstractFormattedText<T extends AbstractFormattedText<T>>
{
    private final Color color;
    private final Font font;

    private AbstractFormattedText child;

    public AbstractFormattedText(Color color, Font font)
    {
        this.color = color;
        this.font = font;
    }

    public AbstractFormattedText(Color color)
    {
        this(color, null);
    }

    public AbstractFormattedText(Font font)
    {
        this(null, font);
    }

    public AbstractFormattedText()
    {
        this(null, null);
    }

    public T add(AbstractFormattedText text)
    {
        if (child != null)
        {
            child.add(text);
        }
        else
        {
            child = text;
        }

        return (T) this;
    }

    public abstract String getText();

    public int getWidth(GameboiiFont fontRenderer)
    {
        int i = fontRenderer.getStringWidth(getText(), font);

        if (child != null)
        {
            i += child.getWidth(fontRenderer);
        }

        return i;
    }

    public void draw(GameboiiFont fontRenderer, Graphics2D graphics, int x, int y, Color defColor, Font defFont, DrawFunction func)
    {
        if (color != null)
        {
            graphics.setColor(color);
        }
        else if (defColor != null)
        {
            graphics.setColor(defColor);
        }

        if (font != null)
        {
            graphics.setFont(font);
        }
        else if (defFont != null)
        {
            graphics.setFont(defFont);
        }

        String s = getText();
        graphics.drawString(s, x, y);

        if (child != null)
        {
            child.draw(fontRenderer, graphics, x + fontRenderer.getStringWidth(s), y, defColor, defFont);
        }
    }

    public void draw(GameboiiFont fontRenderer, Graphics2D graphics, int x, int y, Color defColor, Font defFont)
    {
        draw(fontRenderer, graphics, x, y, defColor, defFont, (s, x1, y1) -> graphics.drawString(s, x1, y1));
    }

    public interface DrawFunction
    {
        void draw(String s, int x, int y);
    }
}
