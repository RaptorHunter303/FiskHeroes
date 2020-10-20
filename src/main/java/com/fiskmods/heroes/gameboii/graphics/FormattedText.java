package com.fiskmods.heroes.gameboii.graphics;

import java.awt.Color;
import java.awt.Font;

public class FormattedText extends AbstractFormattedText<FormattedText>
{
    public final String text;

    public FormattedText(String text, Color color, Font font)
    {
        super(color, font);
        this.text = text;
    }

    public FormattedText(String text, Color color)
    {
        super(color);
        this.text = text;
    }

    public FormattedText(String text, Font font)
    {
        super(font);
        this.text = text;
    }

    public FormattedText(String text)
    {
        this.text = text;
    }

    public FormattedText add(String text, Color color)
    {
        return add(new FormattedText(text, color));
    }

    public FormattedText add(String text)
    {
        return add(new FormattedText(text));
    }

    @Override
    public String getText()
    {
        return text;
    }
}
