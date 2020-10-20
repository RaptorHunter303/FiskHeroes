package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemTachyonDevice extends ItemUntextured implements ITachyonCharged, IEquipmentItem
{
    public ItemTachyonDevice()
    {
        setMaxStackSize(1);
    }

    @Override
    public int getTachyonMaxCharge(ItemStack itemstack)
    {
        return SHConstants.MAX_CHARGE_DEVICE;
    }

    @Override
    public boolean isTachyonBattery(ItemStack itemstack)
    {
        return true;
    }

    @Override
    public boolean renderTachyonBar(ItemStack itemstack)
    {
        return true;
    }

    @Override
    public boolean canEquip(ItemStack itemstack, TileEntityDisplayStand tile)
    {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b)
    {
        if (renderTachyonBar(itemstack))
        {
            list.add(getChargeForDisplay(itemstack));
        }
    }

    public static String getChargeForDisplay(ItemStack itemstack)
    {
        return StatCollector.translateToLocalFormatted("tooltip.tachyonCharge", getCharge(itemstack), getMaxCharge(itemstack));
    }

    public static int getCharge(ItemStack itemstack)
    {
        if (itemstack.getItem() instanceof ITachyonCharged)
        {
            if (itemstack.getItem() instanceof ItemTachyonDevice && (!itemstack.hasTagCompound() || !itemstack.getTagCompound().hasKey("TachyonCharge", NBT.TAG_INT)))
            {
                setCharge(itemstack, getMaxCharge(itemstack) - itemstack.getItemDamage());
                itemstack.setItemDamage(0);
            }

            if (itemstack.hasTagCompound())
            {
                return itemstack.getTagCompound().getInteger("TachyonCharge");
            }
        }

        return 0;
    }

    public static boolean setCharge(ItemStack itemstack, int charge)
    {
        if (itemstack.getItem() instanceof ITachyonCharged)
        {
            if (!itemstack.hasTagCompound())
            {
                itemstack.setTagCompound(new NBTTagCompound());
            }

            int i = MathHelper.clamp_int(charge, 0, getMaxCharge(itemstack));
            itemstack.getTagCompound().setInteger("TachyonCharge", i);

            return i == charge;
        }

        return false;
    }

    public static int getMaxCharge(ItemStack itemstack)
    {
        if (itemstack.getItem() instanceof ITachyonCharged)
        {
            return ((ITachyonCharged) itemstack.getItem()).getTachyonMaxCharge(itemstack);
        }

        return 0;
    }

    public static boolean isBattery(ItemStack itemstack)
    {
        if (itemstack.getItem() instanceof ITachyonCharged)
        {
            return ((ITachyonCharged) itemstack.getItem()).isTachyonBattery(itemstack);
        }

        return false;
    }
}
