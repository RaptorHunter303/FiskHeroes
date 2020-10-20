function init(hero) {
    hero.setName("hero.fiskheroes.anti_monitor.name");
    hero.setCosmic();
    hero.setTier(6);
    
    hero.setHelmet("item.superhero_armor.piece.head");
    hero.setChestplate("item.superhero_armor.piece.chestplate");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:superhuman_durability", "fiskheroes:enhanced_reflexes", "fiskheroes:teleportation", "fiskheroes:energy_projection", "fiskheroes:size_manipulation", "fiskheroes:leaping");
    hero.addAttribute("ATTACK_DAMAGE", 2.45, 1);
    hero.addAttribute("FALL_RESISTANCE", 10.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 2.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 13.5, 0);
    hero.addAttribute("SWORD_DAMAGE", -0.8, 1);
    
    hero.addKeyBind("ENERGY_PROJECTION", "key.energyProjection", 1);
    hero.addKeyBind("TELEPORT", "key.teleport", 2);
    hero.addKeyBind("SHRINK", "key.shrink", 3);
    hero.addKeyBind("GROW", "key.grow", 4);
    
    hero.setDefaultScale(1.1);
    hero.setHasProperty(hasProperty);
    hero.setModifierEnabled(isModifierEnabled);
    hero.supplyFunction("getMinSize", 1.1);
    hero.supplyFunction("getMaxSize", 8.0);
    hero.supplyFunction("isInstant", false);
}

function hasProperty(entity, property) {
    return property == "BREATHE_UNDERWATER" || property == "BREATHE_SPACE";
}

function isModifierEnabled(entity, modifier) {
    return entity.getData("fiskheroes:scale") <= 1.11 || modifier != "fiskheroes:teleportation" && modifier != "fiskheroes:energy_projection";
}
