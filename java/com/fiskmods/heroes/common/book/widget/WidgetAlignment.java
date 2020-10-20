package com.fiskmods.heroes.common.book.widget;

import java.awt.Point;
import java.util.Locale;

public enum WidgetAlignment
{
    LEFT
    {
        @Override
        public Point offset(Widget widget, int x, int y, int pageWidth)
        {
            return new Point(widget.x + x, widget.y + y);
        }
    },
    RIGHT
    {
        @Override
        public Point offset(Widget widget, int x, int y, int pageWidth)
        {
            return new Point(Math.round(-widget.x + x + pageWidth - widget.getWidth(x, y, pageWidth)), widget.y + y);
        }
    },
    CENTER
    {
        @Override
        public Point offset(Widget widget, int x, int y, int pageWidth)
        {
            return new Point(Math.round(widget.x + x + (pageWidth - widget.getWidth(x, y, pageWidth)) / 2), widget.y + y);
        }
    };

    public abstract Point offset(Widget widget, int x, int y, int pageWidth);

    public static WidgetAlignment get(String name)
    {
        WidgetAlignment align = valueOf(name.toUpperCase(Locale.ROOT));

        if (align != null)
        {
            return align;
        }

        return LEFT;
    }
}
