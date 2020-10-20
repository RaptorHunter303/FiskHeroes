package com.fiskmods.heroes.common.entity.attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.fiskmods.heroes.common.hero.Hero;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public class ArmorAttribute extends BaseAttribute
{
    private final Map<UUID, Map<AttributePair, UUID>> globalUUIDs = new HashMap<>();
    private final boolean isAdditive;

    public ArmorAttribute(String unlocalizedName, double defaultValue, boolean additive)
    {
        super("armor." + unlocalizedName, defaultValue);
        isAdditive = additive;
    }

    public ArmorAttribute(String unlocalizedName, boolean additive)
    {
        this(unlocalizedName, 0, additive);
    }

    public void clean(EntityPlayer player, IAttributeInstance instance, List<UUID> validated)
    {
        Map<AttributePair, UUID> map = getGlobalUUIDs(player);
        Map<AttributePair, UUID> map1 = new HashMap<>(map);

        ImmutableList<Entry<AttributePair, UUID>> list = ImmutableList.copyOf(map.entrySet());

        for (Map.Entry<AttributePair, UUID> e : list)
        {
            UUID uuid = e.getValue();

            if (!validated.contains(uuid) && instance.getModifier(uuid) != null)
            {
                reset(player, instance, uuid);
                map1.remove(e.getKey());
            }
        }

        if (map1.size() < map.size())
        {
            map.clear();
            map.putAll(map1);
        }
    }

    public void reset(EntityPlayer player, IAttributeInstance instance, UUID uuid)
    {
        SHAttributes.removeModifier(instance, uuid);
    }

    public UUID createUUID(EntityPlayer player, AttributePair pair)
    {
        Map<AttributePair, UUID> map = getGlobalUUIDs(player);

        if (map.containsKey(pair))
        {
            return map.get(pair);
        }

        UUID uuid = UUID.randomUUID();
        map.put(pair, uuid);

        return uuid;
    }

    public UUID createUUID(EntityPlayer player, double amount, int operation)
    {
        return createUUID(player, new AttributePair(amount, operation));
    }

    private Map<AttributePair, UUID> getGlobalUUIDs(EntityPlayer player)
    {
        UUID uuid = player.getUniqueID();

        if (globalUUIDs.containsKey(uuid))
        {
            return globalUUIDs.get(uuid);
        }

        Map<AttributePair, UUID> map = new HashMap<>();
        globalUUIDs.put(uuid, map);

        return map;
    }

    public boolean isAdditive()
    {
        return isAdditive;
    }

    public double get(EntityLivingBase entity, double baseValue)
    {
        return SHAttributes.getModifier(entity, this, baseValue);
    }

    public float get(EntityLivingBase entity, float baseValue)
    {
        return SHAttributes.getModifier(entity, this, baseValue);
    }

    public double get(EntityLivingBase entity, Hero hero, double baseValue)
    {
        return SHAttributes.getModifier(entity, hero, this, baseValue);
    }

    public float get(EntityLivingBase entity, Hero hero, float baseValue)
    {
        return SHAttributes.getModifier(entity, hero, this, baseValue);
    }

    @Override
    public double clampValue(double value)
    {
        return value;
    }
}
