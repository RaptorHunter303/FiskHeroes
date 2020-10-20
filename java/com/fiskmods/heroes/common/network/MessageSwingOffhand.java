package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageSwingOffhand extends AbstractMessage<MessageSwingOffhand>
{
    private int id;

    public MessageSwingOffhand()
    {
    }

    public MessageSwingOffhand(EntityPlayer player)
    {
        id = player.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityPlayer player = getSender(id);

        if (!FiskHeroes.proxy.isClientPlayer(player))
        {
            SHHelper.swingOffhandItem(player);
        }

        if (context.side.isServer())
        {
            SHNetworkManager.wrapper.sendToAllAround(new MessageSwingOffhand(player), new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 64));
        }
    }
}
