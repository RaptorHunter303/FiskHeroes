package com.fiskmods.heroes.common.book;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.common.book.json.BookContainer;
import com.fiskmods.heroes.common.item.ItemMetahumanLog;

public enum BookHandler
{
    INSTANCE;

    public Map<String, BookContainer> books = new HashMap<>();

    public void load(File saveDir)
    {
        ItemMetahumanLog.clearCache("minecraft");
        File dir = new File(saveDir, "books");
        books.clear();

        if (!dir.exists())
        {
            dir.mkdirs();
        }

        File[] files = dir.listFiles();

        if (files != null)
        {
            for (File bookFile : files)
            {
                BookContainer container = BookContainer.create(bookFile);

                if (container != null)
                {
                    books.put(bookFile.getName(), container);
                }
            }
        }
    }
}
