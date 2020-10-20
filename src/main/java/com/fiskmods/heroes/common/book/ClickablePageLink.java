package com.fiskmods.heroes.common.book;

import com.fiskmods.heroes.client.gui.GuiSuperheroesBook;

import net.minecraft.util.EnumChatFormatting;

public class ClickablePageLink extends Clickable
{
    private GuiSuperheroesBook parent;
    private String transferId;

    public ClickablePageLink(GuiSuperheroesBook book, String id)
    {
        parent = book;
        transferId = id;

        setColor(book.book.getPageFromId(id) == null ? EnumChatFormatting.DARK_RED : EnumChatFormatting.BLUE);
    }

    @Override
    public void execute(int button)
    {
        Page page = parent.book.getPageFromId(transferId);

        if (page != null)
        {
            parent.currPage = page.pageNumber / 2 - 1 + page.pageNumber % 2;
            parent.updateButtons();
        }
    }
}
