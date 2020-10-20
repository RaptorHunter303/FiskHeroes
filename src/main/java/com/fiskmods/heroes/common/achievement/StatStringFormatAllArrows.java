package com.fiskmods.heroes.common.achievement;

import com.fiskmods.heroes.common.data.DataManager;
import com.fiskmods.heroes.common.data.SHPlayerData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public enum StatStringFormatAllArrows implements IStatStringFormat
{
    INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public String formatString(String s)
    {
        if (mc.thePlayer != null)
        {
            int amount = DataManager.getArrowsCollected(mc.thePlayer);
            int total = SHPlayerData.getData(mc.thePlayer).arrowCollection.values().stream().reduce(0, (r, e) -> r + e);
            int progress = MathHelper.floor_float((float) total / (DataManager.serverArrows * 32) * 100);

            s += "\n\n" + StatCollector.translateToLocalFormatted(SHAchievements.ALL_ARROWS.statId + ".progress", progress, amount, DataManager.serverArrows);
        }

        return s;
    }
}
