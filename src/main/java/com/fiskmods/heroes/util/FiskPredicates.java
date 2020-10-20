package com.fiskmods.heroes.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class FiskPredicates
{
    public static final Map<Class, Map<String, Predicate>> METHODS = new HashMap<>();
    public static final String AND = " && ";

    public static final Predicate<Entity> IS_FLYING = t -> t instanceof EntityPlayer && ((EntityPlayer) t).capabilities.isFlying;

    public static <T> Predicate<T> forInput(Class<T> clazz, String input)
    {
        String[] astring = input.split(AND);
        Predicate<T> pred = t -> true;

        for (String s : astring)
        {
            boolean inverted = false;

            while (s.startsWith("!"))
            {
                s = s.substring(1);
                inverted = !inverted;
            }

            Predicate<T> p = getHook(clazz, s);

            if (p == null)
            {
                continue;
            }

            if (inverted)
            {
                p = p.negate();
            }

            pred = pred.and(p);
        }

        return pred;
    }

    public static Predicate<EntityLivingBase> isHolding(Item item)
    {
        return t -> t.getHeldItem() != null && t.getHeldItem().getItem() == item;
    }

    public static <T> void addHook(String key, Class<T> clazz, Predicate<T> p)
    {
        getHooks(clazz).put(key, p);
    }

    public static Map<String, Predicate> getHooks(Class clazz)
    {
        Map<String, Predicate> map = METHODS.get(clazz);

        if (map == null)
        {
            METHODS.put(clazz, map = new HashMap<>());
        }

        return map;
    }

    public static Predicate getHook(Class clazz, String key)
    {
        Predicate p = getHooks(clazz).get(key);

        return p == null ? t -> false : p;
    }

    static
    {
        addHook("hasTab", Item.class, t -> t.getCreativeTab() != null);

        for (ModContainer mod : Loader.instance().getModList())
        {
            addHook("fromMod_" + mod.getModId(), Item.class, t ->
            {
                return t.delegate.name().startsWith(mod.getModId() + ":");
            });
        }
    }
}
