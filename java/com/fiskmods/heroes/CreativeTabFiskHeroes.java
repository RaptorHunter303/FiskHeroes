package com.fiskmods.heroes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.ItemHeroArmor;
import com.fiskmods.heroes.common.item.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabFiskHeroes extends CreativeTabs
{
    public CreativeTabFiskHeroes()
    {
        super(FiskHeroes.MODID);
    }

    @Override
    public Item getTabIconItem()
    {
        return ModItems.metahumanLog;
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

//        heroList.sort(Comparator.comparing((ItemStack t) -> ItemHeroArmor.get(t).hero));
//        list.addAll(heroList);
        
        Map<Hero, ItemStack[]> map = new TreeMap<>();

        for (ItemStack stack : heroList)
        {
            if (stack.getItem() instanceof ItemHeroArmor)
            {
                Hero hero = ItemHeroArmor.get(stack).hero;
                ItemStack[] array = map.computeIfAbsent(hero, t -> new ItemStack[4]);
                
                array[((ItemHeroArmor) stack.getItem()).armorType] = stack;
                map.put(hero, array);
            }
        }
        
        int i = 0;
        
        for (Map.Entry<Hero, ItemStack[]> e : map.entrySet())
        {
            for (int j = 0; j < 4; ++j)
            {
                list.add(e.getValue()[j]);
            }
            
            if (++i % 2 == 1)
            {
                list.add(null);
            }
        }
    }
}
