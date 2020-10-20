package com.fiskmods.heroes.common.data.effect;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.registry.FiskRegistryEntry;
import com.fiskmods.heroes.common.registry.FiskRegistryNumerical;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;

public class StatEffect extends FiskRegistryEntry<StatEffect>
{
    public static final FiskRegistryNumerical<StatEffect> REGISTRY = new FiskRegistryNumerical(FiskHeroes.MODID, null);

    public static void register(int id, String key, StatEffect value)
    {
        REGISTRY.addObject(id, key, value);
    }

    public static StatEffect getEffectFromName(String key)
    {
        return REGISTRY.getObject(key);
    }

    public static String getNameForEffect(StatEffect value)
    {
        return REGISTRY.getNameForObject(value);
    }

    public static int getIdFromEffect(StatEffect value)
    {
        return value == null ? 0 : REGISTRY.getIDForObject(value);
    }

    public static StatEffect getEffectById(int id)
    {
        return REGISTRY.getObjectById(id);
    }

    public static final StatEffect VELOCITY_9 = new StatEffect(false).setIconIndex(0, 0);
    public static final StatEffect SPEED_SICKNESS = new StatEffect(true).setIconIndex(1, 0);
    public static final StatEffect SPEED_DAMPENER = new StatEffect(true).setIconIndex(2, 0);
    public static final StatEffect PHASE_SUPPRESSANT = new StatEffect(true).setIconIndex(3, 0);
    public static final StatEffect ETERNIUM_WEAKNESS = new StatEffect(true).setIconIndex(4, 0);
    public static final StatEffect TUTRIDIUM_POISON = new StatEffect(true).setIconIndex(5, 0);

    public static final StatEffect FLASHBANG = new StatEffect(true).setIconIndex(0, 1);

    private final boolean negativeEffect;
    private int statusIconIndex = -1;

    public StatEffect(boolean negative)
    {
        negativeEffect = negative;
    }

    protected StatEffect setIconIndex(int x, int y)
    {
        statusIconIndex = x + y * 8;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasStatusIcon()
    {
        return statusIconIndex >= 0;
    }

    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex()
    {
        return statusIconIndex;
    }

    public boolean isNegative()
    {
        return negativeEffect;
    }

    public String getUnlocalizedName()
    {
        return "statusEffect." + delegate.name().replace(':', '.');
    }

    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(getUnlocalizedName());
    }

    public String getFormattedString(StatusEffect effect)
    {
        String s = getLocalizedName();

        if (effect.amplifier > 0 && effect.amplifier < 10)
        {
            s += " " + StatCollector.translateToLocal("enchantment.level." + (effect.amplifier + 1));
        }

        return s;
    }

    public String getDurationString(StatusEffect effect)
    {
        if (effect.isMaxDuration())
        {
            return "**:**";
        }

        return StringUtils.ticksToElapsedTime(effect.duration);
    }

    public static void register()
    {
        register(0, "velocity_nine", VELOCITY_9);
        register(1, "speed_sickness", SPEED_SICKNESS);
        register(2, "disable_speed", SPEED_DAMPENER);
        register(3, "disable_phasing", PHASE_SUPPRESSANT);
        register(4, "flashbang", FLASHBANG);
        register(5, "eternium", ETERNIUM_WEAKNESS);
        register(6, "tutridium", TUTRIDIUM_POISON);
    }
}
