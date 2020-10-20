package com.fiskmods.heroes.common.book;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.common.book.widget.IItemListEntry;
import com.fiskmods.heroes.util.FiskPredicates;
import com.google.common.collect.Iterables;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IPageParser
{
    boolean parseGenerated(String id, List<Page> queue);

    public class Impl implements IPageParser
    {
        @Override
        public boolean parseGenerated(String id, List<Page> queue)
        {
            if (id.startsWith("itemlist"))
            {
                id = id.substring("itemlist".length());
                List<ItemStack> stacks = new ArrayList<>();
                Iterable<Item> iter = Item.itemRegistry;
                int index = 0;

                if (id.startsWith("[") && id.endsWith("]"))
                {
                    String s = id.substring(1, id.length() - 1);
                    iter = Iterables.filter(iter, t -> FiskPredicates.forInput(Item.class, s).test(t));
                }

                for (Item item : iter)
                {
                    if (item instanceof IItemListEntry)
                    {
                        ((IItemListEntry) item).getListItems(item, item.getCreativeTab(), stacks);
                    }
                    else
                    {
                        item.getSubItems(item, item.getCreativeTab(), stacks);
                    }
                }

                for (ItemStack itemstack : stacks)
                {
                    if (index % 42 == 0)
                    {
                        queue.add(new PageItemList(stacks, index));
                    }

                    ++index;
                }

                return true;
            }

            return false;
        }
    }
}
