package com.fiskmods.heroes.pack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.move.MoveSet;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class HeroPack
{
    private static final FilenameFilter ZIP_FILTER = (f, s) -> f.isFile() && (s.endsWith(".zip") || s.endsWith(".jar"));
    static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static final Pattern PATTERN_HERO = Pattern.compile("^assets\\/([a-z_0-9]+)\\/data\\/heroes\\/([a-z_0-9]+)\\.js$");
    private static final Pattern PATTERN_MOVESET = Pattern.compile("^assets\\/([a-z_0-9]+)\\/data\\/movesets\\/([a-z_0-9]+)\\.json$");
    private static final Pattern PATTERN_LENIENT = Pattern.compile("^assets\\/(.+)\\/data\\/(heroes\\/(.+)\\.js|movesets\\/(.+)\\.json)$");

    private final File fileLocation;
    private String name;

    private Map<ResourceLocation, Hero> heroes;
    private Map<ResourceLocation, MoveSet> moveSets;

    private Map alts;
    private Map remaps;

    public HeroPack(File file)
    {
        fileLocation = file;
    }

    public String getName()
    {
        return name;
    }

    public Map<ResourceLocation, Hero> getHeroes()
    {
        return heroes;
    }

    public Map<ResourceLocation, MoveSet> getMoveSets()
    {
        return moveSets;
    }

    public Map getAlts()
    {
        return alts;
    }

    public Map getRemaps()
    {
        return remaps;
    }

    public void reload(AbstractHeroPackSerializer serializer) throws Exception
    {
        if (new File(JSHeroesEngine.PACK_PROPERTIES).equals(fileLocation))
        {
            return;
        }

        try
        {
            heroes = new HashMap<>();
            moveSets = new HashMap<>();

            if (ZIP_FILTER.accept(fileLocation, fileLocation.getName()))
            {
                reloadFromFile(serializer);
            }
            else if (fileLocation.isDirectory() && new File(fileLocation, "heropack.json").exists())
            {
                reloadFromDirectory(serializer);
            }
            else
            {
                reset();
            }
        }
        catch (Exception e)
        {
            reset();
            throw e;
        }
    }

    private void reset()
    {
        name = null;
        heroes = null;
        moveSets = null;
        alts = null;
        remaps = null;
    }

    private void reloadFromFile(AbstractHeroPackSerializer serializer) throws Exception
    {
        ZipFile zip = null;

        try
        {
            zip = new ZipFile(fileLocation);
            ZipEntry pack = zip.getEntry("heropack.json");

            if (pack != null)
            {
                ZipFile zipf = zip; // Effectively final
                loadProperties(new InputStreamReader(zip.getInputStream(pack)), serializer);

                for (ZipEntry zipentry : Collections.list(zip.entries()))
                {
                    String s = zipentry.getName();
                    Matcher m;

                    if ((m = PATTERN_HERO.matcher(s)).matches())
                    {
                        ResourceLocation id = new ResourceLocation(m.group(1), m.group(2));

                        try
                        {
                            heroes.put(id, Preconditions.checkNotNull(JSHeroesEngine.INSTANCE.loadHero(id, () -> zipf.getInputStream(zipentry), serializer)));
                        }
                        catch (Exception e)
                        {
                            throw new HeroPackException("Loading Hero " + id + " failed!", e);
                        }
                    }
                    else if ((m = PATTERN_MOVESET.matcher(s)).matches())
                    {
                        // ResourceLocation id = new ResourceLocation(m.group(1), m.group(2));
                        //
                        // try
                        // {
                        // moveSets.put(id, Preconditions.checkNotNull(JSHeroesEngine.INSTANCE.loadMoveSet(zip.getInputStream(zipentry))));
                        // }
                        // catch (Exception e)
                        // {
                        // throw new HeroPackException("Loading MoveSet " + id + " failed!", e);
                        // } // TODO: 1.4 Combat
                    }
                    else if (s.startsWith("assets/"))
                    {
                        if ((m = PATTERN_LENIENT.matcher(s)).matches())
                        {
                            logNameNotLowercase(m.group(1));
                        }
                        else if (serializer != null && (m = IAssetSnooper.PATTERN.matcher(s)).matches())
                        {
                            serializer.accept(m.group(1), zip, zipentry);
                        }
                    }
                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(zip);
        }
    }

    private void reloadFromDirectory(AbstractHeroPackSerializer serializer) throws Exception
    {
        File dir = new File(fileLocation, "assets/");
        loadProperties(new FileReader(new File(fileLocation, "heropack.json")), serializer);

        if (dir.isDirectory())
        {
            for (File file : dir.listFiles(File::isDirectory))
            {
                String domain = file.getName();

                if (domain.matches("^[a-z_0-9]+$"))
                {
                    File heroDir = new File(file, "data/heroes/");
                    File moveSetDir = new File(file, "data/movesets/");

                    if (heroDir.isDirectory())
                    {
                        for (File file1 : heroDir.listFiles())
                        {
                            String name = file1.getName();

                            if (name.matches("^[a-z_0-9]+\\.js$"))
                            {
                                ResourceLocation id = new ResourceLocation(domain, name.substring(0, name.indexOf('.')));

                                try
                                {
                                    heroes.put(id, Preconditions.checkNotNull(JSHeroesEngine.INSTANCE.loadHero(id, () -> new FileInputStream(file1), serializer)));
                                }
                                catch (Exception e)
                                {
                                    throw new HeroPackException("Loading Hero " + id + " failed!", e);
                                }
                            }
                            else if (name.endsWith(".js"))
                            {
                                logNameNotLowercase(name);
                            }
                        }
                    }

                    // if (moveSetDir.isDirectory())
                    // {
                    // for (File file1 : moveSetDir.listFiles())
                    // {
                    // String name = file1.getName();
                    //
                    // if (name.matches("^[a-z_0-9]+\\.json$"))
                    // {
                    // ResourceLocation id = new ResourceLocation(domain, name.substring(0, name.indexOf('.')));
                    //
                    // try
                    // {
                    // moveSets.put(id, Preconditions.checkNotNull(JSHeroesEngine.INSTANCE.loadMoveSet(new FileInputStream(file1))));
                    // }
                    // catch (Exception e)
                    // {
                    // throw new HeroPackException("Loading MoveSet " + id + " failed!", e);
                    // }
                    // }
                    // else if (name.endsWith(".json"))
                    // {
                    // logNameNotLowercase(name);
                    // }
                    // }
                    // } // TODO: 1.4 Combat

                    if (serializer != null)
                    {
                        serializer.accept(domain, file);
                    }
                }
                else
                {
                    logNameNotLowercase(domain);
                }
            }
        }
    }

    private void loadProperties(Reader reader, AbstractHeroPackSerializer serializer) throws Exception
    {
        try
        {
            Map properties = GSON.fromJson(reader, Map.class);
            name = String.valueOf(properties.get("name"));

            if (StringUtils.isNullOrEmpty(name))
            {
                throw new IllegalArgumentException("heropack.json file is missing field 'name'!");
            }

            Object obj = properties.get("alts");

            if (obj instanceof Map)
            {
                alts = (Map) obj;
            }

            obj = properties.get("remap");

            if (obj instanceof Map)
            {
                remaps = (Map) obj;
            }

            if (serializer != null)
            {
                serializer.putData(AbstractHeroPackSerializer.PACK, fileLocation.getName(), GSON.toJson(properties));
            }
        }
        catch (Exception e)
        {
            throw new HeroPackException("Reading heropack.json failed!", e);
        }
    }

    private void logNameNotLowercase(String s)
    {
        JSHeroesEngine.LOGGER.warn("HeroPack: ignored non-lowercase registration: {} in {}", s, fileLocation);
    }

    public static class HeroPackException extends Exception
    {
        public HeroPackException(String message, Exception e)
        {
            super(message, e);
        }

        public HeroPackException(String message)
        {
            super(message);
        }
    }
}
