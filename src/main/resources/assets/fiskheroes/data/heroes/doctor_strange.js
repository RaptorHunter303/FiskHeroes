function init(hero) {
    hero.setName("hero.fiskheroes.doctor_strange.name");
    hero.setTier(2);
    
    hero.addAbilities("fiskheroes:flight", "fiskheroes:spellcasting", "fiskheroes:retractable_shield", "fiskheroes:retractable_blade", "fiskheroes:hover");
    hero.setChestplate("item.superhero_armor.piece.robes");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAttribute("PUNCH_DAMAGE", -1.0, 0);
    hero.addAttribute("SWORD_DAMAGE", 2.5, 0);
    
    hero.addKeyBind("AIM", "key.aim", -1);
    hero.addKeyBind("BLADE", "key.blade", 1);
    hero.addKeyBind("SHIELD", "key.shield", 1);
    hero.addKeyBind("SPELL_MENU", "key.spellMenu", 2);
    hero.addKeyBind("HOVER", "key.levitate", 3);
    
    hero.addAttributeProfile("BLADE", bladeProfile);
    hero.setAttributeProfile(getAttributeProfile);
    hero.setModifierEnabled(isModifierEnabled);
    hero.setKeyBindEnabled(isKeyBindEnabled);
    hero.supplyFunction("getSpells", getSpells);
    hero.supplyFunction("canAim", canAim);
}

function bladeProfile(profile) {
    profile.addAttribute("PUNCH_DAMAGE", 9.0, 0);
    profile.addAttribute("SWORD_DAMAGE", 2.5, 0);
}

function getAttributeProfile(entity) {
    return entity.getData("fiskheroes:blade") ? "BLADE" : null;
}

function isModifierEnabled(entity, modifier) {
    switch (modifier) {
    case "fiskheroes:retractable_shield":
        return !entity.getData("fiskheroes:blade");
    case "fiskheroes:spellcasting":
        return !entity.getData("fiskheroes:blade") && !entity.getData("fiskheroes:shield");
    default:
        return true;
    }
}

function isKeyBindEnabled(entity, keyBind) {
    switch (keyBind) {
    case "SHIELD":
        return !entity.getData("fiskheroes:shield") || !(entity.getData("fiskheroes:shield") && entity.getData("fiskheroes:aiming"));
    case "BLADE":
        return entity.getData("fiskheroes:shield") && entity.getData("fiskheroes:aiming") || entity.getData("fiskheroes:blade") || entity.isBookPlayer();
    default:
        return true;
    }
}

function getSpells(entity) {
    if (entity.getHeldItem().isEmpty()) {
        return ["fiskheroes:whip", "fiskheroes:atmospheric", "fiskheroes:earth_swallowing", "fiskheroes:duplication"];
    }
    
    return ["fiskheroes:atmospheric", "fiskheroes:earth_swallowing", "fiskheroes:duplication"];
}

function canAim(entity) {
    return entity.getData("fiskheroes:shield") && entity.getData("fiskheroes:shield_blocking_timer") == 0;
}
