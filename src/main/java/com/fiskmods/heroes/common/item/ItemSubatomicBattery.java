package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.book.widget.IItemListEntry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

public class ItemSubatomicBattery extends Item implements IItemListEntry
{
    public ItemSubatomicBattery()
    {
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b)
    {
        list.add(StatCollector.translateToLocalFormatted("tooltip.subatomicCharge", MathHelper.floor_float(getCharge(itemstack) * 100)));
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item));
        list.add(new ItemStack(item, 1, SHConstants.MAX_SUBATOMIC_CHARGE));
    }

    public static float getCharge(ItemStack itemstack)
    {
        return (float) itemstack.getItemDamage() / SHConstants.MAX_SUBATOMIC_CHARGE;
    }

    @Override
    public void getListItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item, 1, SHConstants.MAX_SUBATOMIC_CHARGE));
    }

    @Override
    public String getPageLink(ItemStack itemstack)
    {
        return itemstack.getUnlocalizedName();
    }
}
