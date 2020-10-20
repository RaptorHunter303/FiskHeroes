package com.fiskmods.heroes.common.config;

import java.util.LinkedList;
import java.util.List;

import com.fiskmods.heroes.FiskHeroes;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;

public class SyncedConfig
{
    public static final String CATEGORY_SERVER = "Server";
    public static final String[] DEFAULT_UNPHASABLES = {"minecraft:bedrock", FiskHeroes.MODID + ":subatomic_particle_shell", FiskHeroes.MODID + ":subatomic_particle_core", FiskHeroes.MODID + ":eternium_stone", FiskHeroes.MODID + ":eternium_block"};

    public int qrSpread;

    public List<String> unphasable = new LinkedList<>();

    public void load(Configuration config)
    {
        qrSpread = config.getInt("QR Spread", CATEGORY_SERVER, 3000000, -30000000, 30000000, "How large of an area will players be spread out over when they first enter the Quantum Realm? (Values are entered in blocks.)");
        List<String> list = Lists.newArrayList(config.getStringList("Unphasable Blocks", CATEGORY_SERVER, DEFAULT_UNPHASABLES, "Array of blocks that can't be phased through (Format: modid:blockid OR modid:blockid@metadata)"));

        if (!config.hasKey("_", "version") || config.getInt("version", "_", 1, 1, 1, "_") < 1)
        {
            if (!list.contains(FiskHeroes.MODID + ":eternium_stone"))
            {
                list.add(FiskHeroes.MODID + ":eternium_stone");
                list.add(FiskHeroes.MODID + ":eternium_block");
                config.get(CATEGORY_SERVER, "Unphasable Blocks", DEFAULT_UNPHASABLES).set(list.toArray(new String[0]));
            }

            config.get("_", "version", 1).set(1);
        }

        unphasable.clear();
        unphasable.addAll(list);
    }

    public SyncedConfig fromBytes(ByteBuf buf)
    {
        qrSpread = buf.readInt();
        unphasable.clear();
        int i = buf.readInt();

        for (int j = 0; j < i; ++j)
        {
            unphasable.add(ByteBufUtils.readUTF8String(buf));
        }

        return this;
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(qrSpread);
        buf.writeInt(unphasable.size());

        for (String element : unphasable)
        {
            ByteBufUtils.writeUTF8String(buf, element);
        }
    }

    public SyncedConfig set(SyncedConfig config)
    {
        qrSpread = config.qrSpread;

        unphasable.clear();
        unphasable.addAll(config.unphasable);

        return this;
    }

    public SyncedConfig copy()
    {
        return new SyncedConfig().set(this);
    }

    public boolean canPhase(Block block)
    {
        return !unphasable.contains(block.delegate.name());
    }
}
