function init(hero) {
    hero.setName("hero.fiskheroes.colossus_xmen.name");
    hero.setVersion("item.superhero_armor.version.xmen");
    hero.setTier(4);
    
    hero.setHelmet("item.superhero_armor.piece.head");
    hero.setChestplate("item.superhero_armor.piece.torso");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:superhuman_durability", "fiskheroes:fire_immunity", "fiskheroes:leaping");
    hero.addWeaknesses("fiskheroes:metal_skin");
    hero.addAttribute("ATTACK_DAMAGE", 0.5, 1);
    hero.addAttribute("FALL_RESISTANCE", 10.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 13.0, 0);
    hero.addAttribute("STEP_HEIGHT", 0.5, 0);
    hero.addAttribute("SWORD_DAMAGE", -0.4, 1);
    
    hero.setDefaultScale(1.5);
}
