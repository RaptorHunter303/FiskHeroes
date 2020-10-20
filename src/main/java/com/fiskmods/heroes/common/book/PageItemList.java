package com.fiskmods.heroes.common.book;

import net.minecraft.item.ItemStack;

public class PageItemList extends Page
{
    public Iterable<ItemStack> itemList;
    public int startIndex;

    public PageItemList(Iterable<ItemStack> iterable, int index)
    {
        itemList = iterable;
        startIndex = index;
    }
}
