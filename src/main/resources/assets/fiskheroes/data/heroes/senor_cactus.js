function init(hero) {
    hero.setName("hero.fiskheroes.senor_cactus.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.hat");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:cactus_physiology", "fiskheroes:cellular_regeneration", "fiskheroes:heat_resistance");
    hero.addAttribute("FALL_RESISTANCE", 10.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 4.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.2, 1);
    hero.addAttribute("STEP_HEIGHT", 0.5, 0);
    
    hero.addKeyBind("AIM", "key.point", 1);
    hero.addKeyBind("SHOOT_SPIKES", "key.shootSpikes", 2);
    
    hero.setHasProperty(hasProperty);
    hero.supplyFunction("canAim", canAim);
}

function hasProperty(entity, property) {
    return property == "BREATHE_UNDERWATER";
}

function canAim(entity) {
    return entity.getHeldItem().isEmpty();
}
