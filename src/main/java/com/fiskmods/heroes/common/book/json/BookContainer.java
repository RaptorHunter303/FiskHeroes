package com.fiskmods.heroes.common.book.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.book.Book.BookParseException;
import com.fiskmods.heroes.common.book.widget.Widget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class BookContainer
{
    public static final GsonBuilder GSON_FACTORY = new GsonBuilder().registerTypeAdapter(Widget.class, new Widget.Adapter());

    public JsonBook book;
    public List<JsonChapter> chapters = new ArrayList<>();

    public BookContainer()
    {
    }

    public BookContainer(JsonBook book, List<JsonChapter> chapters)
    {
        this.book = book;
        this.chapters = chapters;
    }

    public static BookContainer create(File bookFile)
    {
        try
        {
            Gson gson = GSON_FACTORY.create();
            JsonBook jsonBook = null;

            try
            {
                jsonBook = gson.fromJson(new FileReader(bookFile), JsonBook.class);
            }
            catch (JsonParseException e)
            {
                throw new BookParseException(e, bookFile.getName());
            }

            if (jsonBook != null)
            {
                List<JsonChapter> chapters = new ArrayList<>();
                List<File> files = jsonBook.getChapters(bookFile);

                for (File chapterFile : files)
                {
                    try
                    {
                        JsonChapter jsonChapter = gson.fromJson(new FileReader(chapterFile), JsonChapter.class);

                        if (jsonChapter != null)
                        {
                            chapters.add(jsonChapter);
                        }
                    }
                    catch (IOException e)
                    {
                        FiskHeroes.LOGGER.warn("Caught exception trying to read {} chapter file at {}:", bookFile.getName(), chapterFile.getPath());
                        e.printStackTrace();
                    }
                    catch (JsonParseException e)
                    {
                        throw new BookParseException(e, chapterFile.getParentFile().getName() + "/" + chapterFile.getName());
                    }
                }

                return new BookContainer(jsonBook, chapters);
            }
        }
        catch (IOException e)
        {
            FiskHeroes.LOGGER.warn("Caught exception trying to read book file at {}:", bookFile.getPath());
            e.printStackTrace();
        }

        return null;
    }
}
