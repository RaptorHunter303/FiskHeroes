function init(hero) {
    hero.setName("hero.fiskheroes.batman_dceu.name");
    hero.setVersion("item.superhero_armor.version.dceu");
    hero.setTier(4);
    
    hero.setHelmet("item.superhero_armor.piece.cowl");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:grappling_gun");
    
    hero.addAbilities("fiskheroes:utility_belt", "fiskheroes:gliding");
    hero.addAttribute("ATTACK_DAMAGE", 0.6, 1);
    hero.addAttribute("FALL_RESISTANCE", 5.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.5, 0);
    hero.addAttribute("PUNCH_DAMAGE", 8.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.3, 1);
    
    hero.addKeyBind("UTILITY_SWITCH", "key.utilitySwitch", 1);
    hero.addKeyBind("UTILITY_RESET", "key.utilityReset", 2);
    
    hero.setHasPermission(hasPermission);
}

function hasPermission(entity, permission) {
    return permission == "USE_GRAPPLING_GUN";
}
