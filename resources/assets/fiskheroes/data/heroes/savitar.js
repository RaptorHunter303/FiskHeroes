function init(hero) {
    hero.setName("hero.fiskheroes.savitar.name");
    hero.setTier(4);
    
    hero.setHelmet("item.superhero_armor.piece.helmet");
    hero.setChestplate("item.superhero_armor.piece.chestplate");
    hero.setLeggings("item.superhero_armor.piece.leggings");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:flash_ring");
    
    hero.addAbilities("fiskheroes:super_speed", "fiskheroes:accelerated_perception", "fiskheroes:superhuman_durability", "fiskheroes:cold_resistance", "fiskheroes:fire_immunity");
    hero.addWeaknesses("fiskheroes:metal_skin");
    hero.addAttribute("ATTACK_DAMAGE", 0.15, 1);
    hero.addAttribute("BASE_SPEED_LEVELS", 7.0, 0);
    hero.addAttribute("FALL_RESISTANCE", 7.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.5, 0);
    hero.addAttribute("PUNCH_DAMAGE", 6.0, 0);
    
    hero.addKeyBind("SUPER_SPEED", "key.superSpeed", 1);
    hero.addKeyBind("SLOW_MOTION", "key.slowMotion", 2);
    hero.addKeyBind("DECELERATE", "key.decelerate", 3);
    hero.addKeyBind("ACCELERATE", "key.accelerate", 4);
    
    hero.setDefaultScale(1.2);
    hero.setHasProperty(hasProperty);
}

function hasProperty(entity, property) {
    return property == "BREATHE_SPACE";
}
