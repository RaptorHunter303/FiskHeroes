package com.fiskmods.heroes.common.network;

import java.util.Collections;
import java.util.Map.Entry;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.data.DataManager;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.SHEntityData;
import com.fiskmods.heroes.util.TemperatureHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageBroadcastState extends MessageSyncBase<MessageBroadcastState>
{
    private int id;

    public MessageBroadcastState()
    {
    }

    public MessageBroadcastState(EntityPlayer player)
    {
        super(player);
        id = player.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        super.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        super.toBytes(buf);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityPlayer player = getSender(id);

        if (context.side.isClient() && FiskHeroes.proxy.isClientPlayer(player))
        {
            return;
        }

        for (Entry<SHData, Object> e : playerData.entrySet())
        {
            e.getKey().setWithoutNotify(player, e.getValue());
        }

        if (!activeEffects.isEmpty())
        {
            Collections.sort(activeEffects);
        }

        SHEntityData.getData(player).activeEffects = activeEffects;
        TemperatureHelper.setTemperatureWithoutNotify(player, temperature);

        DataManager.setHeroCollection(player, heroCollection);
        DataManager.setArrowCollection(player, arrowCollection);

        if (context.side.isServer())
        {
            SHNetworkManager.wrapper.sendToDimension(new MessageBroadcastState(player), player.dimension);
        }
    }
}
