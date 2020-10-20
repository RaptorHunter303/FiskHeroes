package com.fiskmods.heroes.common.book.json;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;

public class JsonClickable
{
    private String link;
    private String align;
    private List<Rectangle> bounds;
    private EnumChatFormatting color;

    public String getLink()
    {
        if (link == null)
        {
            link = "";
        }

        return link;
    }

    public String getAlignment()
    {
        if (align == null)
        {
            align = "left";
        }

        return align;
    }

    public List<Rectangle> getBounds()
    {
        if (bounds == null)
        {
            bounds = new ArrayList<>();
        }

        return bounds;
    }

    public EnumChatFormatting getColor()
    {
        if (color == null)
        {
            color = EnumChatFormatting.BLACK;
        }

        return color;
    }
}
