package com.fiskmods.heroes.common.book.widget;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import net.minecraft.item.ItemStack;

public class Widget
{
    public final WidgetAlignment alignment;
    public final WidgetType type;
    public final String value;
    public final int x;
    public final int y;
    public final float scale;
    public final boolean background;

    public ItemStack[][] itemstacks;

    public Widget(WidgetType type, String value, int x, int y, float scale, boolean background, WidgetAlignment alignment)
    {
        this.type = type;
        this.value = value;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.background = background;
        this.alignment = alignment;
    }

    public Widget(WidgetType type, String value, int x, int y, float scale, boolean background)
    {
        this(type, value, x, y, scale, background, WidgetAlignment.LEFT);
    }

    public Point getPos(int x, int y, int pageWidth)
    {
        return alignment.offset(this, x, y, pageWidth);
    }

    public float getScale(WidgetType type1)
    {
        return type.scale(this, type1);
    }

    public Dimension getSize()
    {
        return type.getSize(this);
    }

    public List<Rectangle> getBounds(int x, int y, int pageWidth)
    {
        List<Rectangle> list = new ArrayList<>();

        for (WidgetType type1 : WidgetType.values())
        {
            if (type.handleType(type1))
            {
                Dimension size = type1.getSize(this);
                Point offset = type.offset(this, type1);
                float scale1 = type.scale(this, type1);

                list.add(new Rectangle(offset, new Dimension(Math.round(size.width * scale1), Math.round(size.height * scale1))));
            }
        }

        return list;
    }

    public float getWidth(int x, int y, int pageWidth)
    {
        List<Rectangle> list = getBounds(x, y, pageWidth);
        float minX = 0;
        float maxX = 0;

        for (Rectangle rect : list)
        {
            minX = (float) Math.min(minX, rect.getMinX());
            maxX = (float) Math.max(maxX, rect.getMaxX());
        }

        return maxX - minX;
    }

    public float getHeight(int x, int y, int pageWidth)
    {
        List<Rectangle> list = getBounds(x, y, pageWidth);
        float minY = 0;
        float maxY = 0;

        for (Rectangle rect : list)
        {
            minY = (float) Math.min(minY, rect.getMinY());
            maxY = (float) Math.max(maxY, rect.getMaxY());
        }

        return maxY - minY;
    }

    public static class Adapter extends TypeAdapter<Widget>
    {
        @Override
        public void write(JsonWriter out, Widget value) throws IOException
        {
            if (value == null)
            {
                out.nullValue();
                return;
            }

            out.beginObject();
            out.name("type").value(value.type.name().toLowerCase(Locale.ROOT));
            out.name("value").value(value.value);

            if (value.alignment != WidgetAlignment.LEFT)
            {
                out.name("align").value(value.alignment.name().toLowerCase(Locale.ROOT));
            }

            if (value.x != 0 || value.y != 0)
            {
                out.name("pos").beginArray().value(value.x).value(value.y).endArray();
            }

            if (value.scale != value.type.scaleDefault)
            {
                out.name("scale").value(value.scale);
            }

            if (value.background != value.type.backgroundDefault)
            {
                out.name("background").value(value.background);
            }

            out.endObject();
        }

        @Override
        public Widget read(JsonReader in) throws IOException
        {
            if (in.peek() == JsonToken.NULL)
            {
                in.nextNull();
                return null;
            }

            WidgetAlignment alignment = WidgetAlignment.LEFT;
            WidgetType type = null;
            String value = null;
            int x = 0;
            int y = 0;
            float scale = 1;
            boolean background = true;
            boolean isSet = false;

            in.beginObject();
            String name = "";

            while (in.hasNext())
            {
                switch (in.peek())
                {
                case NAME:
                    name = in.nextName();
                    break;
                case STRING:
                    if (name.equals("type"))
                    {
                        type = WidgetType.get(in.nextString());
                    }
                    else if (name.equals("value"))
                    {
                        value = in.nextString();
                    }
                    else if (name.equals("align"))
                    {
                        alignment = WidgetAlignment.get(in.nextString());
                    }
                    else if (name.equals("background"))
                    {
                        background = Boolean.valueOf(in.nextString());
                    }
                    else
                    {
                        in.skipValue();
                    }

                    break;
                case NUMBER:
                    if (name.equals("scale"))
                    {
                        scale = (float) in.nextDouble();
                    }
                    else
                    {
                        in.skipValue();
                    }
                    break;
                case BOOLEAN:
                    if (name.equals("background"))
                    {
                        background = in.nextBoolean();
                        isSet = true;
                    }
                    else
                    {
                        in.skipValue();
                    }
                    break;
                case BEGIN_ARRAY:
                    in.beginArray();

                    if (in.peek() == JsonToken.NUMBER)
                    {
                        x = in.nextInt();
                    }

                    if (in.peek() == JsonToken.NUMBER)
                    {
                        y = in.nextInt();
                    }

                    while (in.hasNext())
                    {
                        in.skipValue();
                    }

                    in.endArray();
                    break;
                default:
                    in.skipValue();
                    break;
                }
            }

            in.endObject();

            if (type == null || value == null)
            {
                return null;
            }

            return new Widget(type, value, x, y, scale, isSet ? background : type.backgroundDefault, alignment);
        }
    }
}
