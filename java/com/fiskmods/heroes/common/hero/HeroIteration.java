package com.fiskmods.heroes.common.hero;

import java.util.Comparator;

import com.fiskmods.heroes.common.BackupList;
import com.fiskmods.heroes.common.BackupMap;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class HeroIteration implements Comparable<HeroIteration>
{
    private static final Comparator<HeroIteration> COMPARATOR = Comparator.comparing((HeroIteration t) -> t.hero).thenComparing((o1, o2) -> o1.isDefault() ? -1 : o2.isDefault() ? 1 : 0).thenComparing(Comparator.comparing(HeroIteration::getLocalizedIterName));

    static final BackupMap<String, HeroIteration> REGISTRY = new BackupMap<>();
    static final BackupList<String> ID_REGISTRY = new BackupList<>();

    public final Hero hero;
    public final String key;

    final int id;

    private final String name;
    private final String[] armor = new String[4];

    private final ResourceLocation registryName;
    private final String fullName;

    HeroIteration(Hero hero, int id, String key, Candidate candidate)
    {
        this.hero = hero;
        this.key = key;
        this.id = id;

        if (candidate != null)
        {
            System.arraycopy(candidate.armor, 0, armor, 0, 4);
        }

        String s = Hero.getNameForHero(hero);
        registryName = isDefault() ? hero.getRegistryName() : new ResourceLocation(s + "_" + key);
        fullName = isDefault() ? s : s + "/" + key;
        name = candidate != null ? candidate.name : null;

        REGISTRY.put(getName(), this);
        ID_REGISTRY.add(ID_REGISTRY.size(), getName());
    }

    public String getArmorOverride(int slot)
    {
        return armor[slot];
    }

    public ItemStack createHelmet()
    {
        return createArmor(0);
    }

    public ItemStack createChestplate()
    {
        return createArmor(1);
    }

    public ItemStack createLeggings()
    {
        return createArmor(2);
    }

    public ItemStack createBoots()
    {
        return createArmor(3);
    }

    public ItemStack createArmor(int slot)
    {
        return slot < 0 ? null : ItemHeroArmor.create(hero.getItem(slot), this, false);
    }

    public ItemStack[] createArmorStacks()
    {
        return new ItemStack[] {createHelmet(), createChestplate(), createLeggings(), createBoots()};
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public String getName()
    {
        return fullName;
    }

    public String getLocalizedIterName()
    {
        return StatCollector.translateToLocal(isDefault() ? "hero.iteration.default" : name);
    }

    public int getId()
    {
        return id;
    }

    public boolean isDefault()
    {
        return id < 0;
    }

    @Override
    public int hashCode()
    {
        return 31 * (hero == null ? 0 : hero.hashCode()) + (key == null ? 0 : key.hashCode());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj instanceof HeroIteration)
        {
            HeroIteration other = (HeroIteration) obj;
            return Objects.equal(key, other.key) && Objects.equal(hero, other.hero);
        }

        return false;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public int compareTo(HeroIteration o)
    {
        return COMPARATOR.compare(this, o);
    }

    public static ImmutableSet<String> getKeys()
    {
        return ImmutableSet.copyOf(REGISTRY.keySet());
    }

    public static ImmutableSet<HeroIteration> getValues()
    {
        return ImmutableSet.copyOf(REGISTRY.values());
    }

    public static HeroIteration get(String key)
    {
        return REGISTRY.get(Hero.REGISTRY.namespace(key));
    }

    public static String getName(int index)
    {
        return ID_REGISTRY.get(index);
    }

    public static String getName(HeroIteration iter)
    {
        return iter != null ? iter.getName() : null;
    }

    public static int indexOf(String key)
    {
        return ID_REGISTRY.indexOf(key);
    }

    public static HeroIteration getDefault(Hero hero)
    {
        return hero != null ? hero.getDefault() : null;
    }

    public static HeroIteration lookup(String key)
    {
        if (key.contains("/"))
        {
            return get(key);
        }

        return getDefault(Hero.getHeroFromName(key));
    }

    static class Candidate
    {
        final String name;
        final String[] armor = new String[4];

        Candidate(String name)
        {
            this.name = name;
        }
    }
}
