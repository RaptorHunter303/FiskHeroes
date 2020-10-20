package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.common.achievement.SHAchievements;
import com.fiskmods.heroes.common.data.SHData;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageBatfish extends AbstractMessage<MessageBatfish>
{
    private int action;

    public MessageBatfish()
    {
    }

    public MessageBatfish(int action)
    {
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        action = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(action);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityPlayer player = getPlayer();

        switch (action)
        {
        case 0:
            player.triggerAchievement(SHAchievements.BATFISH);
            break;
        case 1:
            player.triggerAchievement(SHAchievements.SPODERMEN);
            SHData.SPODERMEN.set(player, true);
            break;
        }
    }
}
