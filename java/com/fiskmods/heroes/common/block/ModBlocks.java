package com.fiskmods.heroes.common.block;

import static com.fiskmods.heroes.common.registry.BlockRegistry.*;

import com.fiskmods.heroes.common.item.ItemDisplayStand;
import com.fiskmods.heroes.common.item.ItemIceLayer;
import com.fiskmods.heroes.common.item.ItemTreadmill;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.tileentity.TileEntityCosmicFabricator;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.common.tileentity.TileEntityIceLayer;
import com.fiskmods.heroes.common.tileentity.TileEntityParticleCore;
import com.fiskmods.heroes.common.tileentity.TileEntitySuitFabricator;
import com.fiskmods.heroes.common.tileentity.TileEntityTreadmill;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemSlab;

public class ModBlocks
{
    public static Block tutridiumOre;
    public static Block vibraniumOre;
    public static Block eterniumOre;
    public static Block eterniumStone;
    public static Block tutridiumBlock;
    public static Block vibraniumBlock;
    public static Block eterniumBlock;
    public static Block superchargedEternium;

    public static Block nexusBricks;
    public static Block nexusBrickStairs;
    public static BlockSlab nexusBrickDoubleSlab;
    public static BlockSlab nexusBrickSlab;
    public static Block nexusSoil;

    public static Block treadmill;
    public static Block iceLayer;
    public static Block frostedIce;
    public static Block displayStand;
    public static Block displayStandTop;
    public static Block suitFabricator;
    public static Block suitIterator;
    public static Block cosmicFabricator;
    public static Block weaponCrate;

    public static Block subatomicParticleShell;
    public static Block subatomicParticleCore;
    public static Block tachyonicParticleShell;
    public static Block tachyonicParticleCore;
//    public static Block quantumMatter;

//    public static Block ruleHandler;

    public static void register()
    {
        tutridiumOre = builder("tutridium_ore").register(new BlockOreSH(ModItems.tutridiumGem).setHarvestLvl("pickaxe", 2).setCanFortuneHarvest(false).setCanSilkHarvest(false).setItemDrop(ModItems.tutridiumGem).setXpDrop(3, 7).setHardness(3.0F).setResistance(5.0F));
        vibraniumOre = builder("vibranium_ore").ore("oreVibranium").register(new BlockOreSH(ModItems.vibraniumPlate).setHarvestLvl("pickaxe", 1).setCanFortuneHarvest(false).setHardness(3.0F).setResistance(2000.0F));
        eterniumOre = builder("eternium_ore").ore("oreEternium").register(new BlockOreSH(ModItems.eterniumShard).setHarvestLvl("pickaxe", 3).setCanFortuneHarvest(false).setHardness(5.0F).setResistance(3000.0F));
        eterniumStone = builder("eternium_stone").register(new BlockSH(Material.rock).setHardness(5.0F).setResistance(3000.0F));
        tutridiumBlock = builder("tutridium_block").register(new BlockCompressedSH(ModItems.tutridiumGem).setHarvestLvl("pickaxe", 2).setHardness(5.0F).setResistance(10.0F).setStepSound(Block.soundTypeMetal));
        vibraniumBlock = builder("vibranium_block").ore("blockVibranium").register(new BlockCompressedSH(ModItems.vibraniumPlate).setHarvestLvl("pickaxe", 1).setHardness(5.0F).setResistance(4000.0F).setStepSound(Block.soundTypeMetal));
        eterniumBlock = builder("eternium_block").ore("blockEternium").register(new BlockCompressedSH(ModItems.eterniumShard).setHarvestLvl("pickaxe", 3).setHardness(7.5F).setResistance(6000.0F).setStepSound(Block.soundTypeMetal));
        superchargedEternium = builder("supercharged_eternium").register(new BlockSCEternium().setHardness(20.0F).setResistance(6000.0F));

        nexusBricks = builder("nexus_bricks").register(new BlockSH(Material.rock).setHardness(3.0F).setResistance(100.0F));
        nexusBrickStairs = builder("nexus_brick_stairs").register(new BlockModStairs(nexusBricks, 0));
        nexusBrickDoubleSlab = (BlockSlab) new BlockNexusBrickSlab(true).setHardness(3.0F).setResistance(100.0F);
        nexusBrickSlab = (BlockSlab) new BlockNexusBrickSlab(false).setHardness(3.0F).setResistance(100.0F);
        builder("nexus_brick_double_slab").item(t -> new ItemSlab(t, nexusBrickSlab, (BlockSlab) t, true)).register(nexusBrickDoubleSlab);
        builder("nexus_brick_slab").item(t -> new ItemSlab(t, (BlockSlab) t, nexusBrickDoubleSlab, false)).register(nexusBrickSlab);
        nexusSoil = builder("nexus_soil").register(new BlockNexusSoil().setHardness(0.9F));

        treadmill = builder("treadmill").tile(TileEntityTreadmill.class, "Treadmill").item(ItemTreadmill.class).register(new BlockTreadmill().setHardness(0.5F));
        iceLayer = builder("ice_layer").tile(TileEntityIceLayer.class, "Ice Layer").item(ItemIceLayer.class).register(new BlockIceLayer().setHardness(0.5F).setStepSound(Block.soundTypeGlass));
        frostedIce = builder("frosted_ice").register(new BlockFrostedIce().setHardness(0.5F).setStepSound(Block.soundTypeGlass));
        displayStand = builder("display_stand").tile(TileEntityDisplayStand.class, "Display Stand").item(ItemDisplayStand.class).register(new BlockDisplayStand(false));
        displayStandTop = builder("display_stand_top").register(new BlockDisplayStand(true));
        suitFabricator = builder("suit_fabricator").tile(TileEntitySuitFabricator.class, "Suit Fabricator").register(new BlockSuitFabricator().setHardness(3.0F).setResistance(5.0F).setStepSound(Block.soundTypeMetal));
        suitIterator = builder("suit_iterator").register(new BlockSuitIterator().setHardness(3.0F).setResistance(5.0F).setStepSound(Block.soundTypeMetal));
        cosmicFabricator = builder("cosmic_suit_fabricator").tile(TileEntityCosmicFabricator.class).register(new BlockCosmicFabricator().setHardness(4.0F).setResistance(20.0F).setStepSound(Block.soundTypeMetal));
        weaponCrate = builder("weapon_crate").register(new BlockWeaponCrate().setHardness(4.0F).setResistance(200.0F).setStepSound(Block.soundTypeMetal));

        subatomicParticleShell = builder("subatomic_particle_shell").register(new BlockParticleShell().setHardness(3.0F).setResistance(5.0F).setLightOpacity(0));
        subatomicParticleCore = builder("subatomic_particle_core").tile(TileEntityParticleCore.class, "Subatomic Particle Core").register(new BlockSubatomicCore().setBlockUnbreakable().setResistance(6000000.0F).setLightLevel(1));
        tachyonicParticleShell = builder("tachyonic_particle_shell").register(new BlockParticleShell().setHardness(3.0F).setResistance(5.0F).setLightOpacity(0));
        tachyonicParticleCore = builder("tachyonic_particle_core").tile(TileEntityParticleCore.class, "Tachyonic Particle Core").register(new BlockTachyonicCore().setHardness(10.0F).setResistance(6000000.0F).setLightLevel(1));
//        quantumMatter = builder("quantum_matter").register(new BlockSH(Material.rock).setHardness(3.0F).setResistance(5.0F).setLightOpacity(15).setStepSound(Block.soundTypeSnow));

//      ruleHandler = new BlockRuleHandler().setBlockUnbreakable().setResistance(6000000.0F);
//        registerTileEntity(ruleHandler, "Rule Handler", TileEntityRuleHandler.class);

        displayStandTop.setCreativeTab(null);
//        ruleHandler.setCreativeTab(null);
    }
}
