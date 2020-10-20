package com.fiskmods.heroes.common.network;

import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.util.FiskServerUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class FiskNetworkHelper
{
    private static Map<String, SimpleNetworkWrapper> wrappers = new HashMap<>();
    private static Map<String, Integer> ids = new HashMap<>();

    public static <REQ extends IMessage & IMessageHandler<REQ, IMessage>> void registerMessage(Class<REQ> msg)
    {
        registerMessage(msg, Side.CLIENT);
        registerMessage(msg, Side.SERVER);
    }

    public static <REQ extends IMessage & IMessageHandler<REQ, IMessage>> void registerMessage(Class<REQ> msg, Side side)
    {
        String modid = FiskServerUtils.getActiveModId();
        getWrapper(modid).registerMessage(msg, msg, getNextId(modid), side);
    }

    public static SimpleNetworkWrapper getWrapper(String modid)
    {
        SimpleNetworkWrapper wrapper = wrappers.get(modid);

        if (wrapper == null)
        {
            wrappers.put(modid, wrapper = new SimpleNetworkWrapper(modid));
        }

        return wrapper;
    }

    protected static int getNextId(String modid)
    {
        Integer id = ids.get(modid) != null ? ids.get(modid) : 0;
        ids.put(modid, id + 1);

        return id;
    }
}
