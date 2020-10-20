package com.fiskmods.heroes.common.network;

import java.util.Iterator;

import com.fiskmods.heroes.common.data.DataManager;
import com.fiskmods.heroes.common.data.arrow.GrappleArrowData;
import com.fiskmods.heroes.common.data.arrow.IArrowData;
import com.fiskmods.heroes.common.entity.arrow.EntityGrappleArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;

public class MessageGrappleArrowCut extends AbstractMessage<MessageGrappleArrowCut>
{
    private int id;
    private int arrowId;

    public MessageGrappleArrowCut()
    {
    }

    public MessageGrappleArrowCut(EntityLivingBase entity, EntityGrappleArrow arrow)
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
        Iterator<IArrowData> iter = DataManager.getArrowsInEntity(entity).iterator();

        for (IArrowData data = null; iter.hasNext(); data = iter.next())
        {
            if (data instanceof GrappleArrowData)
            {
                EntityTrickArrow arrow = data.getEntity(entity.worldObj);

                if (arrow != null && arrow.getEntityId() == arrowId)
                {
                    ((GrappleArrowData) data).setIsCableCut(true);
                }
            }
        }
    }
}
