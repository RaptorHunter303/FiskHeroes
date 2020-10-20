package com.fiskmods.heroes.common.book;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.EnumChatFormatting;

public abstract class Clickable
{
    public List<Rectangle> bounds = new ArrayList<>();
    public EnumChatFormatting color = EnumChatFormatting.BLACK;

    public Clickable setBounds(Rectangle... rectangles)
    {
        bounds = Lists.newArrayList(rectangles);
        return this;
    }

    public Clickable setColor(EnumChatFormatting format)
    {
        color = format;
        return this;
    }

    public abstract void execute(int button);
}
