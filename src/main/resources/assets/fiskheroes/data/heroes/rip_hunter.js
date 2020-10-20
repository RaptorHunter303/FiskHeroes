function init(hero) {
    hero.setName("hero.fiskheroes.rip_hunter.name");
    hero.setTier(1);
    
    hero.setChestplate("item.superhero_armor.piece.trenchcoat");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:rip_hunters_gun");
    
    hero.addAttribute("ATTACK_DAMAGE", 0.2, 1);
    hero.addAttribute("FALL_RESISTANCE", 3.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 3.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.1, 1);
    
    hero.addKeyBind("AIM", "key.aim", -1);
    
    hero.setHasPermission(hasPermission);
    hero.supplyFunction("canAim", canAim);
}

function hasPermission(entity, permission) {
    return permission == "USE_RIPS_GUN";
}

function canAim(entity) {
    return entity.getHeldItem().name() == "fiskheroes:rip_hunters_gun";
}
