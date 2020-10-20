package com.fiskmods.heroes.common.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fiskmods.heroes.common.book.BookHandler;
import com.fiskmods.heroes.common.book.json.BookContainer;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.config.RuleHandler;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.config.SyncedConfig;
import com.fiskmods.heroes.common.data.DataManager;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.SHEntityData;
import com.fiskmods.heroes.util.NBTHelper;
import com.fiskmods.heroes.util.TemperatureHelper;
import com.google.gson.Gson;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessagePlayerJoin extends MessageSyncBase<MessagePlayerJoin>
{
    private final Map<String, BookContainer> books = new HashMap<>();
    private final Map<Rule, Object> rules = new HashMap<>();

    private SyncedConfig config;
    private int serverArrows;

    public MessagePlayerJoin()
    {
    }

    public MessagePlayerJoin(EntityPlayer player)
    {
        super(player);
        config = SHConfig.get();
        serverArrows = DataManager.serverArrows;
        books.putAll(BookHandler.INSTANCE.books);

        for (Rule rule : Rule.REGISTRY)
        {
            if (rule.requiresClientSync)
            {
                rules.put(rule, RuleHandler.getGlobal().get(rule));
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        Gson gson = new Gson();
        int i = buf.readByte();

        for (int j = 0; j < i; ++j)
        {
            books.put(ByteBufUtils.readUTF8String(buf), gson.fromJson(ByteBufUtils.readUTF8String(buf), BookContainer.class));
        }

        i = buf.readByte();

        for (int j = 0; j < i; ++j)
        {
            Rule rule = Rule.REGISTRY.getObjectById(buf.readByte());
            rules.put(rule, NBTHelper.fromBytes(buf, rule.getType()));
        }

        config = new SyncedConfig().fromBytes(buf);
        serverArrows = buf.readShort();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        Gson gson = new Gson();
        buf.writeByte(books.size());

        for (Map.Entry<String, BookContainer> e : books.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, e.getKey());
            ByteBufUtils.writeUTF8String(buf, gson.toJson(e.getValue()));
        }

        buf.writeByte(rules.size());

        for (Map.Entry<Rule, Object> e : rules.entrySet())
        {
            buf.writeByte(Rule.REGISTRY.getIDForObject(e.getKey()));
            NBTHelper.toBytes(buf, e.getValue());
        }

        SHConfig.get().toBytes(buf);
        buf.writeShort(serverArrows);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityPlayer player = getPlayer();

        SHConfig.get().set(config);
        DataManager.serverArrows = serverArrows;

        for (Entry<SHData, Object> e : playerData.entrySet())
        {
            e.getKey().setWithoutNotify(player, e.getValue());
        }

        if (!activeEffects.isEmpty())
        {
            Collections.sort(activeEffects);
        }

        SHEntityData.getData(player).activeEffects = activeEffects;
        TemperatureHelper.setTemperature(player, temperature);

        DataManager.setHeroCollection(player, heroCollection);
        DataManager.setArrowCollection(player, arrowCollection);
        BookHandler.INSTANCE.books = books;

        for (Map.Entry<Rule, Object> e : rules.entrySet())
        {
            RuleHandler.getGlobal().put(e.getKey(), e.getValue());
        }
    }
}
