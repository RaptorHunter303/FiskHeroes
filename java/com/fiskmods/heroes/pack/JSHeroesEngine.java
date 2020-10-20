package com.fiskmods.heroes.pack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.move.MoveSet;
import com.fiskmods.heroes.common.network.MessageSyncHeroPacks;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.pack.HeroPack.HeroPackException;
import com.fiskmods.heroes.util.FileHelper;
import com.google.gson.Gson;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public enum JSHeroesEngine
{
    INSTANCE;

    public static final Logger LOGGER = LogManager.getLogger("JSHeroesEngine");

    public static final String HERO_DIR = "data/heroes/";
    public static final String MOVESET_DIR = "data/movesets/";
    public static final String HEROPACKS_DIR = "fiskheroes/";
    public static final String PACK_PROPERTIES = HEROPACKS_DIR + "heropacks.properties";

    private final ScriptEngineManager manager;
    private final ScriptEngine engine;
    private final Invocable inv;

    private AbstractHeroPackSerializer lastPackData;
    IAssetSnooper packResources;

    public boolean lastWorldIsServer;

    boolean requiresResourceReload;

    JSHeroesEngine()
    {
        manager = new ScriptEngineManager(null);
        engine = manager.getEngineByName("nashorn");

        if (engine == null)
        {
            throw new IllegalStateException("Nashorn JavaScript engine == null");
        }

        try
        {
            inv = (Invocable) engine;
        }
        catch (ClassCastException e)
        {
            throw new RuntimeException("JavaScript engine is not an Invocable?!", e);
        }
    }

    public ScriptEngine getEngine()
    {
        return engine;
    }

    public Object evalSilently(String script)
    {
        try
        {
            return engine.eval(script);
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public Object evalSilently(String script, String signature, Object... args)
    {
        try
        {
            engine.eval(String.format("function evaluate(%s){return %s;}", signature, script));
            return inv.invokeFunction("evaluate", args);
        }
        catch (ScriptException | NoSuchMethodException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public Hero loadHeroRaw(ResourceLocation id, String raw) throws ScriptException, NoSuchMethodException
    {
        JSHero hero = new JSHero(id);
        engine.eval(raw);
        inv.invokeFunction("init", hero);

        return hero.new HeroJS();
    }

    public Hero loadHero(ResourceLocation id, Callable<InputStream> input, AbstractHeroPackSerializer serializer) throws ScriptException, IOException, NoSuchMethodException
    {
        try
        {
            JSHero hero = new JSHero(id);
            engine.eval(new InputStreamReader(input.call()));
            inv.invokeFunction("init", hero);

            if (serializer != null)
            {
                serializer.putData(AbstractHeroPackSerializer.HERO, id, FileHelper.compactify(new BufferedReader(new InputStreamReader(input.call()))));
            }

            return hero.new HeroJS();
        }
        catch (RuntimeException | ScriptException | IOException | NoSuchMethodException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public MoveSet loadMoveSet(InputStream in)
    {
        Gson gson = MoveSet.GSON_FACTORY.create();
        return gson.fromJson(new InputStreamReader(in), MoveSet.class);
    }

    public void init(Side side, MinecraftServer server)
    {
        lastPackData = null;
        packResources = null;

        if (side.isServer())
        {
            if (!server.isDedicatedServer())
            {
                return;
            }

            lastPackData = new HeroPackSerializer();
        }
        else if (side.isClient())
        {
            packResources = new HeroPackSnooper();
            lastPackData = (HeroPackSnooper) packResources;
        }

        try
        {
            Hero.REGISTRY.reload(new File(HEROPACKS_DIR), false, lastPackData);
        }
        catch (HeroPackException e)
        {
            throw new RuntimeException(e);
        }
    }

    public int reload(boolean textures) throws HeroPackException
    {
        packResources = null;

        if (MinecraftServer.getServer().isDedicatedServer())
        {
            lastPackData = new HeroPackSerializer();
        }
        else
        {
            packResources = new HeroPackSnooper();
            lastPackData = (HeroPackSnooper) packResources;
        }

        int count = Hero.REGISTRY.reload(MinecraftServer.getServer().getFile(HEROPACKS_DIR), true, lastPackData);

        if (lastPackData instanceof HeroPackSerializer)
        {
            MessageSyncHeroPacks.sync((HeroPackSerializer) lastPackData, false, SHNetworkManager.wrapper::sendToAll);
        }
        else if (textures)
        {
            MessageSyncHeroPacks.sync(null, true, SHNetworkManager.wrapper::sendToAll);
        }

        return count;
    }

    public void reloadResources()
    {
        JSHeroesEngine.LOGGER.info("Triggered HeroPack client resource reload");
        requiresResourceReload = true;
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;

            if (!player.worldObj.isRemote)
            {
                if (lastPackData instanceof HeroPackSerializer)
                {
                    MessageSyncHeroPacks.sync((HeroPackSerializer) lastPackData, false, t -> SHNetworkManager.wrapper.sendTo(t, (EntityPlayerMP) player));
                }
                else if (lastWorldIsServer)
                {
                    LOGGER.info("Reloading singleplayer HeroPacks...");

                    lastWorldIsServer = false;
                    init(Side.CLIENT, null);
                    reloadResources();
                }
            }
        }
    }

    private List<ByteBuf> receivedSlices = new ArrayList<>();
    private boolean receivedAll;

    public void receive(ByteBuf slice, boolean done)
    {
        receivedSlices.add(slice);
        receivedAll = done;
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.player.worldObj.isRemote && receivedAll)
        {
            if (!receivedSlices.isEmpty())
            {
                ByteBuf buf = ByteBufSlicer.merge(receivedSlices);
                boolean resources = buf.readBoolean();

                if (resources || JSHeroesResources.doClientSync())
                {
                    if (resources)
                    {
                        JSHeroesEngine.INSTANCE.reloadResources();
                    }

                    if (buf.readBoolean())
                    {
                        HeroPackSerializer serializer = new HeroPackSerializer();

                        try
                        {
                            serializer.fromBytes(buf.slice());
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        JSHeroesEngine.INSTANCE.lastWorldIsServer = true;
                        new Thread(() ->
                        {
                            try
                            {
                                if (Hero.REGISTRY.reloadFrom(serializer) > 0)
                                {
                                    JSHeroesResources.syncResources(serializer);
                                }
                            }
                            catch (HeroPackException e)
                            {
                                e.printStackTrace();
                            }
                        }, "Client HeroPack reloader").start();
                    }
                }

                receivedSlices.clear();
            }

            receivedAll = false;
        }
    }
}
