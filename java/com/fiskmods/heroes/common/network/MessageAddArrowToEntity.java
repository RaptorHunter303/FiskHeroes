package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.data.DataManager;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;

public class MessageAddArrowToEntity extends AbstractMessage<MessageAddArrowToEntity>
{
    private int id;
    private int arrowId;

    public MessageAddArrowToEntity()
    {
    }

    public MessageAddArrowToEntity(EntityLivingBase entity, EntityTrickArrow arrow)
    {
        id = entity.getEntityId();
        arrowId = arrow.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        arrowId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeInt(arrowId);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityLivingBase entity = getEntity(EntityLivingBase.class, id);
        EntityTrickArrow arrow = getEntity(EntityTrickArrow.class, arrowId);
        ArrowType type = ArrowType.getArrowById(arrow.getArrowId());

        if (type != null)
        {
            DataManager.addArrowToEntity(entity, type, arrow);
        }
    }
}
