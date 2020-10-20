function init(hero) {
    hero.setName("hero.fiskheroes.the_monitor.name");
    hero.setCosmic();
    hero.setTier(6);
    
    hero.setChestplate("item.superhero_armor.piece.chestplate");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:superhuman_durability", "fiskheroes:enhanced_reflexes", "fiskheroes:teleportation", "fiskheroes:energy_projection");
    hero.addAttribute("ATTACK_DAMAGE", 2.35, 1);
    hero.addAttribute("FALL_RESISTANCE", 10.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 2.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 13.5, 0);
    hero.addAttribute("SWORD_DAMAGE", -0.75, 1);
    
    hero.addKeyBind("ENERGY_PROJECTION", "key.energyProjection", 1);
    hero.addKeyBind("TELEPORT", "key.teleport", 2);
    
    hero.setDefaultScale(1.1);
    hero.setHasProperty(hasProperty);
}

function hasProperty(entity, property) {
    return property == "BREATHE_UNDERWATER" || property == "BREATHE_SPACE";
}
