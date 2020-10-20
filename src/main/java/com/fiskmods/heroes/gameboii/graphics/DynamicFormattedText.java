package com.fiskmods.heroes.gameboii.graphics;

import java.awt.Color;
import java.awt.Font;
import java.util.function.Supplier;

public class DynamicFormattedText extends AbstractFormattedText<DynamicFormattedText>
{
    public final Supplier<String> text;

    public DynamicFormattedText(Supplier<String> text, Color color, Font font)
    {
        super(color, font);
        this.text = text;
    }

    public DynamicFormattedText(Supplier<String> text, Color color)
    {
        super(color);
        this.text = text;
    }

    public DynamicFormattedText(Supplier<String> text, Font font)
    {
        super(font);
        this.text = text;
    }

    public DynamicFormattedText(Supplier<String> text)
    {
        this.text = text;
    }

    public DynamicFormattedText add(Supplier<String> text, Color color)
    {
        return add(new DynamicFormattedText(text, color));
    }

    public DynamicFormattedText add(Supplier<String> text)
    {
        return add(new DynamicFormattedText(text));
    }

    @Override
    public String getText()
    {
        return text.get();
    }
}
