package com.fiskmods.heroes.common.item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.gui.book.CactusParser;
import com.fiskmods.heroes.common.book.Book;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemCactusJournal extends ItemMetahumanLog
{
    public static final String TAG_ENTRIES = "Entries";

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        player.openGui(FiskHeroes.MODID, 4, world, 2, 0, 0);
        return itemstack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag)
    {
        byte[] entries = getEntries(stack);

        if (entries.length > 0)
        {
            List<Byte> list1 = new ArrayList<>();

            for (byte entrie : entries)
            {
                list1.add(entrie);
            }

            list1.sort(Comparator.naturalOrder());

            if (list1.size() > 5)
            {
                StringBuilder s = new StringBuilder();

                for (Byte b : list1)
                {
                    s.append(b).append(", ");
                }

                list.add(s.substring(0, s.length() - 2));
            }
            else
            {
                for (Byte b : list1)
                {
                    list.add(StatCollector.translateToLocalFormatted(ModItems.journalEntry.getUnlocalizedName() + ".desc", b & 0xFF));
                }
            }
        }

//        list.add(StatCollector.translateToLocalFormatted(getUnlocalizedName() + ".desc", stack.getItemDamage()));
    }

    public static Book getBook(ItemStack stack)
    {
        // return get(FiskHeroes.MODID, "cactus_journal");
        return new Book(FiskHeroes.MODID, "cactus_journal", new CactusParser(stack));
    }

    public static ItemStack setEntries(ItemStack stack, byte[] entries)
    {
        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setByteArray(TAG_ENTRIES, entries);
        return stack;
    }

    public static byte[] getEntries(ItemStack stack)
    {
        return stack.hasTagCompound() ? stack.getTagCompound().getByteArray(TAG_ENTRIES) : new byte[0];
    }

    public static boolean hasEntry(ItemStack stack, int entry)
    {
        byte[] entries = getEntries(stack);

        for (byte entrie : entries)
        {
            if ((entrie & 0xFF) == entry)
            {
                return true;
            }
        }

        return false;
    }
}
