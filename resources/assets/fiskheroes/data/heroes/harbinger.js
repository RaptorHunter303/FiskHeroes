function init(hero) {
    hero.setName("hero.fiskheroes.harbinger.name");
    hero.setCosmic();
    hero.setTier(5);
    
    hero.setChestplate("item.superhero_armor.piece.tunic");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:superhuman_durability", "fiskheroes:teleportation", "fiskheroes:energy_projection");
    hero.addAttribute("ATTACK_DAMAGE", 2.15, 1);
    hero.addAttribute("FALL_RESISTANCE", 14.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 2.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 12.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.15, 1);
    hero.addAttribute("SWORD_DAMAGE", -0.45, 1);
    
    hero.addKeyBind("ENERGY_PROJECTION", "key.energyProjection", 1);
    hero.addKeyBind("TELEPORT", "key.teleport", 2);
    
    hero.setHasProperty(hasProperty);
}

function hasProperty(entity, property) {
    return property == "BREATHE_SPACE";
}
