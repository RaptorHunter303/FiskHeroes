function init(hero) {
    hero.setName("hero.fiskheroes.guardian.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.helmet");
    hero.setChestplate("item.superhero_armor.piece.chestplate");
    hero.setLeggings("item.superhero_armor.piece.leggings");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:retractable_shield");
    hero.addAttribute("FALL_RESISTANCE", 5.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 3.5, 0);
    hero.addAttribute("SPRINT_SPEED", 0.1, 1);
    
    hero.addKeyBind("SHIELD", "key.shield", 1);
    
    hero.addAttributeProfile("SHIELD", shieldProfile);
    hero.setAttributeProfile(getAttributeProfile);
}

function shieldProfile(profile) {
    profile.addAttribute("ATTACK_DAMAGE", 5.0, 0);
    profile.addAttribute("FALL_RESISTANCE", 5.0, 0);
    profile.addAttribute("JUMP_HEIGHT", 1.0, 0);
    profile.addAttribute("PUNCH_DAMAGE", 3.5, 0);
    profile.addAttribute("SPRINT_SPEED", 0.1, 1);
}

function getAttributeProfile(entity) {
    return entity.getData("fiskheroes:shield") ? "SHIELD" : null;
}
