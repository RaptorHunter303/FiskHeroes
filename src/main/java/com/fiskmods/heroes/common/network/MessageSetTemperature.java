package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.util.TemperatureHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;

public class MessageSetTemperature extends AbstractMessage<MessageSetTemperature>
{
    private int id;
    private float temperature;

    public MessageSetTemperature()
    {
    }

    public MessageSetTemperature(EntityLivingBase entity, float f)
    {
        id = entity.getEntityId();
        temperature = f;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        temperature = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeFloat(temperature);
    }

    @Override
    public void receive() throws MessageException
    {
        TemperatureHelper.setTemperature(getEntity(EntityLivingBase.class, id), temperature);
    }
}
