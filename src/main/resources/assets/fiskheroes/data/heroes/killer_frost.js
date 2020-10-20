function init(hero) {
    hero.setName("hero.fiskheroes.killer_frost.name");
    hero.setTier(2);
    
    hero.setHelmet("item.superhero_armor.piece.hair");
    hero.setChestplate("item.superhero_armor.piece.jacket");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:cryokinesis");
    hero.addWeaknesses("fiskheroes:heat");
    hero.addAttribute("FALL_RESISTANCE", 3.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 2.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.2, 1);
    
    hero.addKeyBind("CHARGE_ICE", "key.chargeIce", 1);
}
