package com.fiskmods.heroes.common.book;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.common.book.json.JsonBook;
import com.fiskmods.heroes.common.book.json.JsonChapter;
import com.fiskmods.heroes.common.book.json.JsonPage;
import com.google.common.collect.Lists;

import net.minecraft.client.gui.FontRenderer;

public interface IBookParser
{
    void parse(JsonBook jsonBook, List<JsonChapter> jsonChapters, Book book, FontRenderer font);

    int getPageWidth();

    public class Impl implements IBookParser
    {
        public static final List<IPageParser> PARSERS = Lists.newArrayList(new IPageParser.Impl());

        @Override
        public void parse(JsonBook jsonBook, List<JsonChapter> jsonChapters, Book book, FontRenderer font)
        {
            JsonPage jsonTOC = null;
            List<Page> queuedPages = new ArrayList<>();
            List<Chapter> queuedSummaries = new ArrayList<>();

            for (JsonPage jsonPage : jsonBook.pages)
            {
                if (jsonPage.id.equals("toc"))
                {
                    jsonTOC = jsonPage;
                    continue;
                }

                queuedPages.add(jsonPage.build(book));
            }

            int chapterId = 0;

            for (JsonChapter jsonChapter : jsonChapters)
            {
                Chapter chapter = new Chapter(jsonChapter.name);
                chapter.pageId = "chapter" + chapterId++;

                queuedPages.add(chapter);

                l: for (JsonPage jsonPage : jsonChapter.pages)
                {
                    String id = jsonPage.id;

                    if (id.startsWith("generated/"))
                    {
                        id = id.substring("generated/".length());

                        if (id.equals("summary"))
                        {
                            queuedSummaries.add(chapter);
                            continue;
                        }
                        else
                        {
                            for (IPageParser parser : PARSERS)
                            {
                                if (parser.parseGenerated(id, queuedPages))
                                {
                                    continue l;
                                }
                            }
                        }
                    }

                    queuedPages.add(jsonPage.build(book));
                }
            }

            if (jsonTOC != null)
            {
                book.addPage(jsonTOC.build(book));
            }

            book.addAll(queuedPages);

            for (Chapter chapter : queuedSummaries)
            {
                book.addChapterSummary(chapter);
            }

            Page toc = book.getPageFromId("toc");

            if (toc != null)
            {
                for (Chapter chapter : book.chapters)
                {
                    String title = chapter.text;
                    String num = chapter.pageNumber + "";

                    int cap = getPageWidth() - font.getStringWidth(".") / 2;
                    title = font.trimStringToWidth(title, cap - font.getStringWidth(num + "..."));

                    while (font.getStringWidth(title + num) < cap)
                    {
                        title += ".";
                    }

                    toc.text += String.format("<link=%s?c=BLACK>%s</link>\n", chapter.pageId, title + num);
                }
            }
        }

        @Override
        public int getPageWidth()
        {
            return 116;
        }
    }
}
