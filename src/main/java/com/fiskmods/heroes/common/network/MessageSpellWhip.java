package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.common.entity.EntitySpellWhip;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class MessageSpellWhip extends AbstractMessage<MessageSpellWhip>
{
    private int id, targetId;

    public MessageSpellWhip()
    {
    }

    public MessageSpellWhip(EntitySpellWhip entity, Entity target)
    {
        id = entity.getEntityId();
        targetId = target.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        targetId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeInt(targetId);
    }

    @Override
    public void receive() throws MessageException
    {
        getEntity(EntitySpellWhip.class, id).hookedEntity = getEntity(Entity.class, targetId);
    }
}
