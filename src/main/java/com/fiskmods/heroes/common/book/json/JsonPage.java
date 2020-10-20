package com.fiskmods.heroes.common.book.json;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.common.book.Book;
import com.fiskmods.heroes.common.book.Page;
import com.fiskmods.heroes.common.book.widget.Widget;
import com.fiskmods.heroes.common.book.widget.WidgetType;

public class JsonPage
{
    public String id = "";
    public String header = "";
    public String text = "";
    public List<Widget> extra = new ArrayList<>();
    public List<JsonClickable> clickables = new ArrayList<>();

    public Page build(Book book)
    {
        Page page = new Page(text, header).setPageId(id);

        page.widgets.addAll(extra);
        page.jsonClickables.addAll(clickables);

        for (Widget widget : extra)
        {
            for (WidgetType type : WidgetType.values())
            {
                if (widget.type.handleType(type))
                {
                    type.initType(widget);
                }
            }
        }

        return page;
    }
}
