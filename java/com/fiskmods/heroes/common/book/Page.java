package com.fiskmods.heroes.common.book;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.common.book.json.JsonClickable;
import com.fiskmods.heroes.common.book.widget.Widget;

import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;

public class Page implements Comparable<Page>
{
    protected final String[] header = new String[2];
    public String text;
    public Book parent;

    public String pageId = "";
    public int pageNumber;

    public List<JsonClickable> jsonClickables = new ArrayList<>();
    public List<Clickable> clickables = new ArrayList<>();
    public List<Widget> widgets = new ArrayList<>();

    public Page(String text, String... header)
    {
        this.text = text;

        if (header != null)
        {
            for (int i = 0; i < Math.min(header.length, 2); ++i)
            {
                this.header[i] = header[i];
            }
        }
    }

    public Page()
    {
        this("");
    }

    public Page setPageId(String id)
    {
        pageId = id;
        return this;
    }

    public String[] getHeader()
    {
        String[] astring = new String[header.length];

        for (int i = 0; i < astring.length; ++i)
        {
            if (!StringUtils.isNullOrEmpty(header[i]))
            {
                astring[i] = StatCollector.translateToLocal(header[i]);
            }
        }

        return astring;
    }

    public String getSummaryTitle()
    {
        return getHeader()[0];
    }

    @Override
    public int compareTo(Page o)
    {
        return getSummaryTitle().compareTo(o.getSummaryTitle());
    }
}
