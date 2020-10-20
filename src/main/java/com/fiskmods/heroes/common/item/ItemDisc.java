package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.common.book.widget.IItemListEntry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class ItemDisc extends Item implements IItemListEntry
{
    public static final String[] NAMES = new String[] {"normal", "red", "blue", "light_blue"};

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemDisc()
    {
        setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int metadata)
    {
        int i = MathHelper.clamp_int(metadata, 0, icons.length - 1);
        return icons[i];
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        int i = MathHelper.clamp_int(itemstack.getItemDamage(), 0, NAMES.length - 1);
        return super.getUnlocalizedName() + "_" + NAMES[i];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (int i = 0; i < NAMES.length; ++i)
        {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        icons = new IIcon[NAMES.length];

        for (int i = 0; i < NAMES.length; ++i)
        {
            icons[i] = iconRegister.registerIcon(getIconString() + "_" + NAMES[i]);
        }
    }

    @Override
    public void getListItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item));
    }

    @Override
    public String getPageLink(ItemStack itemstack)
    {
        return itemstack.getUnlocalizedName();
    }
}
