function init(hero) {
    hero.setName("hero.fiskheroes.atom.name");
    hero.setTier(4);
    
    hero.setHelmet("item.superhero_armor.piece.helmet");
    hero.setChestplate("item.superhero_armor.piece.chestplate");
    hero.setLeggings("item.superhero_armor.piece.leggings");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:propelled_flight", "fiskheroes:size_manipulation", "fiskheroes:hover");
    hero.addAttribute("ATTACK_DAMAGE", 0.9, 1);
    hero.addAttribute("PUNCH_DAMAGE", 6.5, 0);
    hero.addAttribute("SWORD_DAMAGE", -2.0, 0);
    
    hero.addKeyBind("SHRINK", "key.shrink", 1);
    hero.addKeyBind("GROW", "key.grow", 2);
    hero.addKeyBind("HOVER", "key.hover", 4);
    hero.addKeyBind("MINIATURIZE_SUIT", "key.miniaturizeSuit", 3);
    
    hero.setHasProperty(hasProperty);
    hero.supplyFunction("getMinSize", 0.0625);
    hero.supplyFunction("getMaxSize", 1.0);
    hero.supplyFunction("isInstant", true);
}

function hasProperty(entity, property) {
    switch (property) {
    case "MASK_TOGGLE":
        return true;
    case "BREATHE_UNDERWATER":
        return entity.getData("fiskheroes:mask_open");
    case "BREATHE_SPACE":
        return entity.getData("fiskheroes:mask_open");
    default:
        return false;
    }
}
