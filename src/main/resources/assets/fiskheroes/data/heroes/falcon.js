function init(hero) {
    hero.setName("hero.fiskheroes.falcon.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.goggles");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:gliding_flight", "fiskheroes:retractable_shield");
    hero.addAttribute("ATTACK_DAMAGE", 0.5, 1);
    hero.addAttribute("FALL_RESISTANCE", 5.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 4.0, 0);
    
    hero.addKeyBind("SHIELD", "key.shield", 1);
    
    hero.setModifierEnabled(isModifierEnabled);
    hero.supplyFunction("isToggleShield", false);
}

function isModifierEnabled(entity, modifier) {
    return modifier != "fiskheroes:retractable_shield" || !entity.getData("fiskheroes:gliding");
}
