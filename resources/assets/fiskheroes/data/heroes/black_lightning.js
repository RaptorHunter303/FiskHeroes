function init(hero) {
    hero.setName("hero.fiskheroes.black_lightning.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.mask");
    hero.setChestplate("item.superhero_armor.piece.chestplate");
    hero.setLeggings("item.superhero_armor.piece.leggings");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:electrokinesis", "fiskheroes:propelled_flight", "fiskheroes:superhuman_durability", "fiskheroes:hover");
    hero.addAttribute("ATTACK_DAMAGE", 0.2, 1);
    hero.addAttribute("PUNCH_DAMAGE", 6.5, 0);
    hero.addAttribute("SWORD_DAMAGE", 2.0, 0);
    
    // hero.addKeyBind("AIM", "key.aim", 1); TODO: Re-implement
    hero.addKeyBind("HOVER", "key.hover", 2);

    hero.setHasProperty(hasProperty);
//    hero.supplyFunction("canAim", canAim);
}

function hasProperty(entity, property) {
    return property == "MASK_TOGGLE";
}

//function canAim(entity) {
//    return entity.getHeldItem() == null;
//}
