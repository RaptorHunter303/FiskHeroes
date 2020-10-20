package com.fiskmods.heroes.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class MessageUpdateStepHeight extends AbstractMessage<MessageUpdateStepHeight>
{
    private int id;
    private float stepHeight;

    public MessageUpdateStepHeight()
    {
    }

    public MessageUpdateStepHeight(Entity entity, float height)
    {
        id = entity.getEntityId();
        stepHeight = height;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        stepHeight = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeFloat(stepHeight);
    }

    @Override
    public void receive() throws MessageException
    {
        getEntity(Entity.class, id).stepHeight = stepHeight;
    }
}
