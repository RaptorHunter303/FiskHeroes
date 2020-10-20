package com.fiskmods.heroes.common.hero;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.fiskmods.heroes.client.keybinds.SHKeyBinds;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.attribute.IAttributeContainer;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.hero.HeroIteration.Candidate;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityBlade;
import com.fiskmods.heroes.common.hero.modifier.AbilityShield;
import com.fiskmods.heroes.common.hero.modifier.AbilitySteelTransformation;
import com.fiskmods.heroes.common.hero.modifier.HeroModifier;
import com.fiskmods.heroes.common.hero.modifier.Weakness;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.move.MoveSet;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.NBTHelper;
import com.fiskmods.heroes.util.NBTHelper.INBTSaveAdapter;
import com.fiskmods.heroes.util.NBTHelper.INBTSavedObject;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public abstract class Hero implements Comparable<Hero>, Predicate<Entity>, INBTSavedObject<Hero>
{
    private static final Comparator<Hero> COMPARATOR = Comparator.comparing(Hero::getLocalizedName).thenComparing(Hero::getVersionSafe);
    public static final HeroRegistry REGISTRY = new HeroRegistry();

    public static Hero getHeroFromName(String key)
    {
        return REGISTRY.getObject(key);
    }

    public static String getNameForHero(Hero value)
    {
        return REGISTRY.getNameForObject(value);
    }

    private ImmutableMap<Integer, HeroIteration> iterations;
    private ImmutableMap<String, HeroIteration> iterationKeyMap;

    private ResourceLocation registryName = null;
    private HeroIteration defaultIteration;

    private final ImmutableSet<HeroModifier> modifiers;
    private final ImmutableSet<Ability> abilities;
    private final ImmutableSet<Weakness> weaknesses;

    private ImmutableList<ItemStack> equipment;
    private MoveSet moveSet;

    private byte armorSignature;
    private int armorCount;

    public Hero()
    {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        genModifiers(builder::add);

        modifiers = builder.build();
        abilities = ImmutableSet.copyOf(Iterables.filter(modifiers, Ability.class));
        weaknesses = ImmutableSet.copyOf(Iterables.filter(modifiers, Weakness.class));
    }

    final void register(ResourceLocation id, Map<String, Candidate> map)
    {
        if (registryName == null)
        {
            registryName = id;

            Map idMap = new HashMap<>();
            Map keyMap = new HashMap<>();
            idMap.put(-1, defaultIteration = new HeroIteration(this, -1, null, null));

            if (map != null)
            {
                for (Map.Entry<String, Candidate> e : map.entrySet())
                {
                    HeroIteration iter = new HeroIteration(this, idMap.size() - 1, e.getKey(), e.getValue());
                    idMap.put(iter.id, iter);

                    if (iter.key != null)
                    {
                        keyMap.put(iter.key, iter);
                    }
                }
            }

            iterations = ImmutableMap.copyOf(idMap);
            iterationKeyMap = ImmutableMap.copyOf(keyMap);

            init();
        }
    }

    public final ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public final String getName()
    {
        return String.valueOf(registryName);
    }

    public final String getDomain()
    {
        return registryName.getResourceDomain();
    }

    protected final void setHelmet(String armorType)
    {
        setArmor(0, armorType);
    }

    protected final void setChestplate(String armorType)
    {
        setArmor(1, armorType);
    }

    protected final void setLeggings(String armorType)
    {
        setArmor(2, armorType);
    }

    protected final void setBoots(String armorType)
    {
        setArmor(3, armorType);
    }

    protected final void setArmor(int slot, String armorType)
    {
        if (getItem(slot).register(this, armorType))
        {
            armorSignature |= 1 << slot;
            ++armorCount;
        }
    }

    public ItemHeroArmor getItem(int slot)
    {
        return ModItems.heroArmor[slot];
    }

    public final boolean hasPieceOfSet(int slot)
    {
        int i = 1 << slot;
        return (armorSignature & i) == i;
    }

    public final int getFirstPieceOfSet()
    {
        for (int i = 0; i < 4; ++i)
        {
            if (hasPieceOfSet(i))
            {
                return i;
            }
        }

        return -1;
    }

    public final int getPiecesToSet()
    {
        return armorCount;
    }

    public final byte getArmorSignature()
    {
        return armorSignature;
    }

    protected final void setMoveSet(MoveSet set)
    {
        moveSet = set;
    }

    public MoveSet getMoveSet()
    {
        return moveSet;
    }

    protected abstract void init();

    public abstract Tier getTier();

    public abstract int getKeyBinding(String key);

    public abstract boolean hasKeyBinding(int keycode);

    public abstract Set<String> getKeyBindsMatching(int keycode);

    public abstract void translateKeys(Map<String, String> mappings);

    @SideOnly(Side.CLIENT)
    public final KeyBinding getKey(EntityLivingBase entity, String key)
    {
        if (!isKeyBindEnabled(entity, key))
        {
            return null;
        }

        int i = getKeyBinding(key);
        return i == -1 ? Minecraft.getMinecraft().gameSettings.keyBindAttack : SHKeyBinds.getMapping(i);
    }

    @SideOnly(Side.CLIENT)
    public final boolean isKeyPressed(EntityLivingBase entity, String key)
    {
        KeyBinding keybind = getKey(entity, key);
        return keybind != null && keybind.getIsKeyPressed();
    }

    public String getVersion()
    {
        return null;
    }

    public final String getVersionSafe()
    {
        return String.valueOf(getVersion());
    }

    public String getUnlocalizedName()
    {
        return "hero." + getName().replace(':', '.');
    }

    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(getUnlocalizedName() + ".name").trim().replace("\\u00f1", "\u00f1");
    }

    public void onRemoved(EntityLivingBase entity)
    {
        for (HeroModifier modifier : getEnabledModifiers(entity))
        {
            modifier.onRemoved(entity, this);
        }
    }

    public boolean canTakeDamage(EntityLivingBase entity, EntityLivingBase attacker, DamageSource source, float amount)
    {
        for (HeroModifier modifier : getEnabledModifiers(entity))
        {
            if (!modifier.canTakeDamage(entity, attacker, this, source, amount))
            {
                return false;
            }
        }

        return true;
    }

    public float damageTaken(EntityLivingBase entity, EntityLivingBase attacker, DamageSource source, float amount, float originalAmount)
    {
        for (HeroModifier modifier : getEnabledModifiers(entity))
        {
            amount = modifier.damageTaken(entity, attacker, this, source, amount, originalAmount);
        }

        return amount;
    }

    public float damageDealt(EntityLivingBase entity, EntityLivingBase target, DamageSource source, float amount, float originalAmount)
    {
        for (HeroModifier modifier : getEnabledModifiers(entity))
        {
            amount = modifier.damageDealt(entity, target, this, source, amount, originalAmount);
        }

        return amount;
    }

    public float damageReduction(EntityLivingBase entity, DamageSource source, float reduction)
    {
        for (HeroModifier modifier : getEnabledModifiers(entity))
        {
            reduction = modifier.damageReduction(entity, this, source, reduction);
        }

        return reduction;
    }

    public final ImmutableCollection<HeroIteration> getIterations()
    {
        return iterations.values();
    }

    public final HeroIteration getIteration(int id)
    {
        return iterations.getOrDefault(id, defaultIteration);
    }

    public final HeroIteration getIteration(String key)
    {
        return iterationKeyMap.getOrDefault(key, defaultIteration);
    }

    public final HeroIteration getDefault()
    {
        return defaultIteration;
    }

    public List getEquipment()
    {
        return new ArrayList<>();
    }

    public final ImmutableList<ItemStack> getEquipmentStacks()
    {
        if (equipment == null)
        {
            ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

            for (Object obj : getEquipment())
            {
                ItemStack itemstack = FiskServerUtils.getStackFrom(obj);

                if (itemstack != null)
                {
                    builder.add(itemstack);
                }
            }

            equipment = builder.build();
        }

        return equipment;
    }

    public final ImmutableSet<Ability> getAbilities()
    {
        return abilities;
    }

    public final ImmutableSet<Weakness> getWeaknesses()
    {
        return weaknesses;
    }

    public final ImmutableSet<HeroModifier> getModifiers()
    {
        return modifiers;
    }

    public final Set<HeroModifier> getEnabledModifiers(EntityLivingBase entity)
    {
        return Sets.filter(modifiers, t -> isModifierEnabled(entity, t));
    }

    protected void genModifiers(Consumer<HeroModifier> c)
    {
    }

    public final boolean hasModifier(HeroModifier modifier)
    {
        return getModifiers().contains(modifier);
    }

    public final boolean hasEnabledModifier(EntityLivingBase entity, HeroModifier modifier)
    {
        return getEnabledModifiers(entity).contains(modifier);
    }

    public void getAttributeModifiers(EntityLivingBase entity, IAttributeContainer attributes)
    {
        attributes.add(SHAttributes.DAMAGE_REDUCTION, getTier().getProtection(entity), 1);
    }

    public boolean hasProperty(EntityLivingBase entity, Property property)
    {
        return false;
    }

    public boolean hasPermission(EntityLivingBase entity, String permission)
    {
        return false;
    }

    public boolean isModifierEnabled(EntityLivingBase entity, HeroModifier modifier)
    {
        return true;
    }

    public boolean isKeyBindEnabled(EntityLivingBase entity, String keyBind)
    {
        return true;
    }

    public void onToggleMask(EntityLivingBase entity, boolean state)
    {
    }

//    public <T> T modifyRuleValue(EntityLivingBase entity, Rule<T> rule)
    public Number modifyRuleValue(EntityLivingBase entity, Rule<? extends Number> rule)
    {
        return rule.defaultValue;
    }

    public float getDefaultScale(EntityPlayer player)
    {
        return 1.0F;
    }

    public Object getFuncObject(EntityLivingBase entity, String key)
    {
        return null;
    }

    public final boolean getFuncBoolean(EntityLivingBase entity, String key, boolean defaultVal)
    {
        return getFunc(entity, key, Boolean.class, defaultVal);
    }

    public final float getFuncFloat(EntityLivingBase entity, String key, float defaultVal)
    {
        return getFunc(entity, key, Double.class, (double) defaultVal).floatValue();
    }

    public final int getFuncInt(EntityLivingBase entity, String key, int defaultVal)
    {
        return getFunc(entity, key, Integer.class, defaultVal);
    }

    public final String getFuncString(EntityLivingBase entity, String key, String defaultVal)
    {
        return getFunc(entity, key, String.class, defaultVal);
    }

    private <T> T getFunc(EntityLivingBase entity, String key, Class<T> c, T defaultVal)
    {
        Object obj = getFuncObject(entity, key);

        if (c.isInstance(obj))
        {
            return c.cast(obj);
        }

        return defaultVal;
    }

    public boolean isCosmic()
    {
        return false;
    }

    public boolean isHidden()
    {
        return false;
    }

    /**
     * @return The new material cost, or -1 if it should be calculated like normal
     */
    public int overrideMaterialCost()
    {
        return -1;
    }

    public String[] getHeaderText()
    {
        String s = getLocalizedName();
        String[] astring = new String[2];

        if (s != null && s.contains("/"))
        {
            astring[0] = s.substring(0, s.indexOf("/"));
            astring[1] = s.substring(s.indexOf("/") + 1);
        }
        else
        {
            astring[0] = s;
        }

        return astring;
    }

    public static void updateModifiers(EntityLivingBase entity, Hero hero, Phase phase)
    {
        if (hero != null)
        {
            for (HeroModifier modifier : hero.getModifiers())
            {
                modifier.onUpdate(entity, hero, phase, hero.isModifierEnabled(entity, modifier));
            }

            for (Weakness weakness : Weakness.REGISTRY)
            {
                weakness.onUpdateGlobal(entity, hero, phase, hero.hasEnabledModifier(entity, weakness));
            }
        }
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Hero && getName().equals(((Hero) obj).getName());
    }

    @Override
    public int compareTo(Hero o)
    {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean test(Entity input)
    {
        return input instanceof EntityLivingBase && SHHelper.getHero((EntityLivingBase) input) == this;
    }

    @Override
    public NBTBase writeToNBT()
    {
        return new NBTTagString(getName());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, getName());
    }

    static
    {
        NBTHelper.registerAdapter(Hero.class, new INBTSaveAdapter<Hero>()
        {
            @Override
            public Hero readFromNBT(NBTBase tag)
            {
                if (tag instanceof NBTTagString)
                {
                    return getHeroFromName(((NBTTagString) tag).func_150285_a_());
                }

                return null;
            }

            @Override
            public Hero fromBytes(ByteBuf buf)
            {
                return getHeroFromName(ByteBufUtils.readUTF8String(buf));
            }
        });
    }

    public enum Property
    {
        MASK_TOGGLE(0, 0, SHData.MASK_OPEN),
        BREATHE_UNDERWATER(1, 0),
        BREATHE_SPACE(2, 0);

        public final SHData<Boolean> dataHook;
        public final int iconIndex;

        Property(int iconX, int iconY, SHData<Boolean> data)
        {
            iconIndex = iconX + iconY * 5;
            dataHook = data;
        }

        Property(int iconX, int iconY)
        {
            this(iconX, iconY, null);
        }
    }

    public enum Toggle
    {
        TRANSFORM(0, 0, SHData.TRANSFORMED, Ability.KEY_TRANSFORM, (e, h) -> h.getFuncInt(e, Ability.FUNC_TRANSFORM_TICKS, -1)),
        STEEL(1, 0, SHData.STEELED, AbilitySteelTransformation.KEY_STEEL_TRANSFORM, Rule.TICKS_STEELTIMER::get),
        SHIELD(2, 0, SHData.SHIELD, AbilityShield.KEY_SHIELD),
        BLADE(3, 0, SHData.BLADE, AbilityBlade.KEY_BLADE);

        public final BiFunction<EntityLivingBase, Hero, Integer> cooldown;
        public final SHData<Boolean> dataHook;
        public final String keybind;
        public final int iconIndex;

        Toggle(int iconX, int iconY, SHData<Boolean> data, String key, BiFunction<EntityLivingBase, Hero, Integer> func)
        {
            iconIndex = iconX + iconY * 5 + 10;
            dataHook = data;
            cooldown = func;
            keybind = key;
        }

        Toggle(int iconX, int iconY, SHData<Boolean> data, String key)
        {
            this(iconX, iconY, data, key, (e, h) -> -1);
        }
    }

    public interface Permission
    {
        String THROW_SHIELD = "THROW_SHIELD";
        String USE_SHIELD = "USE_SHIELD";
        String USE_COLD_GUN = "USE_COLD_GUN";
        String USE_HEAT_GUN = "USE_HEAT_GUN";
        String USE_RIPS_GUN = "USE_RIPS_GUN";
        String USE_CHRONOS_RIFLE = "USE_CHRONOS_RIFLE";
        String USE_GRAPPLING_GUN = "USE_GRAPPLING_GUN";
    }
}
