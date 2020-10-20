package com.fiskmods.heroes.common.item;

import java.io.File;
import java.util.List;

import com.fiskmods.heroes.FiskHeroes;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemDebugBook extends ItemMetahumanLog
{
    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag)
    {
        if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Path", NBT.TAG_STRING))
        {
            String s = itemstack.getTagCompound().getString("Path");
            File file = new File(s);

            String[] astring = s.split("\\\\");

            if (astring != null)
            {
                int max = 3;
                s = "";

                for (int i = astring.length - 1; i >= Math.max(astring.length - max, 0); --i)
                {
                    s = "/" + astring[i] + s;
                }

                if (!s.isEmpty() && astring.length - 1 < max)
                {
                    s = s.substring(1);
                }
                else
                {
                    s = "..." + s;
                }
            }

            list.add(s);

            if (!file.isFile() || !file.exists())
            {
                list.add(EnumChatFormatting.RED + I18n.format("item.debug_book.invalid"));
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        player.openGui(FiskHeroes.MODID, 4, world, 1, itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Path", NBT.TAG_STRING) ? 1 : 0, 0);
        return itemstack;
    }
}
