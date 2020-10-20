package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.common.book.widget.IItemListEntry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemDisplayStand extends ItemBlock implements IItemListEntry
{
    public ItemDisplayStand(Block block)
    {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        int i = ItemDye.field_150923_a.length - 1;
        return super.getUnlocalizedName() + "." + ItemDye.field_150923_a[MathHelper.clamp_int(i - itemstack.getItemDamage(), 0, i)];
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemstack)
    {
        if (itemstack.hasTagCompound())
        {
            if (itemstack.getTagCompound().hasKey("Username", NBT.TAG_COMPOUND))
            {
                return StatCollector.translateToLocalFormatted(super.getUnlocalizedName() + ".player.name", NBTUtil.func_152459_a(itemstack.getTagCompound().getCompoundTag("Username")).getName());
            }

            if (itemstack.getTagCompound().hasKey("Username", NBT.TAG_STRING))
            {
                return StatCollector.translateToLocalFormatted(super.getUnlocalizedName() + ".player.name", itemstack.getTagCompound().getString("Username"));
            }
        }

        return super.getItemStackDisplayName(itemstack);
    }

    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }

    @Override
    public void getListItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item));
    }

    @Override
    public String getPageLink(ItemStack itemstack)
    {
        return super.getUnlocalizedName();
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
    }
}
