package com.fiskmods.heroes.util;

import java.util.Comparator;
import java.util.function.Function;

import com.fiskmods.heroes.common.hero.Hero;

public class SHComparators
{
    public static final Comparator<Hero> TIER = Comparator.comparing(Hero::getTier).thenComparing(Function.identity());

    public static Comparator<Hero> fabricator(int maxTier)
    {
        return (o1, o2) -> o1.getTier().tier > maxTier || o2.getTier().tier > maxTier ? TIER.compare(o1, o2) : o1.compareTo(o2);
    }
}
