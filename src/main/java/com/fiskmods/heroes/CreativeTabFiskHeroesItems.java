package com.fiskmods.heroes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.fiskmods.heroes.common.hero.ItemHeroArmor;
import com.fiskmods.heroes.common.item.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabFiskHeroesItems extends CreativeTabs
{
    public CreativeTabFiskHeroesItems()
    {
        super(FiskHeroes.MODID + ".items");
    }

    @Override
    public Item getTabIconItem()
    {
        return ModItems.tutridiumGem;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllReleventItems(List list)
    {
        Iterator iterator = Item.itemRegistry.iterator();
        List<ItemStack> heroList = new ArrayList<>();

        while (iterator.hasNext())
        {
            Item item = (Item) iterator.next();

            if (item == null)
            {
                continue;
            }

            for (CreativeTabs tab : item.getCreativeTabs())
            {
                if (tab == this)
                {
                    item.getSubItems(item, this, item instanceof ItemHeroArmor ? heroList : list);
                }
            }
        }

        heroList.sort(Comparator.comparing((ItemStack t) -> ItemHeroArmor.get(t).hero));
        list.addAll(heroList);
    }
}
