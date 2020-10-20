package com.fiskmods.heroes.common.achievement;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.item.ItemExclusivityToken.IconType;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.gameboii.GameboiiCartridge;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class SHAchievements
{
    public static final AchievementPage PAGE = new AchievementPage(FiskHeroes.NAME);

    public static final Achievement SUIT_FABRICATOR = new AchievementSH("suitFabricator", 0, 0, ModBlocks.suitFabricator, null);
    public static final Achievement SUIT_ITERATOR = new AchievementSH("suitIterator", 1, -1, ModBlocks.suitIterator, SUIT_FABRICATOR);
    public static final Achievement COSMIC_FABRICATOR = new AchievementSH("cosmicFabricator", -3, 3, ModBlocks.cosmicFabricator, SUIT_FABRICATOR);
    public static final Achievement TIER1 = new AchievementSH("tier1", 2, 2, IconType.TIER1.create(), SUIT_FABRICATOR);
    public static final Achievement TIER2 = new AchievementSH("tier2", 2, 1, IconType.TIER2.create(), TIER1);
    public static final Achievement TIER3 = new AchievementSH("tier3", 2, 0, IconType.TIER3.create(), TIER2);
    public static final Achievement TIER4 = new AchievementSH("tier4", 0, 2, IconType.TIER4.create(), TIER3);
    public static final Achievement TIER5 = new AchievementSH("tier5", 1, 2, IconType.TIER5.create(), TIER4);
    public static final Achievement TIER6 = new AchievementSH("tier6", 0, 2, IconType.TIER6.create(), TIER5);
    public static final Achievement ALL_SUITS = new AchievementSH("allSuits", -1, 2, Blocks.cactus, TIER6).setSpecial();
    public static final Achievement ALL_ARROWS = new AchievementSH("allArrows", 9, 8, ModItems.trickArrow, null).setSpecial();
    public static final Achievement DISPLAY_CASE = new AchievementSH("displayCase", -2, -2, ModItems.displayCase, null);
    public static final Achievement SECRET = new AchievementSH("secret", -5, 1, Items.potato, null);

    public static final Achievement GAMEBOII = new AchievementSH("gameboii", 10, 4, ModItems.gameboii, null);
    public static final Achievement BATFISH = new AchievementSH("batfish", 2, -1, GameboiiCartridge.BATFISH.create(), GAMEBOII);
    public static final Achievement SPODERMEN = new AchievementSH("spodermen", 2, 0, IconType.SPODERMEN.create(), BATFISH);

    public static final Achievement OVER_9000 = new AchievementSH("over9000", -2, 1, ModItems.tutridiumGem, SUIT_FABRICATOR);
    public static final Achievement FACEPLANT = new AchievementSH("glideFaceplant", 2, -1, IconType.FACEPLANT.create(), TIER1);
    public static final Achievement KILL_DEADPOOL = new AchievementSH("language", 2, -1, ModItems.captainAmericasShield, TIER3);
    public static final Achievement LAND_DEADPOOL = new AchievementSH("landing", 0, -2, IconType.LAND_DEADPOOL.create(), TIER3);

    public static final Achievement QUANTUM_REALM = new AchievementSH("quantumRealm", 0, 4, ModBlocks.subatomicParticleCore, TIER1);
    public static final Achievement TACHYONS = new AchievementSH("tachyons", 0, 2, ModItems.tachyonPrototype, QUANTUM_REALM);
    public static final Achievement BLACK_HOLE = new AchievementSH("blackHole", 2, 0, new ItemStack(ModBlocks.subatomicParticleCore, 1, 5), QUANTUM_REALM);

    public static final Achievement KMPH_1000 = new AchievementSH("kmph1000", -2, 2, IconType.KMPH_1000.create(), TIER1);
    public static final Achievement MAX_SPEED = new AchievementSH("maxSpeed", -2, 1, ModBlocks.treadmill, KMPH_1000).setSpecial();

    public static final Achievement SPEED_CURE = new AchievementSH("speedCure", 5, -1, ModItems.velocityNine, null);
    public static final Achievement ALL_DEBUFFS = new AchievementSH("allDebuffs", 2, 0, Items.milk_bucket, SPEED_CURE).setSpecial();

    public static final Achievement[] TIERS = {TIER1, TIER2, TIER3, TIER4, TIER5, TIER6};

    public static void register()
    {
        AchievementPage.registerAchievementPage(PAGE);
    }
}
