package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.common.container.ContainerSuitIterator;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageSuitIteration extends AbstractMessage<MessageSuitIteration>
{
    private int id;
    private int iterationId;

    public MessageSuitIteration()
    {
    }

    public MessageSuitIteration(EntityPlayer player, int iteration)
    {
        id = player.getEntityId();
        iterationId = iteration;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        iterationId = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeByte(iterationId);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityPlayer player = getPlayer(id);

        if (player.openContainer instanceof ContainerSuitIterator)
        {
            ((ContainerSuitIterator) player.openContainer).updateIteration(iterationId);
        }
    }
}
