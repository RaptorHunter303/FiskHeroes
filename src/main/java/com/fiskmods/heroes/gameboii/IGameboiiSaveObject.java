package com.fiskmods.heroes.gameboii;

import java.nio.ByteBuffer;

public interface IGameboiiSaveObject
{
    void read(ByteBuffer buf, int protocol);

    void write(ByteBuffer buf);
}
