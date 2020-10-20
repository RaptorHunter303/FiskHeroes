package com.fiskmods.heroes.common.network;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;

import com.fiskmods.heroes.pack.ByteBufSlicer;
import com.fiskmods.heroes.pack.HeroPackSerializer;
import com.fiskmods.heroes.pack.JSHeroesEngine;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MessageSyncHeroPacks extends AbstractMessage<MessageSyncHeroPacks>
{
    public static void sync(HeroPackSerializer serializer, boolean resources, Consumer<IMessage> sendFunc)
    {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBoolean(resources);
        buf.writeBoolean(serializer != null);

        if (serializer != null)
        {
            try
            {
                serializer.toBytes(buf);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Iterator<ByteBuf> iter = ByteBufSlicer.slice(buf, 2097048).iterator();

        while (iter.hasNext())
        {
            sendFunc.accept(new MessageSyncHeroPacks(iter.next(), !iter.hasNext()));
        }
    }

    private ByteBuf slice;
    private boolean hasNext;

    public MessageSyncHeroPacks()
    {
    }

    private MessageSyncHeroPacks(ByteBuf buf, boolean flag)
    {
        slice = buf;
        hasNext = flag;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        hasNext = buf.readBoolean();
        slice = buf.slice();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(hasNext);
        buf.writeBytes(slice, slice.readableBytes());
    }

    @Override
    public void receive() throws MessageException
    {
        JSHeroesEngine.INSTANCE.receive(slice, hasNext);
    }
}
