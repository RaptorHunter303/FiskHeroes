package com.fiskmods.heroes.common.arrowtype;

import java.util.List;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.item.ItemTrickArrow;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class ArrowTypeFirework extends ArrowType
{
    public static final String FIREWORK_TAG = "{Fireworks:{Flight:3b,Explosions:[{Type:0b,Colors:[15790320,11743532]}]}}";

    public ArrowTypeFirework(Class<? extends EntityTrickArrow> clazz)
    {
        super(clazz);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        ItemStack itemstack = makeItem();

        try
        {
            NBTTagCompound nbt = (NBTTagCompound) JsonToNBT.func_150315_a(FIREWORK_TAG);
            ItemStack firework = new ItemStack(Items.fireworks);

            firework.setTagCompound(nbt);
            ItemTrickArrow.setItem(itemstack, firework);
        }
        catch (NBTException e)
        {
            e.printStackTrace();
        }

        list.add(itemstack);
    }
}
