package com.fiskmods.heroes.common.move;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.registry.FiskRegistryEntry;
import com.fiskmods.heroes.common.registry.FiskSimpleRegistry;
import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public abstract class Move extends FiskRegistryEntry<Move> implements Comparable<Move>
{
    public static final FiskSimpleRegistry<Move> REGISTRY = new FiskSimpleRegistry(FiskHeroes.MODID, null);

    public static void register(String key, Move value)
    {
        REGISTRY.putObject(key, value);
    }

    public static Move getMoveFromName(String key)
    {
        return REGISTRY.getObject(key);
    }

    public static String getNameForMove(Move value)
    {
        return REGISTRY.getNameForObject(value);
    }

    public static final String KEY_QUANTITY = "QUANTITY";

    public static final Move ICICLES = new MoveIcicles(2);
    public static final Move SPIKE_RING = new MoveSpikeRing(2);
    public static final Move SHIELD_THROW = new MoveShieldThrow(2);
    public static final Move CHARGED_PUNCH = new MoveChargedPunch(2);

    protected final Random rand = new Random();

    public final BiPredicate<MouseAction, Integer> action;
    public final int tier;

    private BiFunction<String, Number, Object> modifierFormat = (s, n) -> n;
    private boolean continualUse;

    public Move(int tier, BiPredicate<MouseAction, Integer> action)
    {
        this.action = action;
        this.tier = tier;
    }

    protected void setModifierFormat(BiFunction<String, Number, Object> func)
    {
        modifierFormat = func;
    }

    protected void setModifierFormat(Function<Number, Object> func)
    {
        setModifierFormat((s, n) -> func.apply(n));
    }

    protected void setModifierFormat(String key, Function<Number, Object> func)
    {
        setModifierFormat((s, n) -> key.equals(s) ? func.apply(n) : modifierFormat.apply(s, n));
    }

    public final Object formatModifier(String key, Number n)
    {
        return modifierFormat.apply(key, n);
    }

    public Move setContinualUse()
    {
        continualUse = true;
        return this;
    }

    public boolean isContinualUse()
    {
        return continualUse;
    }

    public String getUnlocalizedName()
    {
        return "move." + delegate.name().replace(':', '.');
    }

    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(getUnlocalizedName() + ".name");
    }

    public String localizeModifier(String key)
    {
        return null;
    }

    public abstract boolean onActivated(EntityLivingBase entity, Hero hero, MovingObjectPosition mop, MoveActivation activation, ImmutableMap<String, Number> modifiers, float focus);

    public boolean canPickupItem(InventoryPlayer inventory, ItemStack stack, MoveSet set)
    {
        return false;
    }

    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase)
    {
    }

    public void onRemoved(EntityLivingBase entity, Hero hero)
    {
    }

    public boolean canTakeDamage(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount)
    {
        return true;
    }

    public float damageTaken(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        return amount;
    }

    public float damageDealt(EntityLivingBase entity, EntityLivingBase target, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        return amount;
    }

    public boolean attackEntity(EntityPlayer player, Entity target, Hero hero, ImmutableMap<String, Number> modifiers, float focus)
    {
        return true;
    }

    public static void updateMoves(EntityLivingBase entity, Hero hero, Phase phase)
    {
        if (hero != null)
        {
//            for (Move move : hero.getMoves())
//            {
//                move.onUpdate(entity, hero, phase);
//            }
        }
    }

    @Override
    public int compareTo(Move o)
    {
        return getLocalizedName().compareTo(o.getLocalizedName());
    }

    static
    {
        for (Field field : Move.class.getFields())
        {
            if (field.getType() == Move.class)
            {
                try
                {
                    register(field.getName().toLowerCase(Locale.ROOT), (Move) field.get(null));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static enum MouseAction implements BiPredicate<MouseAction, Integer>
    {
        RIGHT_CLICK,
        LEFT_CLICK;

        public BiPredicate<MouseAction, Integer> with(int sensitivity)
        {
            return (t, u) -> this == t && (u & sensitivity) == u;
        }

        @Override
        public boolean test(MouseAction t, Integer u)
        {
            return this == t;
        }

        public static MouseAction get(Action action)
        {
            return action == Action.LEFT_CLICK_BLOCK ? LEFT_CLICK : RIGHT_CLICK;
        }
    }

    public static enum MoveActivation
    {
        RIGHT_CLICK_ENTITY(false, MoveSensitivity.ENTITY),
        RIGHT_CLICK_BLOCK(false, MoveSensitivity.BLOCK),
        RIGHT_CLICK_AIR(false, MoveSensitivity.AIR),
        LEFT_CLICK_ENTITY(true, MoveSensitivity.ENTITY),
        LEFT_CLICK_BLOCK(true, MoveSensitivity.BLOCK),
        LEFT_CLICK_AIR(true, MoveSensitivity.AIR);

        public final int sensitivity;
        public final MouseAction button;

        private MoveActivation(boolean left, int sensitivity)
        {
            this.sensitivity = sensitivity;
            button = left ? MouseAction.LEFT_CLICK : MouseAction.RIGHT_CLICK;
        }

        public static MoveActivation get(MouseAction action, int sensitivity)
        {
            switch (sensitivity)
            {
            case MoveSensitivity.ENTITY:
                return action == MouseAction.LEFT_CLICK ? LEFT_CLICK_ENTITY : RIGHT_CLICK_ENTITY;
            case MoveSensitivity.BLOCK:
                return action == MouseAction.LEFT_CLICK ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK;
            case MoveSensitivity.AIR:
                return action == MouseAction.LEFT_CLICK ? LEFT_CLICK_AIR : RIGHT_CLICK_AIR;
            default:
                return RIGHT_CLICK_AIR;
            }
        }
    }

    public static interface MoveSensitivity
    {
        int ENTITY = 0x1;
        int BLOCK = 0x2;
        int AIR = 0x4;
        int ANY = ENTITY | BLOCK | AIR;
    }
}
