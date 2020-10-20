package com.fiskmods.heroes.common.hero;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.json.hero.SlotType;
import com.fiskmods.heroes.common.BackupList;
import com.fiskmods.heroes.common.BackupMap;
import com.fiskmods.heroes.common.hero.HeroIteration.Candidate;
import com.fiskmods.heroes.common.move.MoveSet;
import com.fiskmods.heroes.pack.AbstractHeroPackSerializer;
import com.fiskmods.heroes.pack.HeroPack;
import com.fiskmods.heroes.pack.HeroPack.HeroPackException;
import com.fiskmods.heroes.pack.JSHeroesEngine;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class HeroRegistry extends RegistrySimple implements Iterable<Hero>
{
    private final Map<String, String> listeners = new HashMap<>();
    private final BackupMap<String, HeroIteration> remaps = new BackupMap<>();

    private final HeroIntMap underlyingIntegerMap = new HeroIntMap();
    private int nextId = -1;

    @Override
    protected Map createUnderlyingMap()
    {
        return new BackupMap<>();
    }

    @Override
    @Deprecated
    public void putObject(Object key, Object value)
    {
        JSHeroesEngine.LOGGER.error("Runtime Hero registration not allowed: ignoring key {}, use a {} instead", key, HeroSupplier.class);
    }

    public int getIDForObject(Hero value)
    {
        return underlyingIntegerMap.getIDForHero(value);
    }

    public String getNameForObject(Hero value)
    {
        return value != null ? value.getName() : null;
    }

    @Override
    public Hero getObject(Object key)
    {
        return getObject((String) key);
    }

    public Hero getObjectById(int id)
    {
        return underlyingIntegerMap.getHeroById(id);
    }

    public Hero getObject(String key)
    {
        return (Hero) super.getObject(namespace(key));
    }

    public boolean containsId(int id)
    {
        return underlyingIntegerMap.containsId(id);
    }

    public boolean containsKey(String key)
    {
        return super.containsKey(namespace(key));
    }

    @Override
    public boolean containsKey(Object key)
    {
        return containsKey((String) key);
    }

    public boolean containsValue(Hero value)
    {
        return registryObjects.values().contains(value);
    }

    @Override
    public Set<String> getKeys()
    {
        return super.getKeys();
    }

    public ImmutableSet<Hero> getValues()
    {
        return ImmutableSet.copyOf(registryObjects.values());
    }

    public Set<String> getKeys(Predicate<Hero> p)
    {
        return ((Map<String, Hero>) registryObjects).entrySet().stream().filter(e -> p.test(e.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    @Override
    public Iterator<Hero> iterator()
    {
        return underlyingIntegerMap.iterator();
    }

    public String namespace(String key)
    {
        return key != null && key.indexOf(':') == -1 ? FiskHeroes.MODID + ":" + key : key;
    }

    public String getRemap(String key)
    {
        HeroIteration iter = remaps.get(namespace(key));
        return iter != null ? iter.getName() : key;
    }

    public int reload(File packs, boolean sensitive, AbstractHeroPackSerializer serializer) throws HeroPackException
    {
        JSHeroesEngine.LOGGER.info("Reloading HeroPacks...");

        try
        {
            Map<ResourceLocation, Map<String, Candidate>> iterations = new HashMap<>();
            Map<ResourceLocation, Hero> heroes = new LinkedHashMap<>();
            Map<ResourceLocation, MoveSet> moveSets = new HashMap<>();
            Set<ResourceLocation> nonnull = new HashSet<>();
            Map remaps = new HashMap<>();

            reset();
            int packCount = reloadNatives(iterations, remaps, heroes, nonnull);

            if (packs.isDirectory() || packs.mkdirs())
            {
                File[] files = packs.listFiles((f, s) -> (f.isDirectory() || f.isFile() && s.endsWith(".zip")) && !s.equals(".bin") && !s.equals("heropacks.properties"));

                if (files != null)
                {
                    for (File file : files)
                    {
                        HeroPack pack = new HeroPack(file);

                        try
                        {
                            pack.reload(serializer);

                            if (pack.getHeroes() != null || pack.getMoveSets() != null || pack.getAlts() != null)
                            {
                                if (pack.getHeroes() != null)
                                {
                                    pack.getHeroes().forEach((k, v) ->
                                    {
                                        if (!nonnull.contains(k))
                                        {
                                            heroes.put(k, v);
                                        }
                                    });
                                }

                                if (pack.getMoveSets() != null)
                                {
                                    moveSets.putAll(pack.getMoveSets());
                                }

                                if (pack.getRemaps() != null)
                                {
                                    remaps.putAll(pack.getRemaps());
                                }

                                processIterations(pack.getAlts(), iterations);
                                JSHeroesEngine.LOGGER.info("Loaded HeroPack {} ({})", pack.getName(), file.getName());
                                ++packCount;
                            }
                        }
                        catch (Exception e)
                        {
                            if (sensitive)
                            {
                                throw new HeroPackException(String.format("HeroPack %s failed to read properly", file.getName()), e);
                            }

                            JSHeroesEngine.LOGGER.warn(String.format("HeroPack %s failed to read properly, it will be ignored", file.getName()), e);
                        }
                    }
                }
            }
            else
            {
                throw new HeroPackException("Failed to create HeroPack directory");
            }

//            for (Map.Entry<ResourceLocation, MoveSet> e : moveSets.entrySet())
//            {
//                Hero hero = heroes.get(e.getKey());
//
//                if (hero != null)
//                {
//                    hero.setMoveSet(e.getValue());
//                }
//            } // TODO: 1.4 Combat

            heroes.forEach((k, v) ->
            {
                v.register(k, iterations.get(k));
                registryObjects.put(k.toString(), v);
                underlyingIntegerMap.put(v, ++nextId);
            });

            injectRemaps(remaps);
            JSHeroesEngine.LOGGER.info("Successfully loaded {} HeroPack(s)! ({} heroes)", packCount, registryObjects.size());
            MinecraftForge.EVENT_BUS.post(new RegisterHeroesEvent(this));

            return packCount;
        }
        catch (Exception e)
        {
            restore();
            JSHeroesEngine.LOGGER.error("Loading HeroPacks FAILED!");
            throw e;
        }
    }

    public int reloadFrom(AbstractHeroPackSerializer serializer) throws HeroPackException
    {
        if (serializer == null)
        {
            throw new HeroPackException("serializer must not be null!", new NullPointerException());
        }

        JSHeroesEngine.LOGGER.info("Reloading HeroPacks...");

        try
        {
            Map<ResourceLocation, Map<String, Candidate>> iterations = new HashMap<>();
            Map<ResourceLocation, Hero> heroes = new LinkedHashMap<>();
            Set<ResourceLocation> nonnull = new HashSet<>();
            Map remaps = new HashMap<>();
            reset();

            int packCount = reloadNatives(iterations, remaps, heroes, nonnull);
            packCount += serializer.reload(iterations, remaps, heroes, nonnull);
            heroes.forEach((k, v) ->
            {
                v.register(k, iterations.get(k));
                registryObjects.put(k.toString(), v);
                underlyingIntegerMap.put(v, ++nextId);
            });

            injectRemaps(remaps);
            JSHeroesEngine.LOGGER.info("Successfully loaded {} HeroPack(s)! ({} heroes)", packCount, registryObjects.size());

            return packCount;
        }
        catch (Exception e)
        {
            restore();
            JSHeroesEngine.LOGGER.error("Loading HeroPacks FAILED!");
            throw e;
        }
    }

    protected int reloadNatives(Map iterations, Map remaps, Map heroes, Set nonnull) throws HeroPackException
    {
        String domain = null;
        int packCount = 0;

        try
        {
            for (Map.Entry<String, String> e : listeners.entrySet())
            {
                domain = e.getKey();
                ModContainer container = Loader.instance().getIndexedModList().get(domain);

                if (container != null)
                {
                    HeroPack pack = new HeroPack(container.getSource());
                    pack.reload(null);

                    if (pack.getHeroes() == null)
                    {
                        throw new IllegalStateException("HeroSuppliers must specify at least one hero!");
                    }

                    Class c = Class.forName(e.getValue());

                    for (Field field : c.getFields())
                    {
                        if (field.getType() == Hero.class)
                        {
                            ResourceLocation key = new ResourceLocation(domain, field.getName());
                            Hero hero;

                            if (field.isAnnotationPresent(Immutable.class))
                            {
                                if ((hero = (Hero) field.get(null)) != null)
                                {
                                    nonnull.add(key);
                                    heroes.put(key, hero);
                                }
                            }
                            else if ((hero = pack.getHeroes().get(key)) != null)
                            {
                                field.set(null, hero);
                                heroes.put(key, hero);
                            }
                        }
                    }

//                    if (pack.getMoveSets() != null)
//                    {
//                        moveSets.putAll(pack.getMoveSets());
//                    } TODO: 1.4 Combat

                    if (pack.getRemaps() != null)
                    {
                        remaps.putAll(pack.getRemaps());
                    }

                    processIterations(pack.getAlts(), iterations);
                    ++packCount;
                }
            }

            return packCount;
        }
        catch (Exception e)
        {
            throw new HeroPackException(String.format("Failed to register native heroes for domain %s", domain), e);
        }
    }

    public void processIterations(Map<?, ?> map, Map<ResourceLocation, Map<String, Candidate>> iterations)
    {
        if (map != null)
        {
            for (Map.Entry e : map.entrySet())
            {
                if (e.getValue() instanceof Map)
                {
                    ResourceLocation id = new ResourceLocation(String.valueOf(e.getKey()));
                    Map<?, ?> alts = (Map) e.getValue();

                    for (Map.Entry alt : alts.entrySet())
                    {
                        processIteration(id, alt, iterations);
                    }
                }
            }
        }
    }

    private void processIteration(ResourceLocation id, Map.Entry alt, Map<ResourceLocation, Map<String, Candidate>> iterations)
    {
        String key = String.valueOf(alt.getKey());
        String name;

        Object value = alt.getValue();
        Candidate candidate = null;

        if (value instanceof Map)
        {
            Map map = (Map) value;

            if (map.containsKey("name") && !(name = String.valueOf(map.get("name"))).isEmpty())
            {
                candidate = new Candidate(name);

                if (map.get("armor") instanceof Map)
                {
                    map = (Map) map.get("armor");

                    for (SlotType slot : SlotType.values())
                    {
                        Object obj = map.get(slot.name());

                        if (obj != null)
                        {
                            candidate.armor[slot.ordinal()] = String.valueOf(obj);
                        }
                    }
                }
            }
        }
        else if (!key.isEmpty() && !(name = String.valueOf(value)).isEmpty())
        {
            candidate = new Candidate(name);
        }

        if (candidate != null)
        {
            iterations.computeIfAbsent(id, k -> new TreeMap<>()).put(key, candidate);
        }
    }

    private void injectRemaps(Map<?, ?> map)
    {
        for (Map.Entry e : map.entrySet())
        {
            String key = String.valueOf(e.getKey());
            HeroIteration value;

            if (!key.isEmpty() && (value = HeroIteration.lookup(String.valueOf(e.getValue()))) != null)
            {
                remaps.put(key, value);
                JSHeroesEngine.LOGGER.info("Injecting Hero remap: {} -> {}", key, value.getName());
            }
        }
    }

    private Integer backupNextId;

    private void reset()
    {
        Set<ItemHeroArmor> items = new HashSet<>();

        for (Hero hero : this)
        {
            for (int i = 0; i < 4; ++i)
            {
                items.add(hero.getItem(i));
            }
        }

        for (ItemHeroArmor item : items)
        {
            if (item != null)
            {
                item.registry.reset();
            }
        }

        ((BackupMap) registryObjects).reset();
        HeroIteration.REGISTRY.reset();
        HeroIteration.ID_REGISTRY.reset();
        underlyingIntegerMap.reset();
        backupNextId = nextId;
        nextId = -1;

        remaps.reset();
    }

    private boolean restore()
    {
        if (backupNextId != null)
        {
            Set<ItemHeroArmor> items = new HashSet<>();
            boolean flag = true;

            flag &= ((BackupMap) registryObjects).restore();
            flag &= HeroIteration.REGISTRY.restore();
            flag &= HeroIteration.ID_REGISTRY.restore();
            flag &= underlyingIntegerMap.restore();
            flag &= remaps.restore();
            nextId = backupNextId;

            for (Hero hero : this)
            {
                for (int i = 0; i < 4; ++i)
                {
                    items.add(hero.getItem(i));
                }
            }

            for (ItemHeroArmor item : items)
            {
                if (item != null)
                {
                    flag &= item.registry.restore();
                }
            }

            return flag;
        }

        return false;
    }

    public void construct(FMLConstructionEvent event)
    {
        Set<ASMData> dataSet = event.getASMHarvestedData().getAll(HeroSupplier.class.getCanonicalName());

        for (ASMData data : dataSet)
        {
            String modid = (String) data.getAnnotationInfo().get("value");

            if (modid != null)
            {
                try
                {
                    listeners.put(modid, data.getClassName());
                }
                catch (Exception e)
                {
                    throw new RuntimeException(String.format("Failed to register HeroSupplier %s:", data.getClassName()), e);
                }
            }
        }
    }

    public static class HeroIntMap implements Iterable<Hero>
    {
        protected BackupMap<Hero, Integer> map = new BackupMap<>(64);
        protected BackupList<Hero> list = new BackupList<>();

        private void reset()
        {
            map.reset();
            list.reset();
        }

        private boolean restore()
        {
            boolean flag = true;
            flag &= map.restore();
            flag &= list.restore();

            return flag;
        }

        public void put(Hero hero, int id)
        {
            map.put(hero, id);

            while (list.size() <= id)
            {
                list.add(null);
            }

            list.set(id, hero);
        }

        public int getIDForHero(Hero hero)
        {
            Integer id = map.get(hero);
            return id == null ? -1 : id.intValue();
        }

        public Hero getHeroById(int id)
        {
            return id >= 0 && id < list.size() ? list.get(id) : null;
        }

        @Override
        public Iterator<Hero> iterator()
        {
            return Iterators.filter(list.iterator(), Predicates.notNull());
        }

        public boolean containsId(int id)
        {
            return getHeroById(id) != null;
        }
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Immutable
    {
    }
}
