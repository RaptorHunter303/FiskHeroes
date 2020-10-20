function init(hero) {
    hero.setName("hero.fiskheroes.martian_manhunter_comics.name");
    hero.setVersion("item.superhero_armor.version.comics");
    hero.setTier(5);
    
    hero.setHelmet("item.superhero_armor.piece.head");
    hero.setChestplate("item.superhero_armor.piece.torso");
    hero.setLeggings("item.superhero_armor.piece.legs");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:shape_shifting", "fiskheroes:intangibility", "fiskheroes:invisibility", "fiskheroes:flight", "fiskheroes:heat_vision");
    hero.addWeaknesses("fiskheroes:fire");
    hero.addAttribute("ATTACK_DAMAGE", 2.1, 1);
    hero.addAttribute("PUNCH_DAMAGE", 12.0, 0);
    hero.addAttribute("SWORD_DAMAGE", -0.45, 1);
    
    hero.addKeyBind("SHAPE_SHIFT", "key.shapeShift", 1);
    hero.addKeyBind("SHAPE_SHIFT_RESET", "key.shapeShiftReset", 2);
    hero.addKeyBind("INTANGIBILITY", "key.intangibility", 3);
    hero.addKeyBind("INVISIBILITY", "key.invisibility", 4);
    hero.addKeyBind("HEAT_VISION", "key.heatVision", 5);
    
    hero.setDefaultScale(1.1);
    hero.setHasProperty(hasProperty);
}

function hasProperty(entity, property) {
    return property == "BREATHE_UNDERWATER" || property == "BREATHE_SPACE";
}
