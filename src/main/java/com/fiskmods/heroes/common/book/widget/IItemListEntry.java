package com.fiskmods.heroes.common.book.widget;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemListEntry
{
    void getListItems(Item item, CreativeTabs tab, List list);

    String getPageLink(ItemStack itemstack);
}
