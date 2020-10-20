package com.fiskmods.heroes.client.gui.book;

import com.fiskmods.heroes.common.book.Page;
import com.fiskmods.heroes.common.hero.Hero;

import net.minecraft.client.resources.I18n;

public class PageCharacter extends Page
{
    public final Hero hero;
    public final boolean stats;

    public PageCharacter(Hero hero, boolean stats)
    {
        this.hero = hero;
        this.stats = stats;

        setPageId(hero.getUnlocalizedName());
    }

    @Override
    public String[] getHeader()
    {
        return stats ? header : hero.getHeaderText();
    }

    @Override
    public String getSummaryTitle()
    {
        String name = hero.getLocalizedName();

        if (hero.getVersion() != null)
        {
            name = String.format("%s (%s)", name, I18n.format(hero.getVersion()));
        }

        return name;
    }
}
