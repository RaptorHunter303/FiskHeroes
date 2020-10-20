function init(hero) {
    hero.setName("hero.fiskheroes.iron_man.name");
    hero.setTier(4);
    
    hero.setHelmet("item.superhero_armor.piece.helmet");
    hero.setChestplate("item.superhero_armor.piece.chestplate");
    hero.setLeggings("item.superhero_armor.piece.leggings");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:fire_immunity", "fiskheroes:propelled_flight", "fiskheroes:repulsor_blast", "fiskheroes:hover", "fiskheroes:sentry_mode");
    hero.addWeaknesses("fiskheroes:metal_skin");
    hero.addAttribute("ATTACK_DAMAGE", 1.2, 1);
    hero.addAttribute("PUNCH_DAMAGE", 6.5, 0);
    hero.addAttribute("SWORD_DAMAGE", -2.0, 0);
    
    hero.addKeyBind("AIM", "key.aim", 1);
    hero.addKeyBind("HOVER", "key.hover", 2);
    hero.addKeyBind("SENTRY_MODE", "key.sentryMode", 3);
    
    hero.setHasProperty(hasProperty);
    hero.supplyFunction("canAim", canAim);
}

function hasProperty(entity, property) {
    switch (property) {
    case "MASK_TOGGLE":
        return true;
    case "BREATHE_UNDERWATER":
        return !entity.getData("fiskheroes:mask_open");
    case "BREATHE_SPACE":
        return !entity.getData("fiskheroes:mask_open");
    default:
        return false;
    }
}

function canAim(entity) {
    return entity.getHeldItem().isEmpty() && !entity.getData("fiskheroes:jetpacking");
}
