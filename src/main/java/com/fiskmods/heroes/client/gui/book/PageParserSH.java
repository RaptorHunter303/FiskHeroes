package com.fiskmods.heroes.client.gui.book;

import java.util.List;

import com.fiskmods.heroes.common.book.IPageParser;
import com.fiskmods.heroes.common.book.Page;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHComparators;

public enum PageParserSH implements IPageParser
{
    INSTANCE;

    @Override
    public boolean parseGenerated(String id, List<Page> queue)
    {
        if (id.equals("registry_characters_all"))
        {
            Hero.REGISTRY.getValues().stream().filter(t -> !t.isHidden()).sorted(SHComparators.TIER).forEach(t ->
            {
                queue.add(new PageCharacter(t, false));
                queue.add(new PageCharacter(t, true));
            });

            return true;
        }

        return false;
    }
}
