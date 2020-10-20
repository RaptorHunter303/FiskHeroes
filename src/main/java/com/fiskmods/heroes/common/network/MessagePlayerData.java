package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.util.NBTHelper;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessagePlayerData extends AbstractMessage<MessagePlayerData>
{
    private int id;
    private SHData type;
    private Object value;

    public MessagePlayerData()
    {
    }

    public MessagePlayerData(EntityPlayer player, SHData data, Object obj)
    {
        id = player.getEntityId();
        type = data;
        value = obj;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        type = SHData.REGISTRY.getObjectById(buf.readShort());
        value = NBTHelper.fromBytes(buf, type.typeClass);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeShort(SHData.REGISTRY.getIDForObject(type));
        NBTHelper.toBytes(buf, value);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityPlayer player = getSender(id);
        Side senderSide = context.side.isClient() ? Side.SERVER : Side.CLIENT;

        if (!type.hasPerms(senderSide))
        {
            FiskHeroes.LOGGER.warn("Player {} tried to set {} to {} on illegal side {}!", player.getCommandSenderName(), type, value, senderSide);
            return;
        }

        if (context.side.isClient())
        {
            type.setWithoutNotify(player, value);
        }
        else
        {
            type.set(player, value);
        }
    }
}
