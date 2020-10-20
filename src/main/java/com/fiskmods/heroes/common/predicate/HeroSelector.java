package com.fiskmods.heroes.common.predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.HeroModifier;
import com.fiskmods.heroes.common.hero.modifier.Weakness;
import com.fiskmods.heroes.util.NBTHelper;
import com.google.common.collect.Iterables;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class HeroSelector
{
    public final int minTier;
    public final int maxTier;

    public final List<String> modWlist, modBlist;
    public final List<Ability> abilityWlist, abilityBlist;
    public final List<Weakness> weaknessWlist, weaknessBlist;

    public HeroSelector(NBTTagCompound compound)
    {
        minTier = Math.max(compound.getByte("MinTier"), 1);
        maxTier = compound.hasKey("MaxTier", NBT.TAG_ANY_NUMERIC) ? Math.min(compound.getByte("MaxTier"), 5) : 5;

        if (compound.hasKey("Mod", NBT.TAG_COMPOUND))
        {
            NBTTagCompound modTag = compound.getCompoundTag("Mod");
            modWlist = readNBTList(modTag, "Whitelist");
            modBlist = readNBTList(modTag, "Blacklist");
        }
        else
        {
            modWlist = modBlist = null;
        }

        if (compound.hasKey("Abilities", NBT.TAG_COMPOUND))
        {
            NBTTagCompound abilityTag = compound.getCompoundTag("Abilities");
            abilityWlist = NBTHelper.readNBTList(abilityTag, "Whitelist", Ability.REGISTRY);
            abilityBlist = NBTHelper.readNBTList(abilityTag, "Blacklist", Ability.REGISTRY);
        }
        else
        {
            abilityWlist = abilityBlist = null;
        }

        if (compound.hasKey("Weaknesses", NBT.TAG_COMPOUND))
        {
            NBTTagCompound weaknessTag = compound.getCompoundTag("Weaknesses");
            weaknessWlist = NBTHelper.readNBTList(weaknessTag, "Whitelist", Weakness.REGISTRY);
            weaknessBlist = NBTHelper.readNBTList(weaknessTag, "Blacklist", Weakness.REGISTRY);
        }
        else
        {
            weaknessWlist = weaknessBlist = null;
        }
    }

    public static List<String> readNBTList(NBTTagCompound compound, String name)
    {
        List<String> list = null;

        if (compound.hasKey(name, NBT.TAG_LIST))
        {
            NBTTagList tagList = compound.getTagList(name, NBT.TAG_STRING);
            list = new ArrayList<>();

            for (int i = 0; i < tagList.tagCount(); ++i)
            {
                list.add(tagList.getStringTagAt(i));
            }
        }

        return list;
    }

    public static Predicate<Hero> selector(NBTTagCompound compound)
    {
        return new HeroSelector(compound).predicate;
    }

    public final Predicate<Hero> predicate = new Predicate<Hero>()
    {
        @Override
        public boolean test(Hero input)
        {
            if (input.getTier().tier < minTier || input.getTier().tier > maxTier)
            {
                return false;
            }
            else if (modWlist != null && !modWlist.contains(input.getDomain()))
            {
                return false;
            }
            else if (modBlist != null && modBlist.contains(input.getDomain()))
            {
                return false;
            }
            else if (abilityWlist != null && !hasModifier(input, abilityWlist))
            {
                return false;
            }
            else if (abilityBlist != null && hasModifier(input, abilityBlist))
            {
                return false;
            }
            else if (weaknessWlist != null && !hasModifier(input, weaknessWlist))
            {
                return false;
            }
            else if (weaknessBlist != null && hasModifier(input, weaknessBlist))
            {
                return false;
            }

            return true;
        }

        public boolean hasModifier(Hero hero, Iterable<? extends HeroModifier> iter)
        {
            return Iterables.any(iter, hero::hasModifier);
        }
    };
}
