package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.util.SHHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class MessageKnockback extends AbstractMessage<MessageKnockback>
{
    private int id;
    private int attackerId;
    private float amount;

    public MessageKnockback()
    {
    }

    public MessageKnockback(EntityLivingBase entity, Entity attacker, float f)
    {
        id = entity.getEntityId();
        attackerId = attacker.getEntityId();
        amount = f;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        attackerId = buf.readInt();
        amount = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeInt(attackerId);
        buf.writeFloat(amount);
    }

    @Override
    public void receive() throws MessageException
    {
        SHHelper.knockbackWithoutNotify(getEntity(EntityLivingBase.class, id), getEntity(EntityLivingBase.class, attackerId), amount);
    }
}
