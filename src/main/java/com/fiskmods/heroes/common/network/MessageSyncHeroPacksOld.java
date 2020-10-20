//package com.fiskmods.heroes.common.network;
//
//import java.io.IOException;
//
//import com.fiskmods.heroes.common.hero.Hero;
//import com.fiskmods.heroes.pack.HeroPack.HeroPackException;
//import com.fiskmods.heroes.pack.HeroPackSerializer;
//import com.fiskmods.heroes.pack.JSHeroesEngine;
//import com.fiskmods.heroes.pack.JSHeroesResources;
//
//import fiskfille.core.network.AbstractMessage;
//import io.netty.buffer.ByteBuf;
//
//public class MessageSyncHeroPacks extends AbstractMessage<MessageSyncHeroPacks>
//{
//    private HeroPackSerializer serializer;
//    private boolean resources;
//
//    public MessageSyncHeroPacks()
//    {
//    }
//
//    public MessageSyncHeroPacks(HeroPackSerializer packSerializer, boolean withResources)
//    {
//        serializer = packSerializer;
//        resources = withResources;
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf)
//    {
//        resources = buf.readBoolean();
//
//        if (buf.readBoolean())
//        {
//            try
//            {
//                serializer = new HeroPackSerializer();
//                serializer.fromBytes(buf.slice());
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf)
//    {
//        buf.writeBoolean(resources);
//        buf.writeBoolean(serializer != null);
//
//        if (serializer != null)
//        {
//            try
//            {
//                serializer.toBytes(buf);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void receive() throws MessageException
//    {
//        if (resources || JSHeroesResources.doClientSync())
//        {
//            if (resources)
//            {
//                JSHeroesEngine.INSTANCE.reloadResources();
//            }
//
//            if (serializer != null)
//            {
//                JSHeroesEngine.INSTANCE.lastWorldIsServer = true;
//                new Thread(() ->
//                {
//                    try
//                    {
//                        if (Hero.REGISTRY.reloadFrom(serializer) > 0)
//                        {
//                            JSHeroesResources.syncResources(serializer);
//                        }
//                    }
//                    catch (HeroPackException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }, "Client HeroPack reloader").start();
//            }
//        }
//    }
//}
