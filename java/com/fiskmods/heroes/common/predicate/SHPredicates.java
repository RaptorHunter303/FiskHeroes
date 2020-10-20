package com.fiskmods.heroes.common.predicate;

import java.util.function.Predicate;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.ItemHeroArmor;
import com.fiskmods.heroes.util.FiskPredicates;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

public class SHPredicates
{
//    public static final Predicate<Entity> HAS_MOVESET = t -> t instanceof EntityLivingBase && MoveCommonHandler.hasMoveSet((EntityLivingBase) t); // TODO: 1.4 Combat
//
//    public static Predicate<SHData<?>> isModeApplicable(CommandPlayerData.Mode mode)
//    {
//        return t ->
//        {
//            for (Class c : mode.applicable)
//            {
//                if (c == Object.class || c == t.typeClass.getType())
//                {
//                    return true;
//                }
//            }
//
//            return false;
//        };
//    }

    public static Predicate<Entity> heroPred(Predicate<Hero> p)
    {
        return t ->
        {
            Hero hero = SHHelper.getHero(t);
            return hero != null && p.test(hero);
        };
    }

    public static void register()
    {
        FiskPredicates.addHook("isHeroArmor", Item.class, t -> t instanceof ItemHeroArmor);
    }
}
