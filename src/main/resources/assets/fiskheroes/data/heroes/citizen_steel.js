function init(hero) {
    hero.setName("hero.fiskheroes.citizen_steel.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.helmet");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:steel_transformation", "fiskheroes:cellular_regeneration", "fiskheroes:superhuman_durability", "fiskheroes:fire_immunity");
    hero.addWeaknesses("fiskheroes:metal_skin");
    hero.addAttribute("FALL_RESISTANCE", 5.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 0.5, 0);
    hero.addAttribute("PUNCH_DAMAGE", 4.0, 0);
    
    hero.addKeyBind("STEEL_TRANSFORM", "key.steelTransform", 1);
    
    hero.addAttributeProfile("STEEL", steelProfile);
    hero.setAttributeProfile(getAttributeProfile);
    hero.setModifierEnabled(isModifierEnabled);
}

function steelProfile(profile) {
    profile.addAttribute("ATTACK_DAMAGE", 0.6, 1);
    profile.addAttribute("FALL_RESISTANCE", 11.5, 0);
    profile.addAttribute("JUMP_HEIGHT", 1.0, 0);
    profile.addAttribute("PUNCH_DAMAGE", 11.5, 0);
    profile.addAttribute("SPRINT_SPEED", 0.1, 1);
    profile.addAttribute("SWORD_DAMAGE", -0.2, 1);
}

function getAttributeProfile(entity) {
    return entity.getData("fiskheroes:steeled") ? "STEEL" : null;
}

function isModifierEnabled(entity, modifier) {
    return entity.getData("fiskheroes:steeled") || modifier != "fiskheroes:superhuman_durability" && modifier != "fiskheroes:fire_immunity" && modifier != "fiskheroes:metal_skin";
}
