function init(hero) {
    hero.setName("hero.fiskheroes.shazam_dceu.name");
    hero.setVersion("item.superhero_armor.version.dceu");
    hero.setTier(5);
    
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:electrokinesis", "fiskheroes:flight", "fiskheroes:superhuman_durability", "fiskheroes:super_speed", "fiskheroes:enhanced_reflexes", "fiskheroes:fire_immunity");
    hero.addWeaknesses("fiskheroes:eternium");
    hero.addAttribute("ATTACK_DAMAGE", 1.95, 1);
    hero.addAttribute("BASE_SPEED_LEVELS", 2.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 9.5, 0);
    hero.addAttribute("SPRINT_SPEED", 0.65, 1);
    hero.addAttribute("SWORD_DAMAGE", -0.6, 1);
    
    hero.addKeyBind("SUPER_SPEED", "key.superSpeed", 1);
    hero.addKeyBind("DECELERATE", "key.decelerate", 3);
    hero.addKeyBind("ACCELERATE", "key.accelerate", 4);
    
    hero.setDefaultScale(1.1);
    hero.setModifierEnabled(isModifierEnabled);
}

function isModifierEnabled(entity, modifier) {
    return modifier == "fiskheroes:eternium" || !entity.hasStatusEffect("fiskheroes:eternium");
}
