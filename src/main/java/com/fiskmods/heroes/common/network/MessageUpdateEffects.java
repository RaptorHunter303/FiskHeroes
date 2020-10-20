package com.fiskmods.heroes.common.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fiskmods.heroes.common.data.SHEntityData;
import com.fiskmods.heroes.common.data.effect.StatusEffect;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;

public class MessageUpdateEffects extends AbstractMessage<MessageUpdateEffects>
{
    private int id;
    private List<StatusEffect> activeEffects;

    public MessageUpdateEffects()
    {
    }

    public MessageUpdateEffects(EntityLivingBase entity, List<StatusEffect> list)
    {
        id = entity.getEntityId();
        activeEffects = list;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        activeEffects = new ArrayList<>();
        int i = buf.readByte();

        for (int j = 0; j < i; ++j)
        {
            StatusEffect effect = StatusEffect.fromBytes(buf);

            if (effect != null)
            {
                activeEffects.add(effect);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeByte(activeEffects.size());

        for (StatusEffect effect : activeEffects)
        {
            effect.toBytes(buf);
        }
    }

    @Override
    public void receive() throws MessageException
    {
        SHEntityData.getData(getEntity(EntityLivingBase.class, id)).activeEffects = activeEffects;

        if (!activeEffects.isEmpty())
        {
            Collections.sort(activeEffects);
        }
    }
}
