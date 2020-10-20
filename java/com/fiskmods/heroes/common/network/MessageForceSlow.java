package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.common.data.world.SHMapData;

import io.netty.buffer.ByteBuf;

public class MessageForceSlow extends AbstractMessage<MessageForceSlow>
{
    private float forceSlow;

    public MessageForceSlow()
    {
    }

    public MessageForceSlow(float f)
    {
        forceSlow = f;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        forceSlow = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat(forceSlow);
    }

    @Override
    public void receive() throws MessageException
    {
        SHMapData.get(getWorld()).forceSlow = forceSlow;
    }
}
