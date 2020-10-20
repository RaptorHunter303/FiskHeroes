package com.fiskmods.heroes.common.data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.DimensionalCoords;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Heroes;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.network.MessagePlayerData;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.common.predicate.SHPredicates;
import com.fiskmods.heroes.common.registry.FiskRegistryEntry;
import com.fiskmods.heroes.common.registry.FiskRegistryNumerical;
import com.fiskmods.heroes.pack.accessor.JSCoordsAccessor;
import com.fiskmods.heroes.pack.accessor.JSItemAccessor;
import com.fiskmods.heroes.pack.accessor.JSQuiverAccessor;
import com.fiskmods.heroes.util.FiskPredicates;
import com.fiskmods.heroes.util.NBTHelper;
import com.fiskmods.heroes.util.QuiverHelper.Quiver;
import com.fiskmods.heroes.util.SHFormatHelper;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public class SHData<T> extends FiskRegistryEntry<SHData<?>>
{
    public static final FiskRegistryNumerical<SHData<?>> REGISTRY = new FiskRegistryNumerical(FiskHeroes.MODID, null);
    private static final Map<Class, BiFunction> ACCESSORS = new HashMap<>();

    public static void register(String key, SHData value)
    {
        REGISTRY.putObject(key, value);
    }

    public static SHData getDataFromName(String key)
    {
        return REGISTRY.getObject(key);
    }

    public static String getNameForData(SHData value)
    {
        return REGISTRY.getNameForObject(value);
    }

    public static Object getJSWrappedDataValue(String key, Entity entity, Function<SHData, Object> func)
    {
        SHData data = SHData.REGISTRY.getObject(key);
        return data != null ? data.wrapJS(func.apply(data), entity) : null;
    }

    protected static final int SAVE_NBT = 0x1;
    protected static final int SYNC_BYTES = 0x2;
    protected static final int RESET_ON_DEATH = 0x4;
    protected static final int RESET_WO_SUIT = 0x8;
    protected static final int RESET_WO_PRED = 0x10;
    protected static final int RESET = RESET_ON_DEATH | RESET_WO_SUIT;
    protected static final int COMMAND_ACCESSIBLE = 0x20;

    public static final SHData<Boolean> SPEEDING = new SHData<>(false, t -> t instanceof EntityLivingBase && SpeedsterHelper.hasSuperSpeed((EntityLivingBase) t)).setExempt(RESET_WO_SUIT);
    public static final SHData<Byte> SPEED = new SHData<>((byte) 10).setExempt(RESET_WO_SUIT);
    public static final SHData<Boolean> SLOW_MOTION = new SHData<>(false, Ability.ACCELERATED_PERCEPTION);
    public static final SHData<Byte> SELECTED_ARROW = new SHData<>((byte) 0).setExempt(RESET);
    public static final SHData<ItemStack> CURRENT_ARROW = new SHData<>().setExempt(SAVE_NBT | RESET);
    public static final SHData<Short> TIME_SINCE_DAMAGED = new SHData<>((short) 0, Ability.HEALING_FACTOR).setExempt(SAVE_NBT | RESET_WO_PRED);
    public static final SHData<Boolean> AIMING = new SHData<>(false, t -> t.isEntityAlive()).setExempt(SAVE_NBT);
    public static final SHData<Boolean> PREV_AIMING = new SHData<>(false).setExempt(SAVE_NBT);
    public static final SHData<Boolean> MASK_OPEN = new SHData<>(false);
    public static final SHData<Byte> MASK_OPEN_TIMER = new SHData<>((byte) 0);
    public static final SHData<String> DISGUISE = new SHData<>(Ability.SHAPE_SHIFTING);
    public static final SHData<Float> SHAPE_SHIFT_TIMER = new SHData<>(0.0F, Ability.SHAPE_SHIFTING).setExempt(SAVE_NBT);
    public static final SHData<Boolean> SHAPE_SHIFTING = new SHData<>(false, Ability.SHAPE_SHIFTING).setExempt(SAVE_NBT);
    public static final SHData<String> SHAPE_SHIFTING_FROM = new SHData<>(Ability.SHAPE_SHIFTING).setExempt(SAVE_NBT);
    public static final SHData<String> SHAPE_SHIFTING_TO = new SHData<>(Ability.SHAPE_SHIFTING).setExempt(SAVE_NBT);
    public static final SHData<Boolean> INTANGIBLE = new SHDataIntangible(false, Ability.INTANGIBILITY.or(Ability.ABSOLUTE_INTANGIBILITY).and(t -> t.isEntityAlive()));
    public static final SHData<Boolean> INVISIBLE = new SHDataInvisible(false, Ability.INVISIBILITY.and(t -> t.isEntityAlive()));
    public static final SHData<Boolean> SHRINKING = new SHData<>(false, Ability.SIZE_MANIPULATION).setExempt(SAVE_NBT);
    public static final SHData<Boolean> GROWING = new SHData<>(false, Ability.SIZE_MANIPULATION).setExempt(SAVE_NBT);
    public static final SHData<Boolean> SHOOTING = new SHData<>(false, t -> t.isEntityAlive()).setExempt(SAVE_NBT);
    public static final SHData<Byte> SPEED_EXPERIENCE_LEVEL = new SHData<>((byte) 0).setExempt(RESET);
    public static final SHData<Integer> SPEED_EXPERIENCE_TOTAL = new SHData<>(0).setExempt(RESET);
    public static final SHData<Float> SPEED_EXPERIENCE_BAR = new SHData<>(0.0F).setExempt(RESET);
    public static final SHData<Integer> SPEED_LEVEL_UP_COOLDOWN = new SHData<>(0).setExempt(SAVE_NBT | RESET_WO_SUIT);
    public static final SHData<Boolean> PREV_ON_GROUND = new SHData<>(false).setExempt(SAVE_NBT | RESET);
    public static final SHData<Boolean> IS_SWING_IN_PROGRESS = new SHData<>(false).setExempt(SAVE_NBT | RESET);
    public static final SHData<Byte> SWING_PROGRESS_INT = new SHData<>((byte) 0).setExempt(SAVE_NBT | RESET);
    public static final SHData<Boolean> RIGHT_TONFA_STATE = new SHData<>(false).setExempt(SAVE_NBT | RESET);
    public static final SHData<Boolean> LEFT_TONFA_STATE = new SHData<>(false).setExempt(SAVE_NBT | RESET);
    public static final SHData<Hero> PREV_HERO = new SHData<>().setExempt(SAVE_NBT | RESET | COMMAND_ACCESSIBLE).setTabCompletions(Hero.REGISTRY.getKeys());
    public static final SHData<Float> CRYO_CHARGE = new SHData<>(0.0F, Ability.CRYOKINESIS).revokePerms(Side.CLIENT);
    public static final SHData<Boolean> CRYO_CHARGING = new SHData<>(false, Ability.CRYOKINESIS).setExempt(SAVE_NBT);
    public static final SHData<Byte> UTILITY_BELT_TYPE = new SHData<>((byte) 0).setExempt(RESET);
    public static final SHData<Byte> PREV_UTILITY_BELT_TYPE = new SHData<>((byte) 0).setExempt(RESET);
    public static final SHData<Boolean> SUIT_OPEN = new SHData<>(false).setExempt(RESET_WO_SUIT);
    public static final SHData<Byte> SUIT_OPEN_TIMER = new SHData<>((byte) 0).setExempt(RESET_WO_SUIT);
    public static final SHData<Boolean> GLIDING = new SHData<>(false, Ability.GLIDING.or(Ability.GLIDING_FLIGHT));
    public static final SHData<Boolean> PREV_GLIDING = new SHData<>(false).setExempt(RESET | COMMAND_ACCESSIBLE);
    public static final SHData<Integer> TICKS_GLIDING = new SHData<>(0, GLIDING.isValue(true));
    public static final SHData<Integer> TICKS_SINCE_GLIDING = new SHData<>(0, GLIDING.isValue(false)).setExempt(RESET);
    public static final SHData<Boolean> PREV_JUMPING = new SHData<>(false).setExempt(SAVE_NBT);
    public static final SHData<DimensionalCoords> QR_ORIGIN = new SHData<>(DimensionalCoords.factory()).setExempt(RESET);
    public static final SHData<Float> QR_TIMER = new SHData<>(0.0F).setExempt(RESET_WO_SUIT);
    public static final SHData<Boolean> HOVERING = new SHData<>(false, Ability.HOVER.and(t -> !t.onGround).and(FiskPredicates.IS_FLYING.negate()));
    public static final SHData<Boolean> JETPACKING = new SHData<>(false, Ability.PROPELLED_FLIGHT).setExempt(SAVE_NBT).revokePerms(Side.SERVER);
    public static final SHData<Boolean> PENETRATE_MARTIAN_INVIS = new SHData<>(false).setExempt(RESET);
    public static final SHData<Boolean> TREADMILL_DECREASING = new SHData<>(true).setExempt(SAVE_NBT);
    public static final SHData<Boolean> HORIZONTAL_BOW = new SHData<>(false, Ability.ARCHERY).setExempt(SAVE_NBT);
    public static final SHData<Integer> METAL_HEAT_COOLDOWN = new SHData<>(0).setExempt(RESET_WO_SUIT);
    public static final SHData<Cooldowns> ABILITY_COOLDOWNS = new SHData<>(Cooldowns.factory()).setExempt(RESET | SYNC_BYTES | COMMAND_ACCESSIBLE).revokePerms();
    public static final SHData<Byte> FLIGHT_ANIMATION = new SHData<>((byte) 0, Ability.PROPELLED_FLIGHT).setExempt(SAVE_NBT | RESET_ON_DEATH);
    public static final SHData<Boolean> SUPERHERO_LANDING = new SHData<>(false).setExempt(SAVE_NBT | SYNC_BYTES | RESET).revokePerms();
    public static final SHData<Boolean> STEELED = new SHData<>(false, Ability.STEEL_TRANSFORMATION.and(t -> t.isEntityAlive()));
    public static final SHData<Float> STEEL_COOLDOWN = new SHData<>(0.0F).setExempt(RESET_WO_SUIT);
    public static final SHData<Boolean> SHIELD = new SHData<>(false, Ability.RETRACTABLE_SHIELD.and(t -> !(t instanceof EntityLivingBase) || ((EntityLivingBase) t).getHeldItem() == null));
    public static final SHData<Boolean> SHIELD_BLOCKING = new SHData<>(false, SHIELD.isValue(true)).setExempt(SAVE_NBT);
    public static final SHData<Boolean> SHADOWFORM = new SHDataInvisible(false, Ability.UMBRAKINESIS.and(t -> t.isEntityAlive()));
    public static final SHData<Boolean> PREV_SHADOWFORM = new SHData(false).setExempt(RESET | COMMAND_ACCESSIBLE);
    public static final SHData<Float> SHADOWFORM_COOLDOWN = new SHData<>(0.0F).setExempt(RESET_WO_SUIT);
    public static final SHData<Boolean> LIGHTSOUT = new SHData<>(false, Ability.UMBRAKINESIS.and(t -> t.isEntityAlive())).setExempt(SAVE_NBT);
    public static final SHData<Float> INTANGIBILITY_COOLDOWN = new SHData<>(0.0F).setExempt(RESET_WO_SUIT);
    public static final SHData<Byte> TELEPORT_DELAY = new SHData<>((byte) 0, Ability.TELEPORTATION).revokePerms(Side.CLIENT).setExempt(SAVE_NBT);
    public static final SHData<DimensionalCoords> TELEPORT_DEST = new SHData<>((DimensionalCoords) null, Ability.TELEPORTATION).revokePerms(Side.CLIENT).setExempt(SAVE_NBT);
    public static final SHData<Boolean> TRANSFORMED = new SHData<>(false, SHPredicates.heroPred(t -> t.getKeyBinding(Ability.KEY_TRANSFORM) > 0).and(t -> t.isEntityAlive()));
    public static final SHData<Float> TRANSFORM_COOLDOWN = new SHData<>(0.0F).setExempt(RESET_WO_SUIT);
    public static final SHData<Integer> PREV_TRANSFORM_MAX = new SHData<>(-1).setExempt(RESET_WO_SUIT);
    public static final SHData<Boolean> BLADE = new SHData<>(false, Ability.RETRACTABLE_BLADE.and(t -> !(t instanceof EntityLivingBase) || ((EntityLivingBase) t).getHeldItem() == null));
    public static final SHData<Float> SPELL_FRACTION = new SHData<>(0.0F, Ability.SPELLCASTING).setExempt(SAVE_NBT);
    public static final SHData<Boolean> SPODERMEN = new SHData<>(false).setExempt(RESET).revokePerms(Side.CLIENT);

    public static final SHData<Quiver> EQUIPPED_QUIVER = new SHData<>().setExempt(RESET);
    public static final SHData<Byte> EQUIPPED_QUIVER_SLOT = new SHData<>((byte) -1).setExempt(SAVE_NBT | RESET).revokePerms(Side.SERVER);
    public static final SHData<Byte> EQUIPPED_TACHYON_DEVICE_SLOT = new SHData<>((byte) -1).setExempt(SAVE_NBT | RESET).revokePerms(Side.SERVER);
    public static final SHData<Boolean> TACHYON_DEVICE_ON = new SHData<>(false).setExempt(SAVE_NBT | RESET);
    public static final SHData<Byte> HAS_CPT_AMERICAS_SHIELD = new SHData<>((byte) 0).setExempt(RESET);
    public static final SHData<Byte> HAS_DEADPOOLS_SWORDS = new SHData<>((byte) 0).setExempt(RESET);
    public static final SHData<Byte> HAS_PROMETHEUS_SWORD = new SHData<>((byte) 0).setExempt(RESET);

    public static final SHDataInterp<Float> SCALE = new SHDataInterp<>(1.0F, SHHelper::isHero).revokePerms(Side.CLIENT);
    public static final SHDataInterp<Float> STEEL_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> SHIELD_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> SHIELD_BLOCKING_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> BLADE_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> LIGHTSOUT_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> MASK_OPEN_TIMER2 = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> AIMING_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> AIMED_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> SHOOTING_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> INVISIBILITY_TIMER = new SHDataInterp<>(0.0F, Ability.INVISIBILITY).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> INTANGIBILITY_TIMER = new SHDataInterp<>(0.0F, Ability.INTANGIBILITY.or(Ability.ABSOLUTE_INTANGIBILITY)).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> SHADOWFORM_TIMER = new SHDataInterp<>(0.0F, Ability.UMBRAKINESIS).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> SPELLCAST_TIMER = new SHDataInterp<>(0.0F, Ability.SPELLCASTING).setExempt(SAVE_NBT | SYNC_BYTES);
    public static final SHDataInterp<Float> TRANSFORM_TIMER = new SHDataInterp<>(0.0F, SHPredicates.heroPred(t -> t.getKeyBinding(Ability.KEY_TRANSFORM) > 0)).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> SWING_PROGRESS = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT | RESET);
    public static final SHDataInterp<Float> TACHYON_CHARGE = new SHDataInterp<>(0.0F).setExempt(RESET);
    public static final SHDataInterp<Float> WING_ANIMATION_TIMER = new SHDataInterp<>(0.0F, Ability.GLIDING.or(Ability.GLIDING_FLIGHT));
    public static final SHDataInterp<Float> TREADMILL_LIMB_FACTOR = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT | RESET);
    public static final SHDataInterp<Float> TREADMILL_LIMB_PROGRESS = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT | RESET);
    public static final SHDataInterp<Float> HORIZONTAL_BOW_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> VEL9_CONVERT = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT | RESET);
    public static final SHDataInterp<Float> METAL_HEAT = new SHDataInterp<>(0.0F).setExempt(RESET);
    public static final SHDataInterp<Float> RELOAD_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> SCOPE_TIMER = new SHDataInterp<>(0.0F).setExempt(SAVE_NBT);
    public static final SHDataInterp<Float> HAT_TIP = new SHDataInterp<>(0.0F, t -> Heroes.senor_cactus.test(t)).setExempt(SAVE_NBT).revokePerms(Side.CLIENT);
    public static final SHDataInterp<Double> HEAT_VISION_LENGTH = new SHDataInterp<>(0.0, Ability.HEAT_VISION.or(Ability.ENERGY_PROJECTION)).setExempt(SAVE_NBT);

//    public static final SHDataInterp<Float> FOCUS_TIME = new SHDataInterp<>(0F, SHPredicates.HAS_MOVESET).setExempt(SAVE_NBT);
//    public static final SHDataInterp<Float> FOCUS = new SHDataInterp<>(0.0F, SHPredicates.HAS_MOVESET).revokePerms(Side.CLIENT).setExempt(SAVE_NBT);
//    public static final SHData<Boolean> COMBAT_MODE = new SHDataCombat<>(false);
//    public static final SHData<Byte> COMBAT_INDEX = new SHDataCombat<>((byte) 0); // TODO: 1.4 Combat

    final DataFactory<T> defaultValue;
    public final Predicate<Entity> canSet;

    public String legacyID;
    public ClassType<T> typeClass;
    protected Iterable<String> tabCompletions = Collections.EMPTY_SET;
    protected int flags = -1;
    protected int permissions = -1;

    protected SHData(DataFactory<T> defaultVal, Predicate<Entity> p)
    {
        defaultValue = defaultVal;
        canSet = p;
    }

    protected SHData(T defaultVal, Predicate<Entity> p)
    {
        this(() -> defaultVal, p);
    }

    protected SHData(DataFactory<T> defaultVal)
    {
        this(defaultVal, null);
    }

    protected SHData(T defaultVal)
    {
        this(defaultVal, null);
    }

    protected SHData(Predicate<Entity> p)
    {
        this((T) null, p);
    }

    protected SHData()
    {
        this((T) null);
    }

    protected SHData setExempt(int exempt)
    {
        flags &= ~exempt;
        return this;
    }

    protected SHData revokePerms(Side side)
    {
        permissions &= ~(1 << side.ordinal());
        return this;
    }

    protected SHData revokePerms()
    {
        revokePerms(Side.CLIENT);
        revokePerms(Side.SERVER);
        return this;
    }

    protected SHData setTabCompletions(Iterable<String> iter)
    {
        tabCompletions = iter;
        return this;
    }

    protected SHData setTabCompletions(String... astring)
    {
        return setTabCompletions(Arrays.asList(astring));
    }

    protected boolean hasFlag(int flags, int flag)
    {
        return (flags & flag) == flag;
    }

    public boolean shouldSaveNBT()
    {
        return hasFlag(flags, SAVE_NBT);
    }

    public boolean shouldSyncBytes()
    {
        return hasFlag(flags, SYNC_BYTES);
    }

    public boolean shouldResetOnDeath()
    {
        return hasFlag(flags, RESET_ON_DEATH);
    }

    public boolean shouldResetWithoutSuit()
    {
        return hasFlag(flags, RESET_WO_SUIT);
    }

    public boolean shouldResetWithoutPredicate()
    {
        return hasFlag(flags, RESET_WO_PRED);
    }

    public boolean isCommandAccessible()
    {
        return hasFlag(flags, COMMAND_ACCESSIBLE);
    }

    public boolean hasPerms(Side side)
    {
        return hasFlag(permissions, 1 << side.ordinal());
    }

    public Iterable<String> getTabCompletions()
    {
        if (ofType(Boolean.class))
        {
            return Arrays.asList("true", "false");
        }

        return tabCompletions;
    }

    public Predicate<Entity> isValue(T value)
    {
        return t -> Objects.equal(value, get(t));
    }

    public boolean ofType(Class clazz)
    {
        return clazz.isAssignableFrom(typeClass.getType());
    }

    public Object wrapJS(T value, Entity entity)
    {
        return ACCESSORS.getOrDefault(typeClass.getType(), (t, e) -> t).apply(value, entity);
    }

    protected boolean validUpdate(Entity entity, T value)
    {
        if (ofType(ItemStack.class))
        {
            return !ItemStack.areItemStacksEqual((ItemStack) get(entity), (ItemStack) value);
        }

        return !Objects.equal(value, get(entity));
    }

    protected boolean legalUpdate(Entity entity)
    {
        return canSet == null || canSet.test(entity);
    }

    public T getDefault()
    {
        return defaultValue.construct();
    }

    public boolean set(Entity entity, T value)
    {
        if (entity instanceof IDataHolder)
        {
            return setWithoutNotify(entity, value);
        }
        else if (entity instanceof EntityPlayer)
        {
            return set((EntityPlayer) entity, value);
        }

        return false;
    }

    public boolean setWithoutNotify(Entity entity, T value)
    {
        return legalUpdate(entity) && setWithBypass(entity, value);
    }

    private boolean setWithBypass(Entity entity, T value)
    {
        if (validUpdate(entity, value))
        {
            if (entity instanceof IDataHolder)
            {
                ((IDataHolder) entity).set(this, value);
                onValueChanged(entity, value);
            }
            else if (entity instanceof EntityPlayer)
            {
                SHPlayerData.getData((EntityPlayer) entity).putData(this, value);
                onValueChanged(entity, value);
            }

            return true;
        }

        return false;
    }

    private boolean reset(Entity entity)
    {
        return setWithBypass(entity, getDefault());
    }

    public boolean incr(Entity entity, T value)
    {
        if (entity instanceof IDataHolder)
        {
            return incrWithoutNotify(entity, value);
        }
        else if (entity instanceof EntityPlayer)
        {
            return incr((EntityPlayer) entity, value);
        }

        return false;
    }

    public boolean incrWithoutNotify(Entity entity, T value)
    {
        if (value instanceof Byte)
        {
            return setWithoutNotify(entity, (T) Byte.valueOf((byte) ((Byte) get(entity) + (Byte) value)));
        }
        else if (value instanceof Short)
        {
            return setWithoutNotify(entity, (T) Short.valueOf((short) ((Short) get(entity) + (Short) value)));
        }
        else if (value instanceof Integer)
        {
            return setWithoutNotify(entity, (T) Integer.valueOf((Integer) get(entity) + (Integer) value));
        }
        else if (value instanceof Long)
        {
            return setWithoutNotify(entity, (T) Long.valueOf((Long) get(entity) + (Long) value));
        }
        else if (value instanceof Float)
        {
            return setWithoutNotify(entity, (T) Float.valueOf((Float) get(entity) + (Float) value));
        }
        else if (value instanceof Double)
        {
            return setWithoutNotify(entity, (T) Double.valueOf((Double) get(entity) + (Double) value));
        }
        else if (value instanceof String)
        {
            return setWithoutNotify(entity, (T) String.valueOf((String) get(entity) + (String) value));
        }

        throw new RuntimeException("Cannot increment a non-numerical data type unless a String!");
    }

    public boolean clamp(Entity entity, T min, T max)
    {
        if (entity instanceof IDataHolder)
        {
            return clampWithoutNotify(entity, min, max);
        }
        else if (entity instanceof EntityPlayer)
        {
            return clamp((EntityPlayer) entity, min, max);
        }

        return false;
    }

    public boolean clampWithoutNotify(Entity entity, T min, T max)
    {
        if (min instanceof Byte)
        {
            return setWithoutNotify(entity, (T) Byte.valueOf((byte) MathHelper.clamp_int((Byte) get(entity), (Byte) min, (Byte) max)));
        }
        else if (min instanceof Short)
        {
            return setWithoutNotify(entity, (T) Short.valueOf((short) MathHelper.clamp_int((Short) get(entity), (Short) min, (Short) max)));
        }
        else if (min instanceof Integer)
        {
            return setWithoutNotify(entity, (T) Integer.valueOf(MathHelper.clamp_int((Integer) get(entity), (Integer) min, (Integer) max)));
        }
        else if (min instanceof Long)
        {
            long l = (Long) get(entity);
            return setWithoutNotify(entity, (T) Long.valueOf(l < (Long) min ? (Long) min : l > (Long) max ? (Long) max : l));
        }
        else if (min instanceof Float)
        {
            return setWithoutNotify(entity, (T) Float.valueOf(MathHelper.clamp_float((Float) get(entity), (Float) min, (Float) max)));
        }
        else if (min instanceof Double)
        {
            return setWithoutNotify(entity, (T) Double.valueOf(MathHelper.clamp_double((Double) get(entity), (Double) min, (Double) max)));
        }

        throw new RuntimeException("Cannot clamp a non-numerical data type!");
    }

    public T get(Entity entity)
    {
        if (entity instanceof IDataHolder)
        {
            return ((IDataHolder) entity).get(this);
        }
        else if (entity instanceof EntityPlayer)
        {
            return SHPlayerData.getData((EntityPlayer) entity).getData(this);
        }

        return getDefault();
    }

    public boolean sync(EntityPlayer player)
    {
        if (hasPerms(player.worldObj.isRemote ? Side.CLIENT : Side.SERVER) && legalUpdate(player))
        {
            if (player.worldObj.isRemote)
            {
                SHNetworkManager.wrapper.sendToServer(new MessagePlayerData(player, this, get(player)));
            }
            else
            {
                SHNetworkManager.wrapper.sendToDimension(new MessagePlayerData(player, this, get(player)), player.dimension);
            }

            return true;
        }

        return false;
    }

    public boolean set(EntityPlayer player, T value)
    {
        return setWithoutNotify(player, value) && sync(player);
    }

    public boolean incr(EntityPlayer player, T value)
    {
        return incrWithoutNotify(player, value) && sync(player);
    }

    public boolean clamp(EntityPlayer player, T min, T max)
    {
        return clampWithoutNotify(player, min, max) && sync(player);
    }

    public static NBTTagCompound writeToNBT(NBTTagCompound nbt, Map<SHData, Object> data)
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (Map.Entry<SHData, Object> e : data.entrySet())
        {
            if (e.getKey().shouldSaveNBT())
            {
                NBTBase nbtbase = NBTHelper.writeToNBT(e.getValue());

                if (nbtbase != null)
                {
                    nbttagcompound.setTag(e.getKey().legacyID, nbtbase);
                }
            }
        }

        nbt.setTag("DataArray", nbttagcompound);

        return nbt;
    }

    public static Map<SHData, Object> readFromNBT(NBTTagCompound nbt, Map<SHData, Object> data)
    {
        NBTTagCompound nbttagcompound = nbt.getCompoundTag("DataArray");

        for (SHData type : SHData.REGISTRY)
        {
            if (type.shouldSaveNBT())
            {
                NBTBase tag = nbttagcompound.getTag(type.legacyID);
                Object obj = null;

                if (tag != null)
                {
                    obj = NBTHelper.readFromNBT(tag, type.typeClass);
                }
                else
                {
                    tag = nbt.getTag(type.legacyID);

                    if (tag != null)
                    {
                        obj = NBTHelper.readFromNBT(tag, type.typeClass);
                    }
                    else
                    {
                        continue;
                    }
                }

                data.put(type, obj);
            }
        }

        return data;
    }

    public static void toBytes(ByteBuf buf, Map<SHData, Object> data)
    {
        buf.writeInt(SHData.REGISTRY.getKeys().size());

        for (SHData type : SHData.REGISTRY)
        {
            if (type.shouldSyncBytes())
            {
                boolean flag = true;

                if (type.defaultValue.canEqual())
                {
                    Object a = type.getDefault();
                    Object b = data.containsKey(type) ? data.get(type) : a;

                    flag = !Objects.equal(a, b);
                }

                buf.writeBoolean(flag);

                if (flag)
                {
                    NBTHelper.toBytes(buf, data.get(type));
                }
            }
        }
    }

    public static Map<SHData, Object> fromBytes(ByteBuf buf, Map<SHData, Object> data)
    {
        int size = buf.readInt();

        if (size != SHData.REGISTRY.getKeys().size())
        {
            throw new RuntimeException(String.format("Incompatible data registries - this is really bad! Received %s, expected: %s", size, SHData.REGISTRY.getKeys().size()));
        }

        for (SHData type : SHData.REGISTRY)
        {
            if (type.shouldSyncBytes() && buf.readBoolean())
            {
                data.put(type, NBTHelper.fromBytes(buf, type.typeClass));
            }
        }

        return data;
    }

    public static void onUpdate(Entity entity)
    {
        for (SHDataInterp data : Iterables.filter(SHData.REGISTRY, SHDataInterp.class))
        {
            data.update(entity);
        }

        if (entity instanceof EntityLivingBase && !SHHelper.isHero((EntityLivingBase) entity))
        {
            for (SHData data : SHData.REGISTRY)
            {
                if (data.shouldResetWithoutSuit())
                {
                    data.reset(entity);
                }
            }
        }

        for (SHData data : SHData.REGISTRY)
        {
            if (data.shouldResetWithoutPredicate() && !data.legalUpdate(entity))
            {
                data.reset(entity);
            }
        }
    }

    public static void onDeath(Entity entity)
    {
        for (SHData data : SHData.REGISTRY)
        {
            if (data.shouldResetOnDeath())
            {
                data.reset(entity);
            }
        }
    }

    public static boolean isTracking(Entity entity)
    {
        return entity instanceof EntityPlayer || entity instanceof IDataHolder;
    }

    protected void init(Field field, String name) throws ClassNotFoundException
    {
        legacyID = SHFormatHelper.getUnconventionalName(name);
        typeClass = ClassType.construct(field.getGenericType().getTypeName()).getParam();

        register(name.toLowerCase(Locale.ROOT), this);
    }

    protected void onValueChanged(Entity entity, T value)
    {
    }

    @Override
    public String toString()
    {
        return getNameForData(this);
    }

    public static <T> void registerJSAccessor(Class<T> type, BiFunction<T, Entity, Object> accessor)
    {
        ACCESSORS.put(type, accessor);
    }

    public static <T> void registerJSAccessor(Class<T> type, Function<T, Object> accessor)
    {
        ACCESSORS.put(type, (t, e) -> accessor.apply((T) t));
    }

    public static void init()
    {
        registerJSAccessor(DimensionalCoords.class, JSCoordsAccessor::wrap);
        registerJSAccessor(ItemStack.class, JSItemAccessor::wrap);
        registerJSAccessor(Quiver.class, JSQuiverAccessor::wrap);

        for (Field field : SHData.class.getFields())
        {
            if (SHData.class.isAssignableFrom(field.getType()))
            {
                try
                {
                    ((SHData) field.get(null)).init(field, field.getName());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class ClassType<C>
    {
        private final Class<C> type;
        private ClassType param;

        public ClassType(Class<C> c)
        {
            type = c;
        }

        private ClassType<C> setParam(ClassType type)
        {
            param = type;
            return this;
        }

        public ClassType getParam()
        {
            return param;
        }

        public ClassType getParamSafe()
        {
            return param == null ? new ClassType(Object.class) : param;
        }

        public Class<C> getType()
        {
            return type;
        }

        @Override
        public String toString()
        {
            String s = getType().getCanonicalName();

            if (getParam() != null)
            {
                return s + "<" + getParam() + ">";
            }

            return s;
        }

        public static ClassType construct(String typeName) throws ClassNotFoundException
        {
            if (typeName.contains("<"))
            {
                ClassType type = new ClassType(Class.forName(typeName.substring(0, typeName.indexOf('<'))));
                ClassType type1 = construct(typeName.substring(typeName.indexOf('<') + 1, typeName.length() - 1));

                return type.setParam(type1);
            }

            return new ClassType(Class.forName(typeName));
        }
    }

    public interface DataFactory<T>
    {
        T construct();

        default boolean canEqual()
        {
            return true;
        }

        public static <T> DataFactory<T> create(Supplier<T> construct, boolean canEqual)
        {
            return new DataFactory<T>()
            {
                @Override
                public T construct()
                {
                    return construct.get();
                }

                @Override
                public boolean canEqual()
                {
                    return canEqual;
                }
            };
        }
    }
}
