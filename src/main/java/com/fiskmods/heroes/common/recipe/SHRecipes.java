package com.fiskmods.heroes.common.recipe;

import static com.fiskmods.heroes.common.arrowtype.ArrowTypeManager.*;
import static net.minecraftforge.oredict.RecipeSorter.Category.*;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.item.ItemCapsShield;
import com.fiskmods.heroes.common.item.ItemCompoundBow;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.gameboii.GameboiiColor;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class SHRecipes
{
    public static void register()
    {
        addRecipe("display_case", new RecipesDisplayCase(), SHAPED, "after:minecraft:shapeless");
        addRecipe("tachyon_recharge", new RecipesTachyonRecharge(), SHAPELESS, "after:minecraft:shapeless");
        addRecipe("gameboii_cartridge", new RecipesGameboiiCartridge(), SHAPELESS, "after:minecraft:shapeless");
//        addRecipe("cactus_journal", new RecipesCactusJournal(), SHAPELESS, "after:minecraft:shapeless");

        GameRegistry.addRecipe(new ItemStack(ModItems.velocityNine, 1), " E ", "TNB", " P ", 'E', Items.ender_pearl, 'T', Items.ghast_tear, 'N', Items.nether_star, 'B', Items.blaze_powder, 'P', new ItemStack(Items.potionitem, 1, 8226));
        GameRegistry.addRecipe(new ItemStack(ModItems.vibraniumDisc, 1), "VV", "VV", 'V', ModItems.vibraniumPlate);
        GameRegistry.addRecipe(new ItemStack(ModItems.gunBase, 1), "BB ", " WI", 'B', Blocks.iron_block, 'W', Blocks.iron_bars, 'I', Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(ModItems.rifleBase, 1), "I  ", "II ", " BC", 'I', Items.iron_ingot, 'C', new ItemStack(ModItems.subatomicBattery, 1, SHConstants.MAX_SUBATOMIC_CHARGE), 'B', Blocks.iron_block);
        GameRegistry.addRecipe(new ItemStack(ModItems.katanaBlade, 1), "  I", " I ", "I  ", 'I', Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(ModItems.tachyonBattery, 1), "DID", "ICI", "DID", 'D', Items.diamond, 'I', Items.iron_ingot, 'C', ModBlocks.tachyonicParticleCore);
        GameRegistry.addRecipe(new ItemStack(ModItems.tachyonPrototype, 1, SHConstants.MAX_CHARGE_DEVICE), "IBI", "BCB", "IBI", 'I', Items.iron_ingot, 'B', Blocks.iron_bars, 'C', ModItems.tachyonBattery);
        GameRegistry.addRecipe(new ItemStack(ModItems.tachyonDevice, 1, SHConstants.MAX_CHARGE_DEVICE), "GIG", "ICI", "GIG", 'G', Items.gold_ingot, 'I', Items.iron_ingot, 'C', ModItems.tachyonBattery);
        GameRegistry.addRecipe(new ItemStack(ModItems.displayCase, 2), "SSS", "G G", "GSG", 'S', new ItemStack(Blocks.stone_slab, 1, 0), 'G', new ItemStack(Blocks.stained_glass, 1, 0));
        GameRegistry.addRecipe(new ItemStack(ModItems.subatomicBattery, 1), "ISI", "SBS", "ISI", 'I', Items.iron_ingot, 'S', ModBlocks.subatomicParticleShell, 'B', ModItems.tachyonBattery);
        GameRegistry.addRecipe(new ItemStack(ModItems.tutridiumPickaxe, 1), "TOT", " P ", " S ", 'T', ModItems.tutridiumGem, 'O', Blocks.obsidian, 'P', Items.diamond_pickaxe, 'S', Items.stick);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.handle, 1), Items.stick, new ItemStack(Items.dye, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.vibraniumDisc, 1, 1), new ItemStack(ModItems.vibraniumDisc, 1, 0), new ItemStack(Items.dye, 1, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.vibraniumDisc, 1, 2), new ItemStack(ModItems.vibraniumDisc, 1, 0), new ItemStack(Items.dye, 1, 4));
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.vibraniumDisc, 1, 3), new ItemStack(ModItems.vibraniumDisc, 1, 0), new ItemStack(Items.dye, 1, 12));
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.metahumanLog, 1), Items.book, new ItemStack(Items.dye, 1, 6), Items.redstone);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.tachyonPrototype, 1), new ItemStack(ModItems.tachyonPrototype, 1, SHConstants.MAX_CHARGE_DEVICE), ModBlocks.tachyonicParticleShell);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.tachyonDevice, 1), new ItemStack(ModItems.tachyonDevice, 1, SHConstants.MAX_CHARGE_DEVICE), ModBlocks.tachyonicParticleShell);

        for (int i = 0; i < 16; ++i)
        {
            GameRegistry.addRecipe(new ItemStack(ModBlocks.displayStand, 1, i), " W ", " W ", "SSS", 'W', new ItemStack(Blocks.wool, 1, i), 'S', new ItemStack(Blocks.stone_slab, 1, 0));
        }

        int[] dyes = {0, 6, 10, 14, 5, 1, 15, 11};

        for (GameboiiColor color : GameboiiColor.values())
        {
            GameRegistry.addRecipe(new ItemStack(ModItems.gameboii, 1, color.ordinal()), "DDD", "ESE", "E E", 'D', new ItemStack(Items.dye, 1, dyes[color.ordinal()]), 'E', ModItems.eterniumShard, 'S', ModBlocks.superchargedEternium);
        }

        GameRegistry.addRecipe(new ItemStack(ModBlocks.treadmill, 1), "IDI", "BQI", "IDI", 'I', Items.iron_ingot, 'D', Items.diamond, 'B', Blocks.iron_block, 'Q', new ItemStack(Blocks.stone_slab, 1, 7));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.suitFabricator, 1), "SGS", "ITI", "ICI", 'S', new ItemStack(Blocks.stone_slab, 1, 0), 'G', ModItems.tutridiumGem, 'I', Blocks.iron_block, 'T', Blocks.crafting_table, 'C', Blocks.stone);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.suitIterator, 1), "IGI", "VRV", "VSV", 'I', Items.iron_ingot, 'G', ModItems.tutridiumGem, 'V', ModItems.vibraniumPlate, 'R', Items.redstone, 'S', Blocks.stone);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.cosmicFabricator, 1), "GEG", "tSt", "tTt", 'G', Items.gold_ingot, 'E', ModItems.eterniumShard, 't', ModItems.tutridiumGem, 'S', ModBlocks.superchargedEternium, 'T', ModBlocks.tutridiumBlock);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.iceLayer, 16), Blocks.packed_ice);

        addMaterialRecipes(ModItems.tutridiumGem, ModBlocks.tutridiumBlock, ModBlocks.tutridiumOre, 1);
        addMaterialRecipes(ModItems.vibraniumPlate, ModBlocks.vibraniumBlock, ModBlocks.vibraniumOre, 1);
        addMaterialRecipes(ModItems.eterniumShard, ModBlocks.eterniumBlock, ModBlocks.eterniumOre, 2);

        GameRegistry.addRecipe(new ItemStack(ModBlocks.nexusBricks, 8, 0), "#-#", "-#-", "#-#", '#', Blocks.stonebrick, '-', ModBlocks.eterniumStone);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.nexusBrickSlab, 6, 0), "###", '#', ModBlocks.nexusBricks);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.nexusBrickStairs, 4), "#  ", "## ", "###", '#', ModBlocks.nexusBricks);

        addWeaponRecipes();
        addArrowRecipes();
    }

    private static void addMaterialRecipes(Item item, Block block, Block ore, float xp)
    {
        GameRegistry.addSmelting(ore, new ItemStack(item), xp);
        GameRegistry.addShapelessRecipe(new ItemStack(item, 9), block);
        GameRegistry.addRecipe(new ItemStack(block), "###", "###", "###", '#', item);
    }

    private static void addWeaponRecipes()
    {
        addRecipe("auto_quiver", new RecipesAutomaticQuiver(), SHAPELESS, "after:minecraft:shapeless");
        addRecipe("bow_repair", new RecipesBowRepair(), SHAPELESS, "after:minecraft:shapeless");

        GameRegistry.addRecipe(new ItemStack(ModItems.flashRing, 1), "NGN", "N N", "NNN", 'N', Items.gold_nugget, 'G', Items.gold_ingot);
        GameRegistry.addRecipe(new ItemStack(ModItems.quiver, 1), "GLI", "L L", "LL ", 'G', new ItemStack(Items.dye, 1, 2), 'L', Items.leather, 'I', Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(ModItems.compoundBow, 1), "WII", "I S", "ISS", 'W', Items.stick, 'I', Items.iron_ingot, 'S', Items.string);
        GameRegistry.addRecipe(new ItemStack(ModItems.captainAmericasShield, 1), " W ", "RVR", " B ", 'R', new ItemStack(ModItems.vibraniumDisc, 1, 1), 'W', new ItemStack(ModItems.vibraniumDisc, 1, 0), 'B', new ItemStack(ModItems.vibraniumDisc, 1, 2), 'V', ModItems.vibraniumPlate);
        GameRegistry.addRecipe(ItemCapsShield.setStealth(new ItemStack(ModItems.captainAmericasShield, 1), true), " W ", "LVL", " B ", 'L', new ItemStack(ModItems.vibraniumDisc, 1, 3), 'W', new ItemStack(ModItems.vibraniumDisc, 1, 0), 'B', new ItemStack(ModItems.vibraniumDisc, 1, 2), 'V', ModItems.vibraniumPlate);
        GameRegistry.addRecipe(new ItemStack(ModItems.deadpoolsSwords, 1), "B B", "H H", "G G", 'B', ModItems.katanaBlade, 'H', ModItems.handle, 'G', Items.gold_nugget);
        GameRegistry.addRecipe(new ItemStack(ModItems.blackCanarysTonfas, 1), "OHH", "   ", "HHO", 'H', ModItems.handle, 'O', Blocks.obsidian);
        GameRegistry.addRecipe(new ItemStack(ModItems.prometheusSword, 1), "B", "H", 'B', ModItems.katanaBlade, 'H', ModItems.handle);
        GameRegistry.addRecipe(new ItemStack(ModItems.boStaff, 1), "  H", " I ", "H  ", 'H', ModItems.handle, 'I', Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(ModItems.coldGun, 1), "CI ", " GI", " BI", 'C', Blocks.ice, 'I', Items.iron_ingot, 'G', ModItems.gunBase, 'B', Blocks.stone_button);
        GameRegistry.addRecipe(new ItemStack(ModItems.heatGun, 1), "CI ", "BGI", " I ", 'C', Items.blaze_powder, 'I', Items.iron_ingot, 'G', ModItems.gunBase, 'B', Blocks.stone_button);
        GameRegistry.addRecipe(new ItemStack(ModItems.ripsGun, 1), "IC ", " GI", " BI", 'C', new ItemStack(ModItems.subatomicBattery, 1, SHConstants.MAX_SUBATOMIC_CHARGE), 'I', Items.iron_ingot, 'G', ModItems.gunBase, 'B', Blocks.stone_button);
        GameRegistry.addRecipe(new ItemStack(ModItems.chronosRifle, 1), "RI ", " GI", " BC", 'R', ModItems.rifleBase, 'I', Items.iron_ingot, 'G', ModItems.gunBase, 'B', Blocks.stone_button, 'C', new ItemStack(ModItems.subatomicBattery, 1, SHConstants.MAX_SUBATOMIC_CHARGE));
        GameRegistry.addRecipe(new ItemStack(ModItems.grapplingGun, 1), "OI ", "LGI", "BW ", 'I', Items.iron_ingot, 'O', Blocks.iron_block, 'L', Items.lead, 'G', ModItems.gunBase, 'B', Blocks.stone_button, 'W', new ItemStack(Blocks.planks, 1, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.quiver, 1, 1), new ItemStack(ModItems.quiver, 1, 0), Blocks.hopper);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.compoundBow, 1), ItemCompoundBow.setBroken(new ItemStack(ModItems.compoundBow), true), Items.string);
    }

    private static void addArrowRecipes()
    {
        addRecipe("firework_arrow", new RecipesFireworkArrow(), SHAPELESS, "after:minecraft:shapeless");
        addRecipe("vial_arrow", new RecipesVialArrow(), SHAPELESS, "after:minecraft:shapeless");

        addArrowRecipe(4, NORMAL, Items.iron_ingot, new ItemStack(Items.dye, 1, 2), 4, Items.arrow);
        addArrowRecipe(3, NORMAL, TRIPLE);
        addArrowRecipe(1, EXPLOSIVE, NORMAL, 2, Items.gunpowder);
        addArrowRecipe(3, EXPLOSIVE, TRIPLE_EXPL);
        addArrowRecipe(2, GRAPPLE, 2, NORMAL, Items.lead, Blocks.sticky_piston);
        addArrowRecipe(1, TRIPLE, 3, NORMAL);
        addArrowRecipe(1, CARROT, NORMAL, Items.carrot);
        addArrowRecipe(1, FIREWORK, NORMAL, Items.fireworks);
        addArrowRecipe(1, FIRE_CHARGE, NORMAL, Items.fire_charge);
        addArrowRecipe(1, CACTUS, EXPLOSIVE, 2, Blocks.cactus);
        addArrowRecipe(1, BOXING_GLOVE, NORMAL, Blocks.wool, Items.leather);
        addArrowRecipe(1, TORCH, NORMAL, Blocks.torch);
        addArrowRecipe(2, VIBRANIUM, 2, NORMAL, ModItems.vibraniumPlate);
        addArrowRecipe(1, PHANTOM, NORMAL, 8, ModBlocks.subatomicParticleShell);
        addArrowRecipe(1, PUFFERFISH, NORMAL, new ItemStack(Items.fish, 1, 3));
        addArrowRecipe(1, EXPL_PUFFERF, EXPLOSIVE, new ItemStack(Items.fish, 1, 3));
        addArrowRecipe(1, EXPL_PUFFERF, PUFFERFISH, 2, Items.gunpowder);
        addArrowRecipe(1, VINE, NORMAL, 2, Blocks.vine, Items.string);
        addArrowRecipe(2, SMOKE_BOMB, 2, NORMAL, Items.iron_ingot, Blocks.furnace, Items.coal);
        addArrowRecipe(1, SLIME, NORMAL, Items.slime_ball);
        addArrowRecipe(1, TRIPLE_EXPL, 3, EXPLOSIVE);
        addArrowRecipe(1, ENDER_PEARL, NORMAL, Items.ender_pearl);
        addArrowRecipe(2, TUTRIDIUM, 2, NORMAL, ModItems.tutridiumGem);
        addArrowRecipe(1, EXCESSIVE, NORMAL, Items.golden_sword);
        addArrowRecipe(1, GROSS, NORMAL, 2, Items.nether_wart);
        addArrowRecipe(1, GLITCH, PHANTOM, Items.ender_pearl);
        addArrowRecipe(1, GLITCH, ENDER_PEARL, 8, ModBlocks.subatomicParticleShell);
        addArrowRecipe(1, BLAZE, NORMAL, Items.blaze_powder);
        addArrowRecipe(1, PULSE, NORMAL, Blocks.redstone_torch);
        addArrowRecipe(1, DETONATOR, NORMAL, Blocks.tnt, Items.redstone);
        addArrowRecipe(4, FIREBALL, 4, NORMAL, 4, Items.gunpowder, Items.lava_bucket);
    }

    public static void addArrowRecipe(int outputAmount, ArrowType result, Object... objects)
    {
        GameRegistry.addShapelessRecipe(result.makeItem(outputAmount), collect(objects));
    }

    public static Object[] collect(Object... objects)
    {
        List list = new ArrayList<>();
        int repeat = 1;

        for (Object object : objects)
        {
            Object obj = object;

            if (obj instanceof ArrowType)
            {
                obj = ((ArrowType) obj).makeItem();
            }

            if (obj instanceof ItemStack || obj instanceof Item || obj instanceof Block)
            {
                for (int j = 0; j < repeat; ++j)
                {
                    list.add(obj);
                }

                repeat = 1;
            }
            else if (obj instanceof Number)
            {
                repeat = ((Number) obj).intValue();
            }
        }

        return list.toArray();
    }

    private static void addRecipe(String name, IRecipe recipe, Category category, String dependencies)
    {
        GameRegistry.addRecipe(recipe);
        RecipeSorter.register(FiskHeroes.MODID + ":" + name, recipe.getClass(), category, dependencies);
    }
}
