package com.fiskmods.heroes.common.achievement;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

public class AchievementSH extends Achievement
{
    public AchievementSH(String name, int x, int y, Block display, Achievement parent)
    {
        this(name, x, y, new ItemStack(display), parent);
    }

    public AchievementSH(String name, int x, int y, Item display, Achievement parent)
    {
        this(name, x, y, new ItemStack(display), parent);
    }

    public AchievementSH(String name, int x, int y, ItemStack display, Achievement parent)
    {
        super("achievement.fiskheroes." + name, "fiskheroes." + name, x + (parent != null ? parent.displayColumn : 0), y + (parent != null ? parent.displayRow : 0), display, parent);
        registerStat();

        if (parent == null)
        {
            initIndependentStat();
        }

        SHAchievements.PAGE.getAchievements().add(this);
    }
}
