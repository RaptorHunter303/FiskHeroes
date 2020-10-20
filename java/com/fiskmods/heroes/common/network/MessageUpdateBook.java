package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.common.item.ItemMetahumanLog;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MessageUpdateBook extends AbstractMessage<MessageUpdateBook>
{
    private NBTTagCompound nbt;

    public MessageUpdateBook()
    {
    }

    public MessageUpdateBook(NBTTagCompound tag)
    {
        nbt = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, nbt);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityPlayer player = getPlayer();
        ItemStack heldItem = player.getHeldItem();

        if (heldItem != null && heldItem.getItem() instanceof ItemMetahumanLog)
        {
            heldItem.setTagCompound(nbt);
        }
    }
}
