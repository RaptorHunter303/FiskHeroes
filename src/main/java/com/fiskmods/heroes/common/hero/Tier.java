package com.fiskmods.heroes.common.hero;

import com.fiskmods.heroes.common.config.Rule;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public enum Tier
{
    T1,
    T2,
    T3,
    T4,
    T5,
    T6;

    public static final int MAX = 6;

    public final int tier;

    Tier()
    {
        tier = ordinal() + 1;
    }

    public double getProtection(Entity entity)
    {
        double b = Rule.DAMAGE_REDUCTION_BASELINE.get(entity);
        double d = Rule.DAMAGE_REDUCTION_FACTOR.get(entity);

        return Math.round((100 - (100 - b) * d / Math.pow(d, tier)) * 10) / 1000.0;
    }

    public static Tier get(int tier)
    {
        return values()[MathHelper.clamp_int(tier - 1, 0, MAX - 1)];
    }
}
