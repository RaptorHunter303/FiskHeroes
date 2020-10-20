package com.fiskmods.heroes.common.book;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StringUtils;

public class Chapter extends Page
{
    public Chapter(String text)
    {
        super(text);
    }

    public List<Page> getPages()
    {
        List<Page> list = new ArrayList<>();
        Chapter chapter = null;

        for (Page page : parent.pages)
        {
            if (page instanceof Chapter)
            {
                chapter = (Chapter) page;
            }
            else if (!StringUtils.isNullOrEmpty(page.getHeader()[0]))
            {
                if (chapter == this)
                {
                    list.add(page);
                }
            }
        }

        return list;
    }

    @Override
    public String getSummaryTitle()
    {
        return text;
    }
}
