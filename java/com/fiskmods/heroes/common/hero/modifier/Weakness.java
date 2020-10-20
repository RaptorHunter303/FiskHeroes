package com.fiskmods.heroes.common.hero.modifier;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.function.Predicate;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.registry.FiskRegistryEntry;
import com.fiskmods.heroes.common.registry.FiskSimpleRegistry;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StatCollector;

public class Weakness extends FiskRegistryEntry<Weakness> implements HeroModifier, Comparable<Weakness>, Predicate<EntityLivingBase>
{
    public static final FiskSimpleRegistry<Weakness> REGISTRY = new FiskSimpleRegistry(FiskHeroes.MODID, null);

    public static void register(String key, Weakness value)
    {
        REGISTRY.putObject(key, value);
    }

    public static Weakness getWeaknessFromName(String key)
    {
        return REGISTRY.getObject(key);
    }

    public static String getNameForWeakness(Weakness value)
    {
        return REGISTRY.getNameForObject(value);
    }

    public static final Weakness COLD = new WeaknessCold();
    public static final Weakness ETERNIUM = new WeaknessEternium();
    public static final Weakness FIRE = new WeaknessFire();
    public static final Weakness HEAT = new WeaknessHeat();
    public static final Weakness METAL_SKIN = new WeaknessMetalSkin();

    @Override
    public String getUnlocalizedName()
    {
        return "weakness." + getName().replace(':', '.');
    }

    @Override
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(getUnlocalizedName() + ".name");
    }

    public void onUpdateGlobal(EntityLivingBase entity, Hero hero, Phase phase, boolean hasWeakness)
    {
    }

    @Override
    public String getName()
    {
        return delegate.name();
    }

    @Override
    public int compareTo(Weakness o)
    {
        return getLocalizedName().compareTo(o.getLocalizedName());
    }

    @Override
    public boolean test(EntityLivingBase input)
    {
        return SHHelper.hasEnabledModifier(input, this);
    }

    @Override
    public String toString()
    {
        return "Weakness[" + getName() + "]";
    }

    static
    {
        for (Field field : Weakness.class.getFields())
        {
            if (field.getType() == Weakness.class)
            {
                try
                {
                    register(field.getName().toLowerCase(Locale.ROOT), (Weakness) field.get(null));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
