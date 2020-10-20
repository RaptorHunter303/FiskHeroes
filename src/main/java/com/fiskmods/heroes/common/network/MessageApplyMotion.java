package com.fiskmods.heroes.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;

public class MessageApplyMotion extends AbstractMessage<MessageApplyMotion>
{
    private double motionX, motionY, motionZ;

    public MessageApplyMotion()
    {
    }

    public MessageApplyMotion(double motX, double motY, double motZ)
    {
        motionX = motX;
        motionY = motY;
        motionZ = motZ;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        motionX = buf.readFloat();
        motionY = buf.readFloat();
        motionZ = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat((float) motionX);
        buf.writeFloat((float) motionY);
        buf.writeFloat((float) motionZ);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityLivingBase entity = getPlayer();
        entity.motionX += motionX;
        entity.motionY += motionY;
        entity.motionZ += motionZ;
    }
}
