package com.fiskmods.heroes.client.gui;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fiskmods.heroes.common.book.Book;
import com.fiskmods.heroes.common.book.Book.BookParseException;
import com.fiskmods.heroes.common.book.Page;
import com.fiskmods.heroes.common.book.json.BookContainer;
import com.google.gson.JsonParseException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.Constants.NBT;

public class GuiDebugBook extends GuiSuperheroesBook
{
    private final File bookFile;
    private Map<File, Long> lastEdited = new HashMap<>();

    public GuiDebugBook(EntityPlayer player, ItemStack itemstack)
    {
        super(player, itemstack, new Book(null).addPage(new Page("\\n\\n\\n\\n\\n       #c#lInvalid book")));
        String path = "";

        if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Path", NBT.TAG_STRING))
        {
            path = itemstack.getTagCompound().getString("Path");
        }

        bookFile = new File(path);
    }

    @Override
    public void updateScreen()
    {
        if (validFile(bookFile))
        {
            boolean flag = lastEdited.isEmpty();

            for (Map.Entry<File, Long> e : lastEdited.entrySet())
            {
                File file = e.getKey();
                long l = e.getValue();

                e.setValue(file.lastModified());

                if (l != file.lastModified() && validFile(file))
                {
                    flag = true;
                    break;
                }
            }

            if (flag)
            {
                lastEdited.clear();
                lastEdited.put(bookFile, bookFile.lastModified());

                try
                {
                    BookContainer container = BookContainer.create(bookFile);

                    if (container != null)
                    {
                        List<File> files = container.book.getChapters(bookFile);

                        for (File chapterFile : files)
                        {
                            lastEdited.put(chapterFile, chapterFile.lastModified());
                        }
                    }

                    book = new Book(container);
                }
                catch (BookParseException e)
                {
                    book = new Book(null);
                    String s = e.getMessage();

                    if (s.contains(": "))
                    {
                        boolean unicode = fontRendererObj.getUnicodeFlag();
                        fontRendererObj.setUnicodeFlag(true);

                        String s1 = s.substring(0, s.indexOf(": "));
                        String s2 = "";
                        int i = fontRendererObj.getStringWidth(EnumChatFormatting.BOLD + s1);

                        while ((BOOK_WIDTH / 2 - 20 - i + fontRendererObj.getStringWidth(s2)) / 2 < 0)
                        {
                            if (s2.isEmpty())
                            {
                                s2 += EnumChatFormatting.BOLD;
                            }

                            s2 += " ";
                        }

                        fontRendererObj.setUnicodeFlag(unicode);
                        book.addPage(new Page("in " + e.fileName + ":\n\n" + s.substring(s.indexOf(": ") + 2), s2 + s1));
                    }
                }
                catch (JsonParseException e)
                {
                    e.printStackTrace();
                }

                currPage = MathHelper.clamp_int(currPage, 0, book.getTotalPagePairs());
                updateButtons();
            }
        }
    }

    public boolean validFile(File file)
    {
        return file != null && file.exists() && file.isFile();
    }
}
