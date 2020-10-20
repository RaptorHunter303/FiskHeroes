package com.fiskmods.heroes.pack;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBufSlicer
{
    public static List<ByteBuf> slice(ByteBuf buf, int maxSize)
    {
        List<ByteBuf> list = new ArrayList<>();

        while (buf.isReadable())
        {
            list.add(buf.readSlice(Math.min(maxSize, buf.readableBytes())));
        }

        return list;
    }

    public static ByteBuf merge(List<ByteBuf> list)
    {
        ByteBuf buf = Unpooled.buffer();

        for (ByteBuf slice : list)
        {
            buf.writeBytes(slice, slice.readableBytes());
        }

        return buf;
    }
}
