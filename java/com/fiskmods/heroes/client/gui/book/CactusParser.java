package com.fiskmods.heroes.client.gui.book;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.common.book.Book;
import com.fiskmods.heroes.common.book.Chapter;
import com.fiskmods.heroes.common.book.IBookParser;
import com.fiskmods.heroes.common.book.Page;
import com.fiskmods.heroes.common.book.json.JsonBook;
import com.fiskmods.heroes.common.book.json.JsonChapter;
import com.fiskmods.heroes.common.book.json.JsonPage;
import com.fiskmods.heroes.common.item.ItemCactusJournal;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

public class CactusParser extends IBookParser.Impl
{
    public final ItemStack stack;

    public CactusParser(ItemStack itemstack)
    {
        stack = itemstack;
    }

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
            int num = ++chapterId;

            if (ItemCactusJournal.hasEntry(stack, num))
            {
                Chapter chapter = new Chapter(jsonChapter.name);
                chapter.pageId = "chapter" + num;
                book.chapters.add(chapter);

                for (int i = 0; i < jsonChapter.pages.size(); ++i)
                {
                    JsonPage jsonPage = jsonChapter.pages.get(i);
                    Page page = jsonPage.build(book);

                    if (i == 0)
                    {
                        chapter.text = page.getHeader()[0];
                        chapter.pageNumber = page.pageNumber;
                        page.pageId = chapter.pageId;
                    }

                    queuedPages.add(page);
                }
            }
            else
            {
                jsonChapter.pages.forEach(t -> queuedPages.add(new Page()));
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
}
