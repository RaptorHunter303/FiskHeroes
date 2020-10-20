package com.fiskmods.heroes.common.network;

import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.config.RuleHandler;
import com.fiskmods.heroes.util.NBTHelper;

import io.netty.buffer.ByteBuf;

public class MessageSyncRules extends AbstractMessage<MessageSyncRules>
{
    private final Map<Rule, Object> rules = new HashMap<>();

    public MessageSyncRules()
    {
    }

    public MessageSyncRules(Map<Rule, Object> map)
    {
        rules.putAll(map);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int i = buf.readByte() & 0xFF;

        for (int j = 0; j < i; ++j)
        {
            Rule rule = Rule.REGISTRY.getObjectById(buf.readByte() & 0xFF);
            rules.put(rule, NBTHelper.fromBytes(buf, rule.getType()));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(rules.size());

        for (Map.Entry<Rule, Object> e : rules.entrySet())
        {
            buf.writeByte(Rule.REGISTRY.getIDForObject(e.getKey()));
            NBTHelper.toBytes(buf, e.getValue());
        }
    }

    @Override
    public void receive() throws MessageException
    {
        for (Map.Entry<Rule, Object> e : rules.entrySet())
        {
            RuleHandler.getGlobal().put(e.getKey(), e.getValue());
        }
    }
}
