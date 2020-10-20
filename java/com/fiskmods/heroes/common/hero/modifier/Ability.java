package com.fiskmods.heroes.common.hero.modifier;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Random;
import java.util.function.Predicate;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.equipment.EnumEquipment;
import com.fiskmods.heroes.common.registry.FiskRegistryEntry;
import com.fiskmods.heroes.common.registry.FiskSimpleRegistry;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.StatCollector;

public class Ability extends FiskRegistryEntry<Ability> implements HeroModifier, Comparable<Ability>, Predicate<Entity>
{
    public static final FiskSimpleRegistry<Ability> REGISTRY = new FiskSimpleRegistry(FiskHeroes.MODID, null);

    public static void register(String key, Ability value)
    {
        REGISTRY.putObject(key, value);
    }

    public static Ability getAbilityFromName(String key)
    {
        return REGISTRY.getObject(key);
    }

    public static String getNameForAbility(Ability value)
    {
        return REGISTRY.getNameForObject(value);
    }

    public static final String FUNC_CAN_AIM = "canAim";
    public static final String FUNC_TRANSFORM_TICKS = "getTransformTimerTicks";
    public static final String FUNC_TRANSFORM_REGEN = "getTransformTimerRegen";
    public static final String KEY_AIM = "AIM";
    public static final String KEY_TRANSFORM = "TRANSFORM";

    public static final Ability ABSOLUTE_INTANGIBILITY = new AbilityIntangibility(8, true);
    public static final Ability ACCELERATED_PERCEPTION = new AbilitySlowMotion(3);
    public static final Ability ARCHERY = new AbilityArchery(3);
    public static final Ability CACTUS_PHYSIOLOGY = new AbilityCactusPhysiology(4);
    public static final Ability CANARY_CRY = new AbilityCanaryCry(4);
    public static final Ability CELLULAR_REGENERATION = new AbilityPotionImmunity(5, Potion.confusion, Potion.poison, Potion.wither);
    public static final Ability COLD_RESISTANCE = new AbilityColdResistance(3);
    public static final Ability CRYOKINESIS = new AbilityCryokinesis(4);
    public static final Ability ELECTROKINESIS = new Ability(7);
    public static final Ability ENERGY_BLAST = new Ability(7);
    public static final Ability ENERGY_PROJECTION = new AbilityEnergyProjection(8);
    public static final Ability ENHANCED_REFLEXES = new AbilityEnhancedReflexes(1);
    public static final Ability EXPLOSION_IMMUNITY = new AbilityExplosionImmunity(6);
    public static final Ability FIRE_IMMUNITY = new AbilityFireImmunity(6);
    public static final Ability FLIGHT = new AbilityFlight(8);
    public static final Ability GEOKINESIS = new Ability(4);
    public static final Ability GLIDING = new AbilityGliding(2);
    public static final Ability GLIDING_FLIGHT = new AbilityGliding(7);
    public static final Ability HEALING_FACTOR = new AbilityHealingFactor(6);
    public static final Ability HEAT_RESISTANCE = new AbilityHeatResistance(2);
    public static final Ability HEAT_VISION = new AbilityHeatVision(5);
    public static final Ability HOVER = new AbilityHover(1);
    public static final Ability INTANGIBILITY = new AbilityIntangibility(8, false);
    public static final Ability INVISIBILITY = new AbilityInvisibility(6);
    public static final Ability LEAPING = new Ability(1);
    public static final Ability PROPELLED_FLIGHT = new AbilityFlight(7);
    public static final Ability PYROKINESIS = new AbilityPyrokinesis(4);
    public static final Ability REPULSOR_BLAST = new AbilityRepulsorBlast(4);
    public static final Ability RETRACTABLE_BLADE = new AbilityBlade(5);
    public static final Ability RETRACTABLE_SHIELD = new AbilityShield(4);
    public static final Ability SENTRY_MODE = new Ability(3);
    public static final Ability SHAPE_SHIFTING = new AbilityShapeShifting(4);
    public static final Ability SIZE_MANIPULATION = new AbilitySizeManipulation(6);
    public static final Ability SPELLCASTING = new AbilitySpellcasting(8);
    public static final Ability STEEL_TRANSFORMATION = new AbilitySteelTransformation(7);
    public static final Ability SUPERHUMAN_DURABILITY = new AbilityDurability(8);
    public static final Ability SUPER_SPEED = new AbilitySuperSpeed(7);
//    public static final Ability TELEKINESIS = new AbilityTelekinesis(7);
    public static final Ability TELEPORTATION = new AbilityTeleportation(8);
    public static final Ability THROWING_STARS = new AbilityEquipment(4).add(EnumEquipment.THROWING_STAR);
    public static final Ability UMBRAKINESIS = new AbilityUmbrakinesis(6);
    public static final Ability UTILITY_BELT = new AbilityEquipment(5).add(EnumEquipment.EQUIPMENT_BATMAN);

    protected final Random rand = new Random();

    private final int tier;

    public Ability(int tier)
    {
        this.tier = tier;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "ability." + getName().replace(':', '.');
    }

    @Override
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(getUnlocalizedName() + ".name");
    }

    public int getTier()
    {
        return tier;
    }

    public boolean isActive(EntityLivingBase entity)
    {
        return true;
    }

    public boolean renderIcon(EntityPlayer player)
    {
        return false;
    }

    public int getX()
    {
        return 0;
    }

    public int getY()
    {
        return 0;
    }

    @Override
    public String getName()
    {
        return delegate.name();
    }

    @Override
    public int compareTo(Ability o)
    {
        return getLocalizedName().compareTo(o.getLocalizedName());
    }

    @Override
    public boolean test(Entity input)
    {
        return input instanceof EntityLivingBase && SHHelper.hasEnabledModifier((EntityLivingBase) input, this);
    }

    @Override
    public String toString()
    {
        return "Ability[" + getName() + "]";
    }

    static
    {
        for (Field field : Ability.class.getFields())
        {
            if (field.getType() == Ability.class)
            {
                try
                {
                    register(field.getName().toLowerCase(Locale.ROOT), (Ability) field.get(null));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
