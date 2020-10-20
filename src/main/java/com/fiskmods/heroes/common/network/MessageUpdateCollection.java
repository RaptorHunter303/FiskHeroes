package com.fiskmods.heroes.common.network;

import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.data.SHPlayerData;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageUpdateCollection extends AbstractMessage<MessageUpdateCollection>
{
    private Map<ArrowType, Integer> arrowCollection;

    public MessageUpdateCollection()
    {
    }

    public MessageUpdateCollection(EntityPlayer player)
    {
        arrowCollection = SHPlayerData.getData(player).arrowCollection;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        arrowCollection = new HashMap<>();
        int i = buf.readShort();

        for (int j = 0; j < i; ++j)
        {
            ArrowType arrow = ArrowType.getArrowById(buf.readShort());
            int b = buf.readByte();

            if (arrow != null)
            {
                arrowCollection.put(arrow, b);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeShort(arrowCollection.size());

        for (Map.Entry<ArrowType, Integer> e : arrowCollection.entrySet())
        {
            buf.writeShort(ArrowType.getIdFromArrow(e.getKey()));
            buf.writeByte(e.getValue());
        }
    }

    @Override
    public void receive() throws MessageException
    {
        SHPlayerData.getData(getPlayer()).arrowCollection = arrowCollection;
    }
}
