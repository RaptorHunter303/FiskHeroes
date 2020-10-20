function init(hero) {
    hero.setName("hero.fiskheroes.firestorm_jax.name");
    hero.setTier(3);
    
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:pyrokinesis", "fiskheroes:propelled_flight", "fiskheroes:fire_immunity");
    hero.addWeaknesses("fiskheroes:cold");
    hero.addAttribute("PUNCH_DAMAGE", 3.0, 0);
    
    hero.addKeyBind("AIM", "key.shoot", 1);
    
    hero.setHasProperty(hasProperty);
    hero.setOnToggleMask(onToggleMask);
    hero.supplyFunction("canAim", canAim);
}

function hasProperty(entity, property) {
    return property == "MASK_TOGGLE";
}

function onToggleMask(entity, state) {
    if (state) {
        entity.playSound("fiskheroes:random.fire.off", 1.0, 1.0);
    }
    else {
        entity.playSound("fiskheroes:random.fire.on", 1.0, 1.0);
    }
}

function canAim(entity) {
    return entity.getHeldItem().isEmpty();
}
