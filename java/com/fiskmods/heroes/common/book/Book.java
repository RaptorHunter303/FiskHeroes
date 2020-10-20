package com.fiskmods.heroes.common.book;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fiskmods.heroes.client.gui.GuiSuperheroesBook;
import com.fiskmods.heroes.common.book.json.BookContainer;
import com.fiskmods.heroes.common.book.json.JsonBook;
import com.fiskmods.heroes.common.book.json.JsonChapter;
import com.fiskmods.heroes.util.FileHelper;
import com.fiskmods.heroes.util.TextureHelper;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.oredict.OreDictionary;

public class Book
{
    private final FontRenderer fontRendererObj;
    public final IBookParser bookParser;
    public final List<Page> pages = new ArrayList<>();
    public final List<Chapter> chapters = new ArrayList<>();
    private ResourceLocation background;

    public Book(BookContainer container, IBookParser parser)
    {
        String parsing = "Unknown origin";
        bookParser = parser;

        try
        {
            fontRendererObj = Minecraft.getMinecraft().fontRenderer;
            if (container != null)
            {
                try
                {
                    parse(container.book, container.chapters);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (BookParseException e)
        {
            throw e;
        }
        catch (JsonParseException e)
        {
            throw new BookParseException(e, parsing);
        }
    }

    public Book(String modid, String name, IBookParser parser)
    {
        Gson gson = BookContainer.GSON_FACTORY.create();
        String parsing = "Unknown origin";
        bookParser = parser;

        try
        {
            String filePath = String.format("%s:books/%s/%s.json", modid, "%s", name);
            String lang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
            JsonBook jsonBook = null;

            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();

            for (int i = 0; i < 2; ++i)
            {
                try
                {
                    parsing = String.format(filePath, lang);
                    jsonBook = gson.fromJson(new InputStreamReader(manager.getResource(new ResourceLocation(parsing)).getInputStream()), JsonBook.class);
                }
                catch (JsonParseException | IOException e)
                {
                    if (i > 0)
                    {
                        e.printStackTrace();
                    }
                }

                if (jsonBook != null)
                {
                    break;
                }

                lang = "en_US";
            }

            fontRendererObj = Minecraft.getMinecraft().fontRenderer;
            try
            {
                List<JsonChapter> list = new ArrayList<>();

                for (String path : jsonBook.chapters)
                {
                    parsing = String.format("%s:books/%s/%s", modid, lang, path);
                    list.add(gson.fromJson(new InputStreamReader(manager.getResource(new ResourceLocation(parsing)).getInputStream()), JsonChapter.class));
                }

                parsing = String.format(filePath, lang);
                parse(jsonBook, list);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (BookParseException e)
        {
            throw e;
        }
        catch (JsonParseException e)
        {
            throw new BookParseException(e, parsing);
        }
    }

    public Book(BookContainer container)
    {
        this(container, new IBookParser.Impl());
    }

    public Book(String modid, String name)
    {
        this(modid, name, new IBookParser.Impl());
    }

    protected void parse(JsonBook jsonBook, List<JsonChapter> jsonChapters)
    {
        if (jsonBook != null)
        {
            boolean prevUnicode = fontRendererObj.getUnicodeFlag();
            fontRendererObj.setUnicodeFlag(true);

            try
            {
                if (background == null)
                {
                    String path = jsonBook.background;
                    background = new ResourceLocation(path);

                    if (FileHelper.isURL(path))
                    {
                        TextureHelper.getDownloadImage(background, path);
                    }
                }

                bookParser.parse(jsonBook, jsonChapters, this, fontRendererObj);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            fontRendererObj.setUnicodeFlag(prevUnicode);
        }
    }

    public static ItemStack parseItem(String id)
    {
        if (id != null)
        {
            int metadata = 0;

            if (id.contains("@"))
            {
                metadata = Integer.valueOf(id.substring(id.lastIndexOf("@") + 1));
                id = id.substring(0, id.lastIndexOf("@"));

                if (metadata < 0)
                {
                    metadata = OreDictionary.WILDCARD_VALUE;
                }
            }

            Item item = (Item) Item.itemRegistry.getObject(id);

            if (item != null)
            {
                return new ItemStack(item, 1, metadata);
            }
        }

        return null;
    }

    public Book addPage(Page page)
    {
        page.parent = this;

        if (page instanceof Chapter)
        {
            chapters.add((Chapter) page);

            if (pages.size() % 2 == 1)
            {
                addPage(new Page());
            }
        }

        page.pageNumber = getTotalPages();
        pages.add(page);

        if (page instanceof Chapter)
        {
            if (page.pageNumber % 2 == 1)
            {
                addPage(new Page());
            }
        }

        return this;
    }

    public Book addAll(Page[] pages)
    {
        for (Page page : pages)
        {
            addPage(page);
        }

        return this;
    }

    public Book addAll(List<Page> pages)
    {
        for (Page page : pages)
        {
            addPage(page);
        }

        return this;
    }

    public Book addPageAfter(Page page, Page page1)
    {
        int i = pages.indexOf(page1) + 1;
        pages.add(i, page);

        for (int j = 0; j < pages.size(); ++j)
        {
            pages.get(j).pageNumber = j + 1;
        }

        return this;
    }

    public Book addChapterSummary(Chapter chapter)
    {
        List<Page> list = new ArrayList<>(chapter.getPages());
        Collections.sort(list);

        Page page = chapter;
        int cap = 13;

        for (int i = 0; i <= list.size() / cap; ++i)
        {
            int j = i * cap;
            List<Page> subList = list.subList(j, j + cap > list.size() ? list.size() : j + cap);
            Page newPage = new Page();

            for (Page summedPage : subList)
            {
                newPage.text += String.format("* <link=%s?c=BLACK>%s</link>\n", summedPage.pageId, summedPage.getSummaryTitle());

                if (page != newPage)
                {
                    addPageAfter(newPage, page);
                    page = newPage;
                }
            }
        }

        if (page.pageNumber % 2 == 0 && pages.size() >= page.pageNumber && StringUtils.isNullOrEmpty(pages.get(page.pageNumber).text))
        {
            pages.remove(page.pageNumber);

            for (int i = 0; i < pages.size(); ++i)
            {
                pages.get(i).pageNumber = i + 1;
            }
        }

        return this;
    }

    public int getTotalPages()
    {
        return pages.size() + 1;
    }

    public int getTotalPagePairs()
    {
        return getTotalPages() / 2;
    }

    public Page getPageFromId(String id)
    {
        for (Page page : pages)
        {
            if (id.equals(page.pageId))
            {
                return page;
            }
        }

        return null;
    }

    public Page getPageAt(int pageNum)
    {
        for (Page page : pages)
        {
            if (page.pageNumber == pageNum)
            {
                return page;
            }
        }

        return null;
    }

    public ResourceLocation getBackground()
    {
        return background == null ? GuiSuperheroesBook.BOOK_TEXTURES : background;
    }

    public int getPageWidth()
    {
        return bookParser.getPageWidth();
    }

    public static void registerPageParser(IPageParser parser)
    {
        IBookParser.Impl.PARSERS.add(parser);
    }

    public static class BookParseException extends JsonParseException
    {
        public final String fileName;

        public BookParseException(JsonParseException cause, String parsingFile)
        {
            super(cause.getMessage(), cause);
            fileName = parsingFile;
        }
    }
}
