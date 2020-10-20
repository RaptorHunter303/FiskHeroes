package com.fiskmods.heroes.common.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class MessageUpdateArmor extends AbstractMessage<MessageUpdateArmor>
{
    private int id;
    private ItemStack[] armor = new ItemStack[4];

    public MessageUpdateArmor()
    {
    }

    public MessageUpdateArmor(EntityPlayer player)
    {
        id = player.getEntityId();
        armor = player.inventory.armorInventory;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();

        for (int i = 0; i < armor.length; ++i)
        {
            armor[i] = ByteBufUtils.readItemStack(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);

        for (ItemStack element : armor)
        {
            ByteBufUtils.writeItemStack(buf, element);
        }
    }

    @Override
    public void receive() throws MessageException
    {
        getPlayer(id).inventory.armorInventory = armor;
    }
}
