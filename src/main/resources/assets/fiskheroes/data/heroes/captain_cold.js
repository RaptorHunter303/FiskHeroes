function init(hero) {
    hero.setName("hero.fiskheroes.captain_cold.name");
    hero.setTier(1);
    
    hero.setHelmet("item.superhero_armor.piece.goggles");
    hero.setChestplate("item.superhero_armor.piece.jacket");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:cold_gun");
    
    hero.addAbilities("fiskheroes:cold_resistance");
    hero.addAttribute("FALL_RESISTANCE", 2.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 4.0, 0);
    
    hero.addKeyBind("AIM", "key.aim", -1);
    
    hero.setHasPermission(hasPermission);
    hero.supplyFunction("canAim", canAim);
}

function hasPermission(entity, permission) {
    return permission == "USE_COLD_GUN";
}

function canAim(entity) {
    return entity.getHeldItem().name() == "fiskheroes:cold_gun";
}
