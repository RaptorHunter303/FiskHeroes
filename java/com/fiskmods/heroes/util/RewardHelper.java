package com.fiskmods.heroes.util;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fiskmods.heroes.FiskHeroes;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

public class RewardHelper
{
    private static final ImmutableList<String> COLLABORATORS = ImmutableList.of("2c11a791-174b-4122-b5ea-aa404231e2bd", "83d7474e-d33b-44b3-959c-1339e4ce06e0", "b055fc86-8a06-4b03-96b4-3a30460f10ea", "c3ed4d52-fb4f-4964-ba1b-9cda2453741e", "9203def4-97a9-4766-b269-c31b7c37207d");

    private static StatsJson stats;

    public static IIcon[] conquestIcons;
    public static IIcon collaboratorIcon;
    public static IIcon patreonIcon;
    public static IIcon cactutIcon;

    public static void registerIcons(IIconRegister iconRegister)
    {
        conquestIcons = new IIcon[Ranking.values().length];
        collaboratorIcon = iconRegister.registerIcon(FiskHeroes.MODID + ":collaborator_gem");
        patreonIcon = iconRegister.registerIcon(FiskHeroes.MODID + ":patreon_gem");
        cactutIcon = iconRegister.registerIcon(FiskHeroes.MODID + ":conquest_top");

        for (Ranking ranking : Ranking.values())
        {
            conquestIcons[ranking.ordinal()] = iconRegister.registerIcon(FiskHeroes.MODID + ":conquest_" + ranking.name().toLowerCase(Locale.ROOT));
        }
    }

    public static void fetchStats()
    {
        try
        {
            stats = new Gson().fromJson(new InputStreamReader(FileHelper.createConnection("https://raw.githubusercontent.com/FiskFille/Superheroes/master/stats.json").getInputStream()), StatsJson.class);
        }
        catch (Exception e)
        {
            FiskHeroes.LOGGER.warn("Caught exception fetching stats json:");
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    public static boolean hasRewardClient(EntityPlayer player)
    {
        return isDonatorClient(player) || isPatronClient(player) || isCollaboratorClient(player);
    }

    public static boolean hasReward(EntityPlayer player)
    {
        return isDonator(player) || isPatron(player) || isCollaborator(player);
    }

    @SideOnly(Side.CLIENT)
    public static boolean isCollaboratorClient(EntityPlayer player)
    {
        return !SHClientUtils.isInanimate(player) && COLLABORATORS.contains(SHClientUtils.getDisguisedUUID(player));
    }

    public static boolean isCollaborator(EntityPlayer player)
    {
        return COLLABORATORS.contains(player.getUniqueID().toString());
    }

    @SideOnly(Side.CLIENT)
    public static boolean isDonatorClient(EntityPlayer player)
    {
        return stats != null && !SHClientUtils.isInanimate(player) && stats.originalDonators.contains(SHClientUtils.getDisguisedUUID(player));
    }

    public static boolean isDonator(EntityPlayer player)
    {
        return stats != null && stats.originalDonators.contains(player.getUniqueID().toString());
    }

    @SideOnly(Side.CLIENT)
    public static boolean isPatronClient(EntityPlayer player)
    {
        return stats != null && !SHClientUtils.isInanimate(player) && stats.patreonDonators.contains(SHClientUtils.getDisguisedUUID(player));
    }

    public static boolean isPatron(EntityPlayer player)
    {
        return stats != null && stats.patreonDonators.contains(player.getUniqueID().toString());
    }

    @SideOnly(Side.CLIENT)
    public static boolean inPatreonClubClient(EntityPlayer player)
    {
        return stats != null && !SHClientUtils.isInanimate(player) && stats.patreonClub.contains(SHClientUtils.getDisguisedUUID(player));
    }

    public static boolean inPatreonClub(EntityPlayer player)
    {
        return stats != null && stats.patreonClub.contains(player.getUniqueID().toString());
    }

    @SideOnly(Side.CLIENT)
    public static boolean isConquestTop(EntityPlayer player)
    {
        return stats != null && !SHClientUtils.isInanimate(player) && stats.conquestTop.equals(SHClientUtils.getDisguisedUUID(player));
    }

    @SideOnly(Side.CLIENT)
    public static int getConquestRanking(EntityPlayer player, Ranking ranking)
    {
        return stats != null && !SHClientUtils.isInanimate(player) ? stats.getConquestRanking(SHClientUtils.getDisguisedUUID(player), ranking) : 0;
    }

    @SideOnly(Side.CLIENT)
    public static boolean hasConquestRanking(EntityPlayer player)
    {
        return stats != null && !SHClientUtils.isInanimate(player) && stats.hasConquestRanking(SHClientUtils.getDisguisedUUID(player));
    }

    private static class StatsJson
    {
        private Map<Ranking, Map<String, Integer>> conquest;
        private String conquestTop;

        private List<String> patreonDonators;
        private List<String> patreonClub;
        private List<String> originalDonators;

        private int getConquestRanking(String uuid, Ranking ranking)
        {
            return conquest != null ? conquest.computeIfAbsent(ranking, k -> new HashMap<>()).getOrDefault(uuid, 0) : 0;
        }

        private boolean hasConquestRanking(String uuid)
        {
            return conquest != null && conquest.values().stream().anyMatch(t -> t.containsKey(uuid));
        }
    }

    public enum Ranking
    {
        @SerializedName("gold")
        GOLD,
        @SerializedName("silver")
        SILVER,
        @SerializedName("bronze")
        BRONZE;
    }
}
