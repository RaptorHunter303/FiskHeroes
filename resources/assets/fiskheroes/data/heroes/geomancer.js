function init(hero) {
    hero.setName("hero.fiskheroes.geomancer.name");
    hero.setTier(2);
    
    hero.setHelmet("item.superhero_armor.piece.goggles");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:geokinesis");
    hero.addAttribute("ATTACK_DAMAGE", 0.4, 1);
    hero.addAttribute("FALL_RESISTANCE", 3.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 4.0, 0);
    
    hero.addKeyBind("EARTHQUAKE", "key.earthquake", 1);
    hero.addKeyBind("GROUND_SMASH", "key.groundSmash", 2);
}
