package com.fiskmods.heroes.common.hero;

import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.Pair;
import com.fiskmods.heroes.pack.JSHeroesEngine;

import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public enum LegacyHeroManager
{
    INSTANCE;

    private final Map<String, Pair<String, Integer>> map = new HashMap<>(64);
    private final Map<Integer, Pair<String, Integer>> idMap = new HashMap<>();

    private boolean dirty;

    private LegacyHeroManager()
    {
        put(0, "ant_mans_helmet", "ant_man");
        put(1, "ant_mans_chestpiece", "ant_man");
        put(2, "ant_mans_pants", "ant_man");
        put(3, "ant_mans_boots", "ant_man");

        put(0, "arrows_hood", "arrow");
        put(1, "arrows_chestpiece", "arrow");
        put(2, "arrows_pants", "arrow");
        put(3, "arrows_boots", "arrow");

        put(0, "arsenals_hood", "arsenal");
        put(1, "arsenals_chestpiece", "arsenal");
        put(2, "arsenals_pants", "arsenal");
        put(3, "arsenals_boots", "arsenal");

        put(0, "atoms_helmet", "atom");
        put(1, "atoms_chestplate", "atom");
        put(2, "atoms_leggings", "atom");
        put(3, "atoms_boots", "atom");

        put(0, "atom_smashers_helmet", "atom_smasher");
        put(0, "atom_smashers_mask", "atom_smasher");
        put(1, "atom_smashers_chestpiece", "atom_smasher");
        put(2, "atom_smashers_pants", "atom_smasher");
        put(3, "atom_smashers_boots", "atom_smasher");

        put(0, "batman_dceus_cowl", "batman_dceu");
        put(0, "batmans_cowl_dceu", "batman_dceu");
        put(0, "batmans_mask_dceu", "batman_dceu");
        put(1, "batman_dceus_chestpiece", "batman_dceu");
        put(1, "batmans_chestpiece_dceu", "batman_dceu");
        put(2, "batman_dceus_pants", "batman_dceu");
        put(2, "batmans_pants_dceu", "batman_dceu");
        put(3, "batman_dceus_boots", "batman_dceu");
        put(3, "batmans_boots_dceu", "batman_dceu");

        put(0, "black_canarys_mask", "black_canary");
        put(1, "black_canarys_chestpiece", "black_canary");
        put(2, "black_canarys_pants", "black_canary");
        put(3, "black_canarys_boots", "black_canary");

        put(0, "black_panthers_helmet", "black_panther");
        put(0, "black_panthers_mask", "black_panther");
        put(1, "black_panthers_chestpiece", "black_panther");
        put(2, "black_panthers_pants", "black_panther");
        put(3, "black_panthers_boots", "black_panther");

        put(0, "captain_americas_helmet", "captain_america");
        put(1, "captain_americas_chestpiece", "captain_america");
        put(2, "captain_americas_pants", "captain_america");
        put(3, "captain_americas_boots", "captain_america");

        put(0, "captain_colds_goggles", "captain_cold");
        put(1, "captain_colds_jacket", "captain_cold");
        put(2, "captain_colds_pants", "captain_cold");
        put(3, "captain_colds_boots", "captain_cold");

        put(0, "chronos_helmet", "chronos");
        put(1, "chronos_chestplate", "chronos");
        put(2, "chronos_leggings", "chronos");
        put(3, "chronos_boots", "chronos");

        put(0, "colossus_head", "colossus_xmen");
        put(0, "colossus_xmens_head", "colossus_xmen");
        put(1, "colossus_torso", "colossus_xmen");
        put(1, "colossus_xmens_torso", "colossus_xmen");
        put(2, "colossus_pants", "colossus_xmen");
        put(2, "colossus_xmens_pants", "colossus_xmen");
        put(3, "colossus_boots", "colossus_xmen");
        put(3, "colossus_xmens_boots", "colossus_xmen");

        put(0, "dark_archers_hood", "dark_archer");
        put(0, "league_hood", "dark_archer");
        put(1, "dark_archers_robes", "dark_archer");
        put(1, "league_robes", "dark_archer");
        put(2, "dark_archers_pants", "dark_archer");
        put(2, "league_pants", "dark_archer");
        put(3, "dark_archers_boots", "dark_archer");
        put(3, "league_boots", "dark_archer");

        put(0, "deadpool_xmens_mask", "deadpool_xmen");
        put(0, "deadpools_mask", "deadpool_xmen");
        put(1, "deadpool_xmens_chestpiece", "deadpool_xmen");
        put(1, "deadpools_chestpiece", "deadpool_xmen");
        put(2, "deadpool_xmens_pants", "deadpool_xmen");
        put(2, "deadpools_pants", "deadpool_xmen");
        put(3, "deadpool_xmens_boots", "deadpool_xmen");
        put(3, "deadpools_boots", "deadpool_xmen");

        put(0, "falcons_goggles", "falcon");
        put(1, "falcons_chestpiece", "falcon");
        put(2, "falcons_pants", "falcon");
        put(3, "falcons_boots", "falcon");

        put(1, "firestorms_jacket", "firestorm");
        put(2, "firestorms_pants", "firestorm");
        put(3, "firestorms_shoes", "firestorm");

        put(1, "firestorm_jaxs_chestpiece", "firestorm_jax");
        put(1, "firestorms_chestpiece_jax", "firestorm_jax");
        put(2, "firestorm_jaxs_pants", "firestorm_jax");
        put(2, "firestorms_pants_jax", "firestorm_jax");
        put(3, "firestorm_jaxs_boots", "firestorm_jax");
        put(3, "firestorms_boots_jax", "firestorm_jax");

        put(0, "geomancers_goggles", "geomancer");
        put(1, "geomancers_chestpiece", "geomancer");
        put(2, "geomancers_pants", "geomancer");
        put(3, "geomancers_boots", "geomancer");

        put(0, "green_arrows_hood", "green_arrow");
        put(1, "green_arrows_chestpiece", "green_arrow");
        put(2, "green_arrows_pants", "green_arrow");
        put(3, "green_arrows_boots", "green_arrow");

        put(0, "heatwaves_goggles", "heatwave");
        put(1, "heatwaves_jacket", "heatwave");
        put(2, "heatwaves_pants", "heatwave");
        put(3, "heatwaves_boots", "heatwave");

        put(0, "iron_man_helmet", "iron_man");
        put(0, "iron_mans_helmet", "iron_man");
        put(1, "iron_man_chestplate", "iron_man");
        put(1, "iron_mans_chestplate", "iron_man");
        put(2, "iron_man_leggings", "iron_man");
        put(2, "iron_mans_leggings", "iron_man");
        put(3, "iron_man_boots", "iron_man");
        put(3, "iron_mans_boots", "iron_man");

        put(0, "kid_flashs_mask", "kid_flash");
        put(1, "kid_flashs_chestpiece", "kid_flash");
        put(2, "kid_flashs_pants", "kid_flash");
        put(3, "kid_flashs_boots", "kid_flash");

        put(0, "killer_frosts_hair", "killer_frost");
        put(1, "killer_frosts_jacket", "killer_frost");
        put(2, "killer_frosts_pants", "killer_frost");
        put(3, "killer_frosts_boots", "killer_frost");

        put(0, "martian_manhunters_head", "martian_manhunter");
        put(1, "martian_manhunters_torso", "martian_manhunter");
        put(2, "martian_manhunters_legs", "martian_manhunter");
        put(3, "martian_manhunters_boots", "martian_manhunter");

        put(0, "martian_manhunter_comics_head", "martian_manhunter_comics");
        put(0, "martian_manhunters_head_comics", "martian_manhunter_comics");
        put(1, "martian_manhunter_comics_torso", "martian_manhunter_comics");
        put(1, "martian_manhunters_torso_comics", "martian_manhunter_comics");
        put(2, "martian_manhunter_comics_legs", "martian_manhunter_comics");
        put(2, "martian_manhunters_legs_comics", "martian_manhunter_comics");
        put(3, "martian_manhunter_comics_boots", "martian_manhunter_comics");
        put(3, "martian_manhunters_boots_comics", "martian_manhunter_comics");

        put(0, "prometheus_hood", "prometheus");
        put(1, "prometheus_chestpiece", "prometheus");
        put(2, "prometheus_pants", "prometheus");
        put(3, "prometheus_boots", "prometheus");

        put(0, "reverse_flashs_mask", "reverse_flash");
        put(1, "reverse_flashs_chestpiece", "reverse_flash");
        put(2, "reverse_flashs_pants", "reverse_flash");
        put(3, "reverse_flashs_boots", "reverse_flash");

        put(1, "rip_hunters_trenchcoat", "rip_hunter");
        put(2, "rip_hunters_pants", "rip_hunter");
        put(3, "rip_hunters_boots", "rip_hunter");

        put(0, "senor_cactus_hat", "senor_cactus");
        put(1, "senor_cactus_chestpiece", "senor_cactus");
        put(2, "senor_cactus_pants", "senor_cactus");
        put(3, "senor_cactus_boots", "senor_cactus");

        put(0, "the_flashs_mask", "the_flash");
        put(1, "the_flashs_chestpiece", "the_flash");
        put(2, "the_flashs_pants", "the_flash");
        put(3, "the_flashs_boots", "the_flash");

        put(0, "the_flash_hunters_helmet", "the_flash_hunter");
        put(0, "the_flashs_helmet_hunter", "the_flash_hunter");
        put(1, "the_flash_hunters_chestpiece", "the_flash_hunter");
        put(1, "the_flashs_chestpiece_hunter", "the_flash_hunter");
        put(2, "the_flash_hunters_pants", "the_flash_hunter");
        put(2, "the_flashs_pants_hunter", "the_flash_hunter");
        put(3, "the_flash_hunters_boots", "the_flash_hunter");
        put(3, "the_flashs_boots_hunter", "the_flash_hunter");

        put(0, "the_flash_jays_helmet", "the_flash_jay");
        put(0, "the_flashs_helmet_jay", "the_flash_jay");
        put(1, "the_flash_jays_chestpiece", "the_flash_jay");
        put(1, "the_flashs_chestpiece_jay", "the_flash_jay");
        put(2, "the_flash_jays_pants", "the_flash_jay");
        put(2, "the_flashs_pants_jay", "the_flash_jay");
        put(3, "the_flash_jays_boots", "the_flash_jay");
        put(3, "the_flashs_boots_jay", "the_flash_jay");

        put(0, "savitars_mask", "the_rival");
        put(0, "the_rivals_mask", "the_rival");
        put(1, "savitars_chestpiece", "the_rival");
        put(1, "the_rivals_chestpiece", "the_rival");
        put(2, "savitars_pants", "the_rival");
        put(2, "the_rivals_pants", "the_rival");
        put(3, "savitars_boots", "the_rival");
        put(3, "the_rivals_boots", "the_rival");

        put(0, "trajectorys_mask", "trajectory");
        put(1, "trajectorys_chestpiece", "trajectory");
        put(2, "trajectorys_pants", "trajectory");
        put(3, "trajectorys_boots", "trajectory");

        put(0, "zooms_mask", "zoom");
        put(1, "zooms_chestpiece", "zoom");
        put(2, "zooms_pants", "zoom");
        put(3, "zooms_boots", "zoom");

        put(0, "spodermens_mask", "spodermen");
        put(1, "spodermens_chestpiece", "spodermen");
        put(2, "spodermens_pants", "spodermen");
        put(3, "spodermens_boots", "spodermen");
    }

    private void put(int armorType, String oldName, String id)
    {
        map.put("fiskheroes:" + oldName, Pair.of(FiskHeroes.MODID + ":" + id, armorType));
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        if (!dirty)
        {
            idMap.clear();
        }

        if (nbt.hasKey("LegacyMappings", NBT.TAG_LIST))
        {
            NBTTagList list = nbt.getTagList("LegacyMappings", NBT.TAG_COMPOUND);

            for (int i = 0; i < list.tagCount(); ++i)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                idMap.put(tag.getInteger("K"), Pair.of(tag.getString("V"), (int) tag.getByte("i")));
            }
        }

        dirty = false;
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        if (!idMap.isEmpty())
        {
            NBTTagList list = new NBTTagList();

            for (Map.Entry<Integer, Pair<String, Integer>> e : idMap.entrySet())
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("i", e.getValue().getValue().byteValue());
                tag.setString("V", e.getValue().getKey());
                tag.setInteger("K", e.getKey());
                list.appendTag(tag);
            }

            nbt.setTag("LegacyMappings", list);
        }
    }

    public void missingMappings(FMLMissingMappingsEvent event)
    {
        idMap.clear();

        for (MissingMapping mapping : event.get())
        {
            if (mapping.type == GameRegistry.Type.ITEM && mapping.name.startsWith("fiskheroes:"))
            {
                Pair<String, Integer> p = map.get(mapping.name);

                if (p != null)
                {
                    idMap.put(mapping.id, p);
                    mapping.ignore();
                    dirty = true;

                    JSHeroesEngine.LOGGER.info("Remapping Hero armor {} -> {}@{}", mapping.name, p.getKey(), p.getValue());
                }
            }
        }
    }

    public static Pair<String, Integer> fromId(int id)
    {
        return INSTANCE.idMap.get(id);
    }
}
