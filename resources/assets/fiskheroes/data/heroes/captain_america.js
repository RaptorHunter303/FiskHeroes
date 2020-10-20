function init(hero) {
    hero.setName("hero.fiskheroes.captain_america.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.helmet");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:captain_americas_shield");
    
    hero.addAbilities("fiskheroes:cellular_regeneration", "fiskheroes:enhanced_reflexes");
    hero.addAttribute("ATTACK_DAMAGE", 0.9, 1);
    hero.addAttribute("FALL_RESISTANCE", 7.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 3.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 5.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.4, 1);
    
    hero.setHasPermission(hasPermission);
}

function hasPermission(entity, permission) {
    return permission == "THROW_SHIELD" || permission == "USE_SHIELD";
}
