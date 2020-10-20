package com.fiskmods.heroes.common.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.SHPlayerData;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.TemperatureHelper;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public abstract class MessageSyncBase<REQ extends MessageSyncBase> extends AbstractMessage<REQ>
{
    protected Map<SHData, Object> playerData;
    protected Map<Hero, Byte> heroCollection;
    protected Map<ArrowType, Integer> arrowCollection;

    protected List<StatusEffect> activeEffects;
    protected float temperature;

    public MessageSyncBase()
    {
    }

    protected MessageSyncBase(EntityPlayer player)
    {
        playerData = SHPlayerData.getData(player).data;
        heroCollection = SHPlayerData.getData(player).heroCollection;
        arrowCollection = SHPlayerData.getData(player).arrowCollection;

        activeEffects = StatusEffect.get(player);
        temperature = TemperatureHelper.getTemperature(player);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        playerData = SHData.fromBytes(buf, new HashMap<>());
        heroCollection = new HashMap<>();
        arrowCollection = new HashMap<>();
        activeEffects = new ArrayList<>();
        int i = buf.readShort();

        for (int j = 0; j < i; ++j)
        {
            Hero hero = Hero.getHeroFromName(ByteBufUtils.readUTF8String(buf));
            byte b = buf.readByte();

            if (hero != null)
            {
                heroCollection.put(hero, b);
            }
        }

        i = buf.readShort();

        for (int j = 0; j < i; ++j)
        {
            ArrowType arrow = ArrowType.getArrowById(buf.readShort());
            int b = buf.readByte();

            if (arrow != null)
            {
                arrowCollection.put(arrow, b);
            }
        }

        temperature = buf.readFloat();
        i = buf.readByte();

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
        SHData.toBytes(buf, playerData);
        buf.writeShort(heroCollection.size());

        for (Map.Entry<Hero, Byte> e : heroCollection.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, Hero.getNameForHero(e.getKey()));
            buf.writeByte(e.getValue());
        }

        buf.writeShort(arrowCollection.size());

        for (Map.Entry<ArrowType, Integer> e : arrowCollection.entrySet())
        {
            buf.writeShort(ArrowType.getIdFromArrow(e.getKey()));
            buf.writeByte(e.getValue());
        }

        buf.writeFloat(temperature);
        buf.writeByte(activeEffects.size());

        for (StatusEffect effect : activeEffects)
        {
            effect.toBytes(buf);
        }
    }
}
