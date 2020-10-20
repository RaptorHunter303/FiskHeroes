function init(hero) {
    hero.setName("hero.fiskheroes.atom_smasher.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.helmet");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:size_manipulation", "fiskheroes:superhuman_durability", "fiskheroes:leaping");
    hero.addAttribute("FALL_RESISTANCE", 10.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 9.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.2, 1);
    hero.addAttribute("SWORD_DAMAGE", -0.4, 1);
    
    hero.addKeyBind("SHRINK", "key.shrink", 1);
    hero.addKeyBind("GROW", "key.grow", 2);
    
    hero.setDefaultScale(1.1);
    hero.setHasProperty(hasProperty);
    hero.supplyFunction("getMinSize", 1.1);
    hero.supplyFunction("getMaxSize", 3.0);
    hero.supplyFunction("isInstant", false);
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
