package com.fiskmods.heroes.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityBookPlayer;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.common.entity.EntityRenderItemPlayer;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.Constants.NBT;

public class SHClientUtils
{
    private static Minecraft mc = Minecraft.getMinecraft();

    public static void addServer()
    {
        try
        {
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(new File(mc.mcDataDir, "servers.dat"));

            if (nbttagcompound == null)
            {
                return;
            }

            NBTTagList nbttaglist = nbttagcompound.getTagList("servers", NBT.TAG_COMPOUND);
            List<ServerData> servers = new ArrayList<>();
            String ip = "heroes.fiskmods.com";
            String oldIp = "fisk.omegamc.org";

            boolean flag = true;

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                ServerData data = ServerData.getServerDataFromNBTCompound(nbttaglist.getCompoundTagAt(i));
                servers.add(data);

                if (data.serverIP.equals(ip))
                {
                    return;
                }
                else if (data.serverIP.equals(oldIp))
                {
                    data.serverIP = ip;
                    flag = false;
                }
            }

            if (flag)
            {
                ServerData data = new ServerData("Fisk Community Server", ip);
                servers.add(data);
            }

            NBTTagList nbttaglist1 = new NBTTagList();
            Iterator iterator = servers.iterator();

            while (iterator.hasNext())
            {
                ServerData serverdata = (ServerData) iterator.next();
                nbttaglist1.appendTag(serverdata.getNBTCompound());
            }

            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setTag("servers", nbttaglist1);
            CompressedStreamTools.safeWrite(nbttagcompound1, new File(mc.mcDataDir, "servers.dat"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static float getGlidingProgress(Entity entity, float partialTicks)
    {
        float f = SHData.TICKS_SINCE_GLIDING.get(entity);

        if (SHData.GLIDING.get(entity))
        {
            f = SHData.TICKS_GLIDING.get(entity) + partialTicks;
        }
        else if (f > 0)
        {
            f = Math.max(10 - f - partialTicks, 0);
        }

        return MathHelper.clamp_float(f * f / 100, 0, 1);
    }

    public static boolean isInanimate(Entity entity)
    {
        return entity instanceof EntityBookPlayer || entity instanceof EntityDisplayMannequin || entity instanceof EntityRenderItemPlayer;
    }

    public static String getDisguisedUUID(EntityPlayer player)
    {
        String disguise = SHData.DISGUISE.get(player);

        if (disguise != null)
        {
            GameProfile profile = FiskServerUtils.lookupProfile(disguise);
            UUID uuid;

            if (profile != null && (uuid = profile.getId()) != null)
            {
                return uuid.toString();
            }
        }

        return player.getUniqueID().toString();
    }
}
