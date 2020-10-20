package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.dispenser.BehaviorTrickArrowDispense;
import com.fiskmods.heroes.common.hero.ItemHeroArmor;
import com.fiskmods.heroes.common.registry.ItemRegistry;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class ModItems
{
    public static final ArmorMaterial MATERIAL_SUPERHERO = EnumHelper.addArmorMaterial("FISKHEROES", 2000 / 16, new int[4], 15);
    public static final ToolMaterial MATERIAL_TUTRIDIUM = EnumHelper.addToolMaterial("TUTRIDIUM", 3, 1652, 10.0F, 2.0F, 8);

    public static ItemHeroArmor[] heroArmor;
    public static Item exclusivityToken;

    public static Item compoundBow;
    public static Item quiver;
    public static Item trickArrow;

    public static Item metahumanLog;
    public static Item debugBook;
    public static Item cactusJournal;
    public static Item journalEntry;
    public static Item flashRing;
    public static Item miniATOMSuit;
    public static Item velocityNine;
    public static Item tachyonPrototype;
    public static Item tachyonDevice;
    public static Item tutridiumPickaxe;
    public static Item tutridiumGem;
    public static Item vibraniumPlate;
    public static Item vibraniumDisc;
    public static Item eterniumShard;
    public static Item gunBase;
    public static Item rifleBase;
    public static Item katanaBlade;
    public static Item handle;
    public static Item tachyonBattery;
    public static Item subatomicBattery;
    public static Item displayCase;

    public static Item captainAmericasShield;
    public static Item deadpoolsSwords;
    public static Item blackCanarysTonfas;
    public static Item prometheusSword;
    public static Item boStaff;
    public static Item coldGun;
    public static Item heatGun;
    public static Item ripsGun;
    public static Item chronosRifle;
    public static Item grapplingGun;

    public static Item gameboii;
    public static Item gameboiiCartridge;

    public static void register()
    {
        heroArmor = new ItemHeroArmor[4];
        ItemRegistry.registerItem("superhero_helmet", heroArmor[0] = new ItemHeroArmor(0));
        ItemRegistry.registerItem("superhero_chestplate", heroArmor[1] = new ItemHeroArmor(1));
        ItemRegistry.registerItem("superhero_leggings", heroArmor[2] = new ItemHeroArmor(2));
        ItemRegistry.registerItem("superhero_boots", heroArmor[3] = new ItemHeroArmor(3));
        
        for (Item item : heroArmor)
        {
            item.setCreativeTab(FiskHeroes.TAB_SUITS);
        }

        compoundBow = ItemRegistry.registerItemNoTab("compound_bow", new ItemCompoundBow().setCreativeTab(FiskHeroes.TAB_ARCHERY));
        quiver = ItemRegistry.registerItemNoTab("quiver", new ItemQuiver().setCreativeTab(FiskHeroes.TAB_ARCHERY));
        trickArrow = ItemRegistry.registerItemNoTab("trick_arrow", new ItemTrickArrow().setCreativeTab(FiskHeroes.TAB_ARCHERY));

        metahumanLog = ItemRegistry.registerItem("metahuman_log", new ItemMetahumanLog());
        debugBook = ItemRegistry.registerItemNoTab("debug_book", new ItemDebugBook());
//        cactusJournal = ItemRegistry.registerItem("cactus_journal", new ItemCactusJournal());
//        journalEntry = ItemRegistry.registerItem("journal_entry", new ItemJournalEntry());
        flashRing = ItemRegistry.registerItem("flash_ring", new ItemFlashRing());
        miniATOMSuit = ItemRegistry.registerItem("mini_atom_suit", new ItemMiniAtomSuit());
        velocityNine = ItemRegistry.registerItem("velocity_nine", new ItemVel9());
        tachyonPrototype = ItemRegistry.registerItem("tachyon_prototype", new ItemTachyonDevice());
        tachyonDevice = ItemRegistry.registerItem("tachyon_device", new ItemTachyonDevice());
        tutridiumPickaxe = ItemRegistry.registerItem("tutridium_pickaxe", new ItemPickaxeSH(MATERIAL_TUTRIDIUM));
        tutridiumGem = ItemRegistry.registerItem("tutridium_gem", new ItemMaterialEnergy(512));
        vibraniumPlate = ItemRegistry.registerItem("vibranium_plate", new ItemMaterialEnergy(32));
        vibraniumDisc = ItemRegistry.registerItem("vibranium_disc", new ItemDisc());
        eterniumShard = ItemRegistry.registerItem("eternium_shard", new ItemMaterialEnergy(1024));
        gunBase = ItemRegistry.registerItem("gun_base", new Item());
        rifleBase = ItemRegistry.registerItem("rifle_base", new Item());
        katanaBlade = ItemRegistry.registerItem("katana_blade", new Item());
        handle = ItemRegistry.registerItem("handle", new Item());
        tachyonBattery = ItemRegistry.registerItem("tachyon_battery", new Item());
        subatomicBattery = ItemRegistry.registerItem("subatomic_battery", new ItemSubatomicBattery());
        displayCase = ItemRegistry.registerItem("display_case", new ItemDisplayCase());

        captainAmericasShield = ItemRegistry.registerItem("captain_americas_shield", new ItemCapsShield());
        deadpoolsSwords = ItemRegistry.registerItem("deadpools_swords", new ItemDeadpoolsSwords());
        blackCanarysTonfas = ItemRegistry.registerItem("black_canarys_tonfas", new ItemBlackCanarysTonfas());
        prometheusSword = ItemRegistry.registerItem("prometheus_sword", new ItemPrometheusSword());
        boStaff = ItemRegistry.registerItem("bo_staff", new ItemBoStaff());
        coldGun = ItemRegistry.registerItem("cold_gun", new ItemColdGun());
        heatGun = ItemRegistry.registerItem("heat_gun", new ItemHeatGun());
        ripsGun = ItemRegistry.registerItem("rip_hunters_gun", new ItemRipHuntersGun());
        chronosRifle = ItemRegistry.registerItem("chronos_rifle", new ItemChronosRifle());
        grapplingGun = ItemRegistry.registerItem("grappling_gun", new ItemGrapplingGun());

        exclusivityToken = ItemRegistry.registerItemNoTab("exclusivity_token", new ItemExclusivityToken());
        gameboii = ItemRegistry.registerItem("gameboii", new ItemGameboii());
        gameboiiCartridge = ItemRegistry.registerItem("gameboii_cartridge", new ItemGameboiiCartridge());

        registerDispenserBehaviors();
        MATERIAL_TUTRIDIUM.setRepairItem(new ItemStack(tutridiumGem));
    }

    private static void registerDispenserBehaviors()
    {
        BlockDispenser.dispenseBehaviorRegistry.putObject(trickArrow, new BehaviorTrickArrowDispense());
    }
}
