package com.fiskmods.heroes.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemJournalEntry extends Item
{
    public ItemJournalEntry()
    {
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag)
    {
        list.add(StatCollector.translateToLocalFormatted(getUnlocalizedName() + ".desc", stack.getItemDamage()));
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
    }
}
