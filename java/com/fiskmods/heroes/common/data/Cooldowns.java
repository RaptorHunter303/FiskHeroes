package com.fiskmods.heroes.common.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiFunction;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData.DataFactory;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.NBTHelper;
import com.fiskmods.heroes.util.NBTHelper.INBTSaveAdapter;
import com.fiskmods.heroes.util.NBTHelper.INBTSavedObject;
import com.fiskmods.heroes.util.SHHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class Cooldowns implements INBTSavedObject<Cooldowns>
{
    private Map<Cooldown, Integer> values = new HashMap<>();

    public void tick()
    {
        for (Map.Entry<Cooldown, Integer> e : new HashSet<>(values.entrySet()))
        {
            int value = e.getValue();

            if (--value <= 0)
            {
                values.remove(e.getKey());
                continue;
            }

            e.setValue(value);
        }
    }

    public static Cooldowns getInstance(Entity entity)
    {
        Cooldowns cooldowns = SHData.ABILITY_COOLDOWNS.get(entity);

        if (cooldowns == null)
        {
            SHData.ABILITY_COOLDOWNS.setWithoutNotify(entity, cooldowns = SHData.ABILITY_COOLDOWNS.getDefault());
        }

        return cooldowns;
    }

    public static DataFactory<Cooldowns> factory()
    {
        return DataFactory.create(Cooldowns::new, false);
    }

    @Override
    public NBTBase writeToNBT()
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (Map.Entry<Cooldown, Integer> e : values.entrySet())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("K", e.getKey().name());
            tag.setInteger("V", e.getValue());
            nbttaglist.appendTag(tag);
        }

        return nbttaglist;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(values.size());

        for (Map.Entry<Cooldown, Integer> e : values.entrySet())
        {
            buf.writeByte(e.getKey().ordinal());
            buf.writeInt(e.getValue());
        }
    }

    static
    {
        NBTHelper.registerAdapter(Cooldowns.class, new INBTSaveAdapter<Cooldowns>()
        {
            @Override
            public Cooldowns readFromNBT(NBTBase tag)
            {
                if (tag instanceof NBTTagList)
                {
                    NBTTagList nbttaglist = (NBTTagList) tag;

                    if (nbttaglist.func_150303_d() == NBT.TAG_COMPOUND)
                    {
                        Cooldowns cooldowns = new Cooldowns();

                        for (int i = 0; i < nbttaglist.tagCount(); ++i)
                        {
                            NBTTagCompound nbt = nbttaglist.getCompoundTagAt(i);
                            Cooldown key = Cooldown.find(nbt.getString("K"));

                            if (key != null)
                            {
                                cooldowns.values.put(key, nbt.getInteger("V"));
                            }
                        }

                        return cooldowns;
                    }
                }

                return null;
            }

            @Override
            public Cooldowns fromBytes(ByteBuf buf)
            {
                Cooldowns cooldowns = new Cooldowns();
                int size = buf.readInt();

                for (int i = 0; i < size; ++i)
                {
                    int j = buf.readByte();

                    if (j >= 0 & j < Cooldown.values().length)
                    {
                        cooldowns.values.put(Cooldown.values()[j], buf.readInt());
                    }
                }

                return cooldowns;
            }
        });
    }

    public enum Cooldown
    {
        SHIELD_THROW(Rule.COOLDOWN_SHIELDTHROW),
        EARTHQUAKE(Rule.COOLDOWN_EARTHQUAKE),
        GROUND_SMASH(Rule.COOLDOWN_GROUNDSMASH),
        SUMMON_CACTUS(Rule.COOLDOWN_SUMMONCACTUS),
        FIREBALL(Rule.COOLDOWN_FIREBALL),
        ICICLES(Rule.COOLDOWN_ICICLES),
        REPULSOR(Rule.COOLDOWN_REPULSOR),
        SPIKE_BURST(Rule.COOLDOWN_SPIKEBURST),
        SPIKE_RING(Rule.COOLDOWN_SPIKERING),
        LIGHTNING(Rule.COOLDOWN_LIGHTNINGCAST),
        ENERGY_BLAST(Rule.COOLDOWN_ENERGYBLAST),
        SPELL_ATMOSPHERIC(Rule.COOLDOWN_SPELL_ATMOSPHERIC),
        SPELL_DUPLICATION(Rule.COOLDOWN_SPELL_DUPLICATION),
        SPELL_EARTHSWALLOW(Rule.COOLDOWN_SPELL_EARTHSWALLOW),
        SPELL_WHIP(Rule.COOLDOWN_SPELL_WHIP);

        private final BiFunction<EntityLivingBase, Hero, Integer> maxValue;

        private Cooldown(BiFunction<EntityLivingBase, Hero, Integer> func)
        {
            maxValue = func;
        }

        private Cooldown(Rule<Integer> rule)
        {
            this(rule::get);
        }

        private static Cooldown find(String name)
        {
            for (Cooldown cooldown : values())
            {
                if (cooldown.name().equals(name))
                {
                    return cooldown;
                }
            }

            return null;
        }

        public int get(EntityLivingBase entity)
        {
            return getInstance(entity).values.getOrDefault(this, 0);
        }

        public int getMax(EntityLivingBase entity, Hero hero)
        {
            return maxValue.apply(entity, hero);
        }

        public boolean available(EntityLivingBase entity)
        {
            return get(entity) == 0;
        }

        public void set(EntityLivingBase entity, Hero hero)
        {
            getInstance(entity).values.put(this, getMax(entity, hero));
        }

        public void set(EntityLivingBase entity)
        {
            set(entity, SHHelper.getHero(entity));
        }

        public double getProgress(EntityLivingBase entity, Hero hero, double max)
        {
            return max * get(entity) / getMax(entity, hero);
        }

        public double getProgress(EntityLivingBase entity, double max)
        {
            return getProgress(entity, SHHelper.getHero(entity), max);
        }

        public float getProgress(EntityLivingBase entity, Hero hero, float max)
        {
            return max * get(entity) / getMax(entity, hero);
        }

        public float getProgress(EntityLivingBase entity, float max)
        {
            return getProgress(entity, SHHelper.getHero(entity), max);
        }
    }
}
