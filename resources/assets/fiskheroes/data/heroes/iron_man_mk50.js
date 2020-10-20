function init(hero) {
    hero.setName("hero.fiskheroes.iron_man_mk50.name");
    hero.setTier(5);
    
    hero.setChestplate("item.superhero_armor.piece.arc_reactor");
    
    hero.addAbilities("fiskheroes:fire_immunity", "fiskheroes:cellular_regeneration", "fiskheroes:propelled_flight", "fiskheroes:energy_blast", "fiskheroes:retractable_blade", "fiskheroes:retractable_shield", "fiskheroes:hover");
    hero.addWeaknesses("fiskheroes:metal_skin");
    hero.addAttribute("ATTACK_DAMAGE", 1.6, 1);
    hero.addAttribute("PUNCH_DAMAGE", 9.0, 0);
    hero.addAttribute("SWORD_DAMAGE", -0.5, 1);
    
    hero.addKeyBind("AIM", "key.aim", 1);
    hero.addKeyBind("BLADE", "key.blade", 2);
    hero.addKeyBind("SHIELD", "key.shield", 2);
    hero.addKeyBind("HOVER", "key.hover", 3);
    hero.addKeyBind("TRANSFORM", "key.naniteTransform", 5);
    
    hero.addAttributeProfile("INACTIVE", inactiveProfile);
    hero.addAttributeProfile("BLADE", bladeProfile);
    hero.setAttributeProfile(getAttributeProfile);
    hero.setModifierEnabled(isModifierEnabled);
    hero.setKeyBindEnabled(isKeyBindEnabled);
    hero.setHasProperty(hasProperty);
    hero.setTierOverride(getTierOverride);
    hero.supplyFunction("canAim", canAim);
    hero.supplyFunction("getTransformTimerTicks", 4200);
    hero.supplyFunction("getTransformTimerRegen", 2.0);
}

function getTierOverride(entity) {
    return entity.getData("fiskheroes:transformed") ? 5 : 0;
}

function inactiveProfile(profile) {
}

function bladeProfile(profile) {
    profile.addAttribute("ATTACK_DAMAGE", 1.6, 1);
    profile.addAttribute("JUMP_HEIGHT", 1.5, 0);
    profile.addAttribute("PUNCH_DAMAGE", 13.0, 0);
    profile.addAttribute("SPRINT_SPEED", 0.4, 1);
    profile.addAttribute("SWORD_DAMAGE", -0.5, 1);
}

function getAttributeProfile(entity) {
    if (!entity.getData("fiskheroes:transformed")) {
        return "INACTIVE";
    }
    
    return entity.getData("fiskheroes:blade") ? "BLADE" : null;
}

function isModifierEnabled(entity, modifier) {
    if (!entity.getData("fiskheroes:transformed")) {
        return false;
    }
  
    switch (modifier) {
    case "fiskheroes:energy_blast":
        return entity.getData("fiskheroes:aimed_timer") >= 1;
    case "fiskheroes:retractable_blade":
        return !entity.getData("fiskheroes:aiming") && !(entity.getData("fiskheroes:shield") && entity.getData("fiskheroes:blade"));
    case "fiskheroes:retractable_shield":
        return !entity.getData("fiskheroes:aiming") && !(entity.getData("fiskheroes:shield") && entity.getData("fiskheroes:blade"));
    case "fiskheroes:cellular_regeneration":
        return !entity.getData("fiskheroes:blade");
    default:
        return true;
    }
}

function isKeyBindEnabled(entity, keyBind) {
    switch (keyBind) {
    case "TRANSFORM":
        return entity.getData("fiskheroes:mask_open_timer") == 0;
    case "SHIELD":
        return entity.getData("fiskheroes:blade_timer") == 0 || entity.isBookPlayer();
    case "BLADE":
        return entity.getData("fiskheroes:shield_timer") > 0 || entity.getData("fiskheroes:blade_timer") > 0 || entity.isBookPlayer();
    default:
        return entity.getData("fiskheroes:transformed");
    }
}

function hasProperty(entity, property) {
    switch (property) {
    case "MASK_TOGGLE":
        return entity.getData("fiskheroes:transform_timer") == 1;
    case "BREATHE_UNDERWATER":
        return !entity.getData("fiskheroes:mask_open") && entity.getData("fiskheroes:transformed");
    case "BREATHE_SPACE":
        return !entity.getData("fiskheroes:mask_open") && entity.getData("fiskheroes:transformed");
    default:
        return false;
    }
}

function canAim(entity) {
    return entity.getHeldItem().isEmpty() && !entity.getData("fiskheroes:jetpacking") && entity.getData("fiskheroes:transformed");
}
