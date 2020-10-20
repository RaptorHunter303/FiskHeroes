package com.fiskmods.heroes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fiskmods.heroes.common.hero.HeroIteration;

import net.minecraft.util.StatCollector;

public class SHFormatHelper
{
    public static String formatNumber(double n)
    {
        return String.format("%,f", n);
    }

    public static String formatNumber(int n)
    {
        return String.format("%,d", n);
    }

    public static String formatHero(HeroIteration iter)
    {
        String s = iter.hero.getLocalizedName();
        List<String> args = new ArrayList<>();

        if (s.contains("/"))
        {
            args.add(s.substring(s.indexOf("/") + 1));
            s = s.substring(0, s.indexOf("/"));
        }

        if (iter.hero.getVersion() != null)
        {
            args.add(StatCollector.translateToLocal(iter.hero.getVersion()));
        }

        if (!iter.isDefault())
        {
            args.add(iter.getLocalizedIterName());
        }

        if (!args.isEmpty())
        {
            String s1 = "";

            for (int i = 0; i < args.size(); ++i)
            {
                s1 += args.get(i);

                if (i < args.size() - 1)
                {
                    s1 += ", ";
                }
            }

            s = String.format("%s (%s)", s, s1);
        }

        return s;
    }

    public static String getUnconventionalName(String s)
    {
        s = s.toLowerCase(Locale.ROOT);

        for (int i = 0; i < s.length(); ++i)
        {
            if (i > 0 && s.charAt(i - 1) == '_' && i < s.length())
            {
                s = s.substring(0, i) + s.substring(i, i + 1).toUpperCase(Locale.ROOT) + s.substring(i + 1);
            }
        }

        s = s.replace(" ", "").replace("'", "").replace("/", "").replace("\\", "").replace("_", "").replace("-", "").replace("(", "").replace(")", "");
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }
}
