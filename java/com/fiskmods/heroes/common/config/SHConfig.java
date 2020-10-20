package com.fiskmods.heroes.common.config;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;

public class SHConfig
{
    public static final String CATEGORY_CLIENT = "Client";
    public static final String CATEGORY_GENERAL = "General";
    public static final String CATEGORY_IDS = "IDs";

    public static int heroRendererTimeout;
    public static boolean vibrantLightningColors;
    public static boolean hudAlignLeft;
    public static boolean hudAlignTop;
    public static boolean quiverHotbarAlignLeft;
    public static boolean useMiles;
    public static OldLightning oldLightning = OldLightning.NEVER;

    public static boolean addServer;

    public static int electroMagneticId;
    public static int capacityId;
    public static int infinityId;

    public static Configuration configFile;
    static SyncedConfig syncedConfig = new SyncedConfig();

    public static void register(File file)
    {
        Configuration config = new Configuration(file);
        config.load();
        load(config);

        if (config.hasChanged())
        {
            config.save();
        }
    }

    public static void load(Configuration config)
    {
        configFile = config;
        heroRendererTimeout = config.getInt("HeroRenderer Timeout", CATEGORY_CLIENT, 10000, 0, 60000, "Returns in milliseconds how long loading a HeroRenderer model is allowed to take before throwing an error.");
        vibrantLightningColors = config.getBoolean("Vibrant Lightning Colors", CATEGORY_CLIENT, true, "If enabled, speedster lightning will be rendered with more vibrant colors. May result in worse color blending in daylight.");
        hudAlignLeft = config.getBoolean("HUD Align Left", CATEGORY_CLIENT, false, "If enabled, the Heads Up Display will be aligned to the left of your screen.");
        hudAlignTop = config.getBoolean("HUD Align Top", CATEGORY_CLIENT, true, "If enabled, the Heads Up Display will be aligned to the top of your screen.");
        quiverHotbarAlignLeft = config.getBoolean("Quiver Hotbar Align Left", CATEGORY_CLIENT, false, "If enabled, the quiver hotbar will be aligned to the left of your screen.");
        useMiles = config.getBoolean("Use Miles", CATEGORY_CLIENT, false, "If enabled, speed will be displayed using miles per hour rather than kilometers per hour.");
        oldLightning = OldLightning.get(config.getString("Old Lightning Rendering", CATEGORY_CLIENT, OldLightning.NEVER.name(), "The circumstances under which the old rendering for speedster lightning will be used instead.", OldLightning.KEYS));

        addServer = config.getBoolean("Add Server", CATEGORY_GENERAL, true, "If enabled, the official Fisk's Superheroes server will be automatically added to the server list.");

        electroMagneticId = config.getInt("Electromagnetic Id", CATEGORY_IDS, 185, 0, 255, "The numerical ID to use for the 'Electromagnetic' shield enchantment.");
        capacityId = config.getInt("Holding Id", CATEGORY_IDS, 186, 0, 255, "The numerical ID to use for the 'Holding' quiver enchantment.");
        infinityId = config.getInt("Bottomless Id", CATEGORY_IDS, 187, 0, 255, "The numerical ID to use for the 'Bottomless' quiver enchantment.");
        syncedConfig.load(config);
    }

    public static SyncedConfig get()
    {
        return syncedConfig;
    }

    public enum OldLightning
    {
        NEVER,
        THIRD_PERSON_ONLY,
        FIRST_PERSON_ONLY,
        ALWAYS;

        private static final String[] KEYS;

        public boolean test()
        {
            switch (this)
            {
            case FIRST_PERSON_ONLY:
                return Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
            case THIRD_PERSON_ONLY:
                return !FIRST_PERSON_ONLY.test();
            case ALWAYS:
                return true;
            default:
                return false;
            }
        }

        public static OldLightning get(String key)
        {
            try
            {
                return valueOf(key);
            }
            catch (IllegalArgumentException e)
            {
                return NEVER;
            }
        }

        static
        {
            KEYS = new String[values().length];

            for (OldLightning e : values())
            {
                KEYS[e.ordinal()] = e.name();
            }
        }
    }
}
