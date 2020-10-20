package com.fiskmods.heroes.common.achievement;

import com.fiskmods.heroes.common.data.SHPlayerData;
import com.fiskmods.heroes.common.hero.Hero;
import com.google.common.collect.Iterables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public enum StatStringFormatAllSuits implements IStatStringFormat
{
    INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public String formatString(String s)
    {
        if (mc.thePlayer != null)
        {
            int amount = SHPlayerData.getData(mc.thePlayer).heroesCollected.size();
            int max = Iterables.size(Iterables.filter(Hero.REGISTRY, t -> !t.isHidden()));
            int progress = MathHelper.floor_float((float) amount / max * 100);

            s += "\n\n" + StatCollector.translateToLocalFormatted(SHAchievements.ALL_SUITS.statId + ".progress", progress);
        }

        return s;
    }
}
