package com.fiskmods.heroes.common.gameboii;

import com.fiskmods.heroes.gameboii.GameboiiCartridge;
import com.fiskmods.heroes.gameboii.GameboiiSave;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GameboiiSaveItemStack extends GameboiiSave
{
    public final EntityPlayer player;

    public GameboiiSaveItemStack(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public void saveData(byte[] data, GameboiiCartridge cartridge) throws Exception
    {
        ItemStack stack = player.getHeldItem();

        if (stack != null)
        {
            if (!stack.hasTagCompound())
            {
                stack.setTagCompound(new NBTTagCompound());
            }

            stack.getTagCompound().setByteArray(cartridge.id, data);
        }
    }

    @Override
    public byte[] loadData(GameboiiCartridge cartridge) throws Exception
    {
        ItemStack stack = player.getHeldItem();

        if (stack != null && stack.hasTagCompound())
        {
            NBTTagCompound compound = stack.getTagCompound();
            return compound.getByteArray(cartridge.id);
        }

        return null;
    }
}
