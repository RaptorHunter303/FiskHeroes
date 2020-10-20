package com.fiskmods.heroes.common.config;

import java.lang.reflect.Field;
import java.util.Locale;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.registry.FiskRegistryEntry;
import com.fiskmods.heroes.common.registry.FiskRegistryNumerical;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class Rule<T> extends FiskRegistryEntry<Rule<?>>
{
    public static final FiskRegistryNumerical<Rule<?>> REGISTRY = new FiskRegistryNumerical(FiskHeroes.MODID, null);

    public static void register(String key, Rule value)
    {
        REGISTRY.putObject(key, value);
    }

    public static Rule getFromName(String key)
    {
        return REGISTRY.getObject(key);
    }

    public static String getNameFor(Rule value)
    {
        return REGISTRY.getNameForObject(value);
    }

    public static final Rule<Boolean> ALLOW_INTANGIBILITY = new Rule<>(true, true);
    public static final Rule<Boolean> ALLOW_QR = new Rule<>(true, true);
    public static final Rule<Boolean> ALLOW_SENTRYMODE = new Rule<>(true, true);
    public static final Rule<Boolean> DURABILITY_SCALED_PROT = new Rule<>(false, true);

    public static final Rule<Boolean> GRIEF_CANARYCRY = new Rule<>(true);
    public static final Rule<Boolean> GRIEF_COLDGUN = new Rule<>(true);
    public static final Rule<Boolean> GRIEF_ENERGYPROJ = new Rule<>(true);
    public static final Rule<Boolean> GRIEF_FIRECHARGEARROW = new Rule<>(true);
    public static final Rule<Boolean> GRIEF_FLAMEBLAST = new Rule<>(true);
    public static final Rule<Boolean> GRIEF_GEOKINESIS = new Rule<>(true);
    public static final Rule<Boolean> GRIEF_HEATGUN = new Rule<>(true);
    public static final Rule<Boolean> GRIEF_HEATVISION = new Rule<>(true);

    public static final Rule<Float> BOOST_GLIDEFLIGHT = new Rule<>(2F, true);

    public static final Rule<Float> DAMAGE_REDUCTION_BASELINE = new Rule<>(80F, true);
    public static final Rule<Float> DAMAGE_REDUCTION_FACTOR = new Rule<>(1.6F, true);

    public static final Rule<Float> DMGMULT_ARROW = new Rule<>(1.25F);
    public static final Rule<Float> DMGMULT_BATARANG = new Rule<>(1.25F, true);
    public static final Rule<Float> DMGMULT_THROWINGSTAR = new Rule<>(1.5F, true);

    public static final Rule<Float> DMG_CACTUSSPIKE = new Rule<>(1.5F);
    public static final Rule<Float> DMG_CANARYCRY_MAX = new Rule<>(7F);
    public static final Rule<Float> DMG_CANARYCRY_MIN = new Rule<>(3F);
    public static final Rule<Float> DMG_COLDGUN = new Rule<>(7F);
    public static final Rule<Float> DMG_EARTHQUAKE = new Rule<>(1F);
    public static final Rule<Float> DMG_ENERGYPROJ = new Rule<>(3F);
    public static final Rule<Float> DMG_FIREBALL = new Rule<>(6F);
    public static final Rule<Float> DMG_FIREBALLARROW = new Rule<>(6F);
    public static final Rule<Float> DMG_FLAMEBLAST = new Rule<>(5F);
    public static final Rule<Float> DMG_GROUNDSMASH = new Rule<>(15F);
    public static final Rule<Float> DMG_HEATGUN = new Rule<>(5F);
    public static final Rule<Float> DMG_HEATVISION = new Rule<>(7F);
    public static final Rule<Float> DMG_ICEFISTBONUS = new Rule<>(3F);
    public static final Rule<Float> DMG_ICICLE = new Rule<>(9F);
    public static final Rule<Float> DMG_LASERBOLT = new Rule<>(6F);
    public static final Rule<Float> DMG_LIGHTNINGCAST = new Rule<>(10F);
    public static final Rule<Float> DMG_QR_SUFFOCATE = new Rule<>(2F);
    public static final Rule<Float> DMG_REPULSOR = new Rule<>(12F);
    public static final Rule<Float> DMG_SNAKE = new Rule<>(3F);
    public static final Rule<Float> DMG_SPELL_ATMOSPHERIC = new Rule<>(2F);
    public static final Rule<Float> DMG_SPELL_EARTHSWALLOW = new Rule<>(14F);
    public static final Rule<Float> DMG_SPELL_WHIP = new Rule<>(5F);
    public static final Rule<Float> DMG_SPELL_WHIPBURN = new Rule<>(0.5F);

    public static final Rule<Float> KNOCKBACK_CANARYCRY = new Rule<>(0.025F, true);
    public static final Rule<Float> KNOCKBACK_GROUNDSMASH = new Rule<>(3F);

    public static final Rule<Float> RADIUS_ETERNIUMWEAKNESS = new Rule<>(3F);
    public static final Rule<Float> RADIUS_EXPLOSIVEARROW = new Rule<>(1.999F);
    public static final Rule<Float> RADIUS_FIREBALL = new Rule<>(2.5F);
    public static final Rule<Float> RADIUS_FIREBALLARROW = new Rule<>(2.5F);
    public static final Rule<Float> RADIUS_FIREWEAKNESS = new Rule<>(2.5F);
    public static final Rule<Float> RADIUS_GROUNDSMASH_EXPL = new Rule<>(2.5F);
    public static final Rule<Float> RADIUS_LIGHTNINGCHAIN = new Rule<>(2.5F, true);
    public static final Rule<Float> RADIUS_SHADOWDOME = new Rule<>(24F, true);
    public static final Rule<Float> RADIUS_SPELL_EARTHSWALLOW = new Rule<>(6F, true);
    public static final Rule<Integer> RADIUS_GROUNDSMASH = new Rule<>(3);

    public static final Rule<Float> RANGE_CACTUSSUMMON = new Rule<>(16F, true);
    public static final Rule<Float> RANGE_COLDGUN = new Rule<>(32F, true);
    public static final Rule<Float> RANGE_ENERGYPROJ = new Rule<>(10F, true);
    public static final Rule<Float> RANGE_FLAMEBLAST = new Rule<>(10F, true);
    public static final Rule<Float> RANGE_HEATGUN = new Rule<>(32F, true);
    public static final Rule<Float> RANGE_HEATVISION = new Rule<>(32F, true);
    public static final Rule<Float> RANGE_LIGHTNINGCAST = new Rule<>(48F, true);
    public static final Rule<Float> RANGE_REPULSOR = new Rule<>(32F);
    public static final Rule<Float> RANGE_SPELL_EARTHSWALLOW = new Rule<>(48F, true);
    public static final Rule<Float> RANGE_TELEPORT = new Rule<>(128F);

    public static final Rule<Float> VELMULT_ARROW = new Rule<>(2.5F, true);

    public static final Rule<Integer> COOLDOWN_CHRONOSRIFLE = new Rule<>(20, true);
    public static final Rule<Integer> COOLDOWN_EARTHQUAKE = new Rule<>(160, true);
    public static final Rule<Integer> COOLDOWN_ENERGYBLAST = new Rule<>(15, true);
    public static final Rule<Integer> COOLDOWN_FIREBALL = new Rule<>(5, true);
    public static final Rule<Integer> COOLDOWN_GROUNDSMASH = new Rule<>(40, true);
    public static final Rule<Integer> COOLDOWN_ICICLES = new Rule<>(5, true);
    public static final Rule<Integer> COOLDOWN_LIGHTNINGCAST = new Rule<>(20, true);
    public static final Rule<Integer> COOLDOWN_REPULSOR = new Rule<>(30, true);
    public static final Rule<Integer> COOLDOWN_RIPSGUN = new Rule<>(5, true);
    public static final Rule<Integer> COOLDOWN_SHIELDTHROW = new Rule<>(40, true);
    public static final Rule<Integer> COOLDOWN_SPELL_ATMOSPHERIC = new Rule<>(20, true);
    public static final Rule<Integer> COOLDOWN_SPELL_DUPLICATION = new Rule<>(1200, true);
    public static final Rule<Integer> COOLDOWN_SPELL_EARTHSWALLOW = new Rule<>(800, true);
    public static final Rule<Integer> COOLDOWN_SPELL_WHIP = new Rule<>(50, true);
    public static final Rule<Integer> COOLDOWN_SPIKEBURST = new Rule<>(5, true);
    public static final Rule<Integer> COOLDOWN_SPIKERING = new Rule<>(10, true);
    public static final Rule<Integer> COOLDOWN_SUMMONCACTUS = new Rule<>(5, true);

    public static final Rule<Integer> TICKS_CACTUSHEALRATE = new Rule<>(5);
    public static final Rule<Integer> TICKS_CACTUSLIFESPAN = new Rule<>(600);
    public static final Rule<Integer> TICKS_ETERNIUMWEAKNESS = new Rule<>(120);
    public static final Rule<Integer> TICKS_FIREWEAKNESS = new Rule<>(100);
    public static final Rule<Integer> TICKS_INTANGIBLETIMER = new Rule<>(300, true);
    public static final Rule<Integer> TICKS_PHANTOMARROW = new Rule<>(1800);
    public static final Rule<Integer> TICKS_QRTIMER = new Rule<>(1200, true);
    public static final Rule<Integer> TICKS_SHADOWDOME = new Rule<>(1200, true);
    public static final Rule<Integer> TICKS_SHADOWDOMECHARGE = new Rule<>(40, true);
    public static final Rule<Integer> TICKS_SHADOWFORMTIMER = new Rule<>(400, true);
    public static final Rule<Integer> TICKS_STEELTIMER = new Rule<>(400, true);
    public static final Rule<Integer> TICKS_TUTRIDIUMARROW = new Rule<>(300);

    public static final Rule<Integer> TICKS_METALSKIN_BLAZEARROW = new Rule<>(4, true);
    public static final Rule<Integer> TICKS_METALSKIN_ENERGYPROJ = new Rule<>(150, true);
    public static final Rule<Integer> TICKS_METALSKIN_FLAMEBLAST = new Rule<>(160, true);
    public static final Rule<Integer> TICKS_METALSKIN_HEATGUN = new Rule<>(170, true);
    public static final Rule<Integer> TICKS_METALSKIN_HEATVISION = new Rule<>(140, true);
    public static final Rule<Integer> TICKS_METALSKIN_LAVA = new Rule<>(120, true);

    public final T defaultValue;
    public final boolean requiresClientSync;

    protected Rule(T defaultVal, boolean sync)
    {
        defaultValue = defaultVal;
        requiresClientSync = sync;
    }

    protected Rule(T defaultVal)
    {
        this(defaultVal, false);
    }

    public T parse(ICommandSender sender, String s)
    {
        if (ofType(Integer.class))
        {
            return (T) Integer.valueOf(CommandBase.parseInt(sender, s));
        }
        else if (ofType(Boolean.class))
        {
            return (T) Boolean.valueOf(CommandBase.parseBoolean(sender, s));
        }
        else if (ofType(Double.class))
        {
            return (T) Double.valueOf(CommandBase.parseDouble(sender, s));
        }
        else if (ofType(Float.class))
        {
            return (T) Float.valueOf((float) CommandBase.parseDouble(sender, s));
        }

        return null;
    }

    public T add(T value, T amount)
    {
        if (ofType(Integer.class))
        {
            return (T) Integer.valueOf((Integer) value + (Integer) amount);
        }
        else if (ofType(Double.class))
        {
            return (T) Double.valueOf((Double) value + (Double) amount);
        }
        else if (ofType(Float.class))
        {
            return (T) Float.valueOf((Float) value + (Float) amount);
        }

        throw new ClassCastException();
    }

    public T mult(T value, T factor)
    {
        if (ofType(Integer.class))
        {
            return (T) Integer.valueOf((Integer) value * (Integer) factor);
        }
        else if (ofType(Double.class))
        {
            return (T) Double.valueOf((Double) value * (Double) factor);
        }
        else if (ofType(Float.class))
        {
            return (T) Float.valueOf((Float) value * (Float) factor);
        }

        throw new ClassCastException();
    }

    public T clamp(T value, T min, T max)
    {
        if (ofType(Integer.class))
        {
            return (T) Integer.valueOf(MathHelper.clamp_int((Integer) value, (Integer) min, (Integer) max));
        }
        else if (ofType(Double.class))
        {
            return (T) Double.valueOf(MathHelper.clamp_double((Double) value, (Double) min, (Double) max));
        }
        else if (ofType(Float.class))
        {
            return (T) Float.valueOf(MathHelper.clamp_float((Float) value, (Float) min, (Float) max));
        }

        throw new ClassCastException();
    }

    public T get(World world, int x, int z)
    {
        return RuleHandler.getLocal(world, x, z).get(this);
    }

    public T get(Entity entity)
    {
        return RuleHandler.getLocal(entity).get(this);
    }

    public T get(EntityLivingBase entity, Hero hero)
    {
        T obj = get(entity);
        
        if (hero != null)
        {
            if (ofType(Float.class))
            {
                return (T) Float.valueOf(hero.modifyRuleValue(entity, (Rule<Float>) this).floatValue());
            }
            else if (ofType(Integer.class))
            {
                return (T) Integer.valueOf(hero.modifyRuleValue(entity, (Rule<Integer>) this).intValue());
            }
        }
        
        return obj;
    }

    public T get(EntityLivingBase entity, HeroIteration iter)
    {
        return get(entity, iter != null ? iter.hero : null);
    }

    public T getHero(EntityLivingBase entity)
    {
        return get(entity, entity != null ? SHHelper.getHero(entity) : null);
    }

    public T get(Entity entity, Hero hero)
    {
        return entity instanceof EntityLivingBase ? get((EntityLivingBase) entity, hero) : get(entity);
    }

    public T get(Entity entity, HeroIteration iter)
    {
        return get(entity, iter != null ? iter.hero : null);
    }

    public T getHero(Entity entity)
    {
        return get(entity, SHHelper.getHero(entity));
    }

    public boolean ofType(Class clazz)
    {
        return clazz.isInstance(defaultValue);
    }

    public Class<T> getType()
    {
        return (Class<T>) defaultValue.getClass();
    }

    public static void init()
    {
        for (Field field : Rule.class.getFields())
        {
            if (Rule.class.isAssignableFrom(field.getType()))
            {
                try
                {
                    register(field.getName().toLowerCase(Locale.ROOT), (Rule) field.get(null));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
