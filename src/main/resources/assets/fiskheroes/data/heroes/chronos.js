function init(hero) {
    hero.setName("hero.fiskheroes.chronos.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.helmet");
    hero.setChestplate("item.superhero_armor.piece.chestplate");
    hero.setLeggings("item.superhero_armor.piece.leggings");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:chronos_rifle");
    
    hero.addAttribute("FALL_RESISTANCE", 5.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 0.5, 0);
    hero.addAttribute("PUNCH_DAMAGE", 4.5, 0);
    
    hero.addKeyBind("AIM", "key.aim", -1);
    
    hero.setHasPermission(hasPermission);
    hero.supplyFunction("canAim", canAim);
}

function hasPermission(entity, permission) {
    return permission == "USE_CHRONOS_RIFLE";
}

function canAim(entity) {
    return entity.getHeldItem().name() == "fiskheroes:chronos_rifle";
}
