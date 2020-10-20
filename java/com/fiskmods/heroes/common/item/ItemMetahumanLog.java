package com.fiskmods.heroes.common.item;

import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.book.Book;
import com.fiskmods.heroes.common.book.BookHandler;
import com.fiskmods.heroes.common.book.json.BookContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemMetahumanLog extends Item
{
    public static final Map<ResourceLocation, Book> CACHE = new HashMap<>();

    public ItemMetahumanLog()
    {
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        player.openGui(FiskHeroes.MODID, 4, world, 0, 0, 0);
        return itemstack;
    }

    public static Book getBook(ItemStack itemstack)
    {
        if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Book", NBT.TAG_STRING))
        {
            String key = itemstack.getTagCompound().getString("Book");

            for (Map.Entry<String, BookContainer> e : BookHandler.INSTANCE.books.entrySet())
            {
                if (e.getKey().equals(key))
                {
                    return get(e.getKey(), e.getValue());
                }
            }
        }

        return get(FiskHeroes.MODID, "metahuman_log");
    }

    public static Book get(String modid, String path)
    {
        ResourceLocation location = new ResourceLocation(modid, path);

        if (CACHE.containsKey(location))
        {
            return CACHE.get(location);
        }

        Book book = new Book(modid, path);
        CACHE.put(location, book);

        return book;
    }

    public static Book get(String path, BookContainer container)
    {
        ResourceLocation location = new ResourceLocation(path);

        if (CACHE.containsKey(location))
        {
            return CACHE.get(location);
        }

        Book book = new Book(container);
        CACHE.put(location, book);

        return book;
    }

    public static void clearCache(String domain)
    {
        CACHE.entrySet().removeIf(e -> domain.equals(e.getKey().getResourceDomain()));
    }
}
