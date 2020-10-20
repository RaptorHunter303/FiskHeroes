package com.fiskmods.heroes.pack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.entity.attribute.ArmorAttribute;
import com.fiskmods.heroes.common.entity.attribute.IAttributeContainer;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Tier;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.HeroModifier;
import com.fiskmods.heroes.common.hero.modifier.Weakness;
import com.fiskmods.heroes.pack.accessor.JSEntityAccessor;
import com.fiskmods.heroes.pack.accessor.JSRuleAccessor;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.NBTHelper;
import com.google.common.base.Preconditions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;

public class JSHero
{
    public final ResourceLocation id;

    private String name;
    private String version;
    private Tier tier = Tier.T1;

    private boolean cosmic;
    private boolean hidden;

    private final String[] armor = new String[4];
    private final List<Equipment> equipment = new ArrayList<>();
    private final List<String> abilities = new ArrayList<>();
    private final List<String> weaknesses = new ArrayList<>();

    private final AttributeProfile defaultAttributes = new AttributeProfile();
    private final Map<String, AttributeProfile> attributes = new HashMap<>();
    private final Map<String, Integer> keyBindings = new HashMap<>();
    private final Map<String, String> keyBindNames = new HashMap<>();

    private BiFunction<JSEntityAccessor, String, Boolean> hasProperty;
    private BiFunction<JSEntityAccessor, String, Boolean> hasPermission;
    private BiFunction<JSEntityAccessor, String, Boolean> isModifierEnabled;
    private BiFunction<JSEntityAccessor, String, Boolean> isKeyBindEnabled;
    private BiFunction<JSEntityAccessor, JSRuleAccessor, Double> ruleValueModifier;
    private BiFunction<JSEntityAccessor, Boolean, Void> onToggleMask;
    private Function<JSEntityAccessor, String> attributeProfile;
    private Function<JSEntityAccessor, Double> defaultScale;
    private Function<JSEntityAccessor, Integer> tierOverride;

    private Map<String, Function<JSEntityAccessor, Object>> functions = new HashMap<>();

    public JSHero(ResourceLocation id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public void setTier(int tier)
    {
        this.tier = parseTier(tier);
    }

    private Tier parseTier(int tier)
    {
        if (tier < 1 || tier > Tier.MAX)
        {
            throw new IllegalArgumentException(String.format("tier (%s) must not be lesser than 1 or greater than %s", tier, Tier.MAX));
        }

        return Tier.get(tier);
    }

    public void setCosmic()
    {
        cosmic = true;
    }

    public void hide()
    {
        hidden = true;
    }

    public void setHelmet(String name)
    {
        setArmor(name, 0);
    }

    public void setChestplate(String name)
    {
        setArmor(name, 1);
    }

    public void setLeggings(String name)
    {
        setArmor(name, 2);
    }

    public void setBoots(String name)
    {
        setArmor(name, 3);
    }

    private void setArmor(String name, int armorType)
    {
        if (StringUtils.isNullOrEmpty(name))
        {
            throw new IllegalArgumentException("name cannot be left blank");
        }

        armor[armorType] = name;
    }

    public void addEquipment(String id, int metadata, String nbt)
    {
        equipment.add(new Equipment(id, metadata, nbt));
    }

    public void addEquipment(String id, int metadata)
    {
        addEquipment(id, metadata, null);
    }

    public void addEquipment(String id, String nbt)
    {
        addEquipment(id, 0, nbt);
    }

    public void addEquipment(String id)
    {
        addEquipment(id, 0);
    }

    public void addAbilities(String... names)
    {
        abilities.addAll(Arrays.asList(names));
    }

    public void addWeaknesses(String... names)
    {
        weaknesses.addAll(Arrays.asList(names));
    }

    public void addAttribute(String key, double value, int operator)
    {
        defaultAttributes.addAttribute(key, value, operator);
    }

    public void addKeyBind(String id, String name, int key)
    {
        keyBindings.put(id, key);
        keyBindNames.put(id, name);
    }

    public void addAttributeProfile(String key, Function<AttributeProfile, Void> func)
    {
        AttributeProfile profile = new AttributeProfile();
        Preconditions.checkNotNull(func).apply(profile);

        attributes.put(Preconditions.checkNotNull(key), profile);
    }

    public void setAttributeProfile(Function<JSEntityAccessor, String> func)
    {
        attributeProfile = func;
    }

    public void setHasProperty(BiFunction<JSEntityAccessor, String, Boolean> func)
    {
        hasProperty = func;
    }

    public void setHasPermission(BiFunction<JSEntityAccessor, String, Boolean> func)
    {
        hasPermission = func;
    }

    public void setModifierEnabled(BiFunction<JSEntityAccessor, String, Boolean> func)
    {
        isModifierEnabled = func;
    }

    public void setKeyBindEnabled(BiFunction<JSEntityAccessor, String, Boolean> func)
    {
        isKeyBindEnabled = func;
    }

    public void setOnToggleMask(BiFunction<JSEntityAccessor, Boolean, Void> func)
    {
        onToggleMask = func;
    }

    public void setDefaultScale(Function<JSEntityAccessor, Double> func)
    {
        defaultScale = func;
    }

    public void setDefaultScale(double scale)
    {
        setDefaultScale(entity -> scale);
    }

    public void setTierOverride(Function<JSEntityAccessor, Integer> func)
    {
        tierOverride = func;
    }

    public void setRuleValueModifier(BiFunction<JSEntityAccessor, JSRuleAccessor, Double> func)
    {
        ruleValueModifier = func;
    }

    public void supplyFunction(String key, Function<JSEntityAccessor, Object> func)
    {
        functions.put(key, func);
    }

    public void supplyFunction(String key, Object obj)
    {
        supplyFunction(key, entity -> obj);
    }

    @Override
    public String toString()
    {
        return String.format("JSHero[\"%s\", name=%s, version=%s, tier=%s, armor=%s, equipment=%s, abilities=%s, weaknesses=%s, keyBindings=%s, keyBindNames=%s, functions=%s]", id, name, version, tier, Arrays.toString(armor), equipment, abilities, weaknesses, keyBindings, keyBindNames, functions.keySet());
    }

    public static class Attrib
    {
        public final double value;
        public final int operator;

        private String key;
        private ArmorAttribute cache;

        public Attrib(String key, double value, int operator)
        {
            this.key = key;
            this.value = value;
            this.operator = operator;
        }

        public ArmorAttribute getAttribute()
        {
            if (cache == null)
            {
                cache = Preconditions.checkNotNull(SHAttributes.getAttribute(key));
            }

            return cache;
        }
    }

    public static class AttributeProfile
    {
        final List<Attrib> attributes = new ArrayList<>();

        public void addAttribute(String key, double value, int operator)
        {
            attributes.add(new Attrib(key, value, operator));
        }
    }

    private static class Equipment
    {
        private final String id;
        private final int metadata;

        private final NBTTagCompound nbt;

        public Equipment(String id, int metadata, String nbt)
        {
            this.id = id;
            this.metadata = metadata;
            this.nbt = nbt != null ? NBTHelper.fromJson(nbt) : null;
        }

        public ItemStack eval()
        {
            Object item = Item.itemRegistry.getObject(id);

            if (item instanceof Item)
            {
                ItemStack stack = new ItemStack((Item) item, 1, metadata);

                if (nbt != null && !nbt.hasNoTags())
                {
                    stack.setTagCompound(nbt);
                }

                return stack;
            }

            throw new NullPointerException(String.format("%s == null", this));
        }

        @Override
        public String toString()
        {
            return id + "@" + metadata + (nbt != null ? " {...}" : "");
        }
    }

    class HeroJS extends Hero
    {
        public HeroJS()
        {
            if (FiskServerUtils.nonNull(armor) == null)
            {
                throw new IllegalArgumentException("Hero must specify at least one armor piece!");
            }
        }

        @Override
        public void init()
        {
            for (int i = 0; i < armor.length; ++i)
            {
                if (armor[i] != null)
                {
                    setArmor(i, armor[i]);
                }
            }
        }

        @Override
        public String getLocalizedName()
        {
            return name != null ? StatCollector.translateToLocal(name).trim().replace("\\u00f1", "\u00f1") : super.getLocalizedName();
        }

        @Override
        public String getVersion()
        {
            return version;
        }

        @Override
        public Tier getTier()
        {
            return tier;
        }

        @Override
        public int getKeyBinding(String name)
        {
            return keyBindings.getOrDefault(name, 0);
        }

        @Override
        public boolean hasKeyBinding(int keycode)
        {
            return keyBindings.containsValue(keycode);
        }

        @Override
        public Set<String> getKeyBindsMatching(int keycode)
        {
            return keyBindings.entrySet().stream().filter(t -> t.getValue() == keycode).map(Map.Entry::getKey).collect(Collectors.toSet());
        }

        @Override
        public void translateKeys(Map<String, String> mappings)
        {
            mappings.putAll(keyBindNames);
        }

        @Override
        public boolean hasProperty(EntityLivingBase entity, Property property)
        {
            return hasProperty != null && hasProperty.apply(JSEntityAccessor.wrap(entity), property.name());
        }

        @Override
        public boolean hasPermission(EntityLivingBase entity, String permission)
        {
            return hasPermission != null && hasPermission.apply(JSEntityAccessor.wrap(entity), permission);
        }

        @Override
        public boolean isModifierEnabled(EntityLivingBase entity, HeroModifier modifier)
        {
            return isModifierEnabled == null || isModifierEnabled.apply(JSEntityAccessor.wrap(entity), modifier.getName());
        }

        @Override
        public boolean isKeyBindEnabled(EntityLivingBase entity, String keyBind)
        {
            return isKeyBindEnabled == null || isKeyBindEnabled.apply(JSEntityAccessor.wrap(entity), keyBind);
        }

        @Override
        public void onToggleMask(EntityLivingBase entity, boolean state)
        {
            if (onToggleMask != null)
            {
                onToggleMask.apply(JSEntityAccessor.wrap(entity), state);
            }
        }
        
        @Override
        public Number modifyRuleValue(EntityLivingBase entity, Rule<? extends Number> rule)
        {
            if (ruleValueModifier != null)
            {
                Number n = ruleValueModifier.apply(JSEntityAccessor.wrap(entity), JSRuleAccessor.wrap(rule));
                
                if (n != null)
                {
                    return n;
                }
            }
            
            return rule.defaultValue;
        }

        @Override
        public float getDefaultScale(EntityPlayer player)
        {
            return (float) (defaultScale != null ? defaultScale.apply(JSEntityAccessor.wrap(player)) : 1);
        }

        @Override
        public Object getFuncObject(EntityLivingBase entity, String key)
        {
            return functions.containsKey(key) ? functions.get(key).apply(JSEntityAccessor.wrap(entity)) : null;
        }

        @Override
        public boolean isCosmic()
        {
            return cosmic;
        }

        @Override
        public boolean isHidden()
        {
            return hidden;
        }

        @Override
        public List getEquipment()
        {
            return equipment.stream().map(Equipment::eval).collect(Collectors.toList());
        }

        @Override
        protected void genModifiers(Consumer<HeroModifier> c)
        {
            for (String s : abilities)
            {
                Ability ability = Ability.getAbilityFromName(s);

                if (ability == null)
                {
                    JSHeroesEngine.LOGGER.error(String.format("Caught attempted registration of unknown Ability '%s' for %s, ignoring entry.", s, id));
                    continue;
                }

                c.accept(ability);
            }

            for (String s : weaknesses)
            {
                Weakness weakness = Weakness.getWeaknessFromName(s);

                if (weakness == null)
                {
                    JSHeroesEngine.LOGGER.error(String.format("Caught attempted registration of unknown Weakness '%s' for %s, ignoring entry.", s, id));
                    continue;
                }

                c.accept(weakness);
            }
        }

        @Override
        public void getAttributeModifiers(EntityLivingBase entity, IAttributeContainer c)
        {
            Tier tier = getTier();

            if (entity != null && tierOverride != null)
            {
                int i = tierOverride.apply(JSEntityAccessor.wrap(entity));
                tier = i == 0 ? null : parseTier(i);
            }

            if (tier != null)
            {
                c.add(SHAttributes.DAMAGE_REDUCTION, tier.getProtection(entity), 1);
            }

            AttributeProfile profile = defaultAttributes;

            if (entity != null && attributeProfile != null)
            {
                profile = attributes.getOrDefault(attributeProfile.apply(JSEntityAccessor.wrap(entity)), profile);
            }

            profile.attributes.forEach(c::add);
        }

        @Override
        public String toString()
        {
            return String.format("HeroJS[\"%s\", name=%s, version=%s, tier=%s, armor=%s, equipment=%s, abilities=%s, weaknesses=%s, keyBindings=%s, keyBindNames=%s, functions=%s]", id, name, version, tier, Arrays.toString(armor), equipment, abilities, weaknesses, keyBindings, keyBindNames, functions.keySet());
        }
    }
}
