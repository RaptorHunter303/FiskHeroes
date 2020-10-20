function init(hero) {
    hero.setName("hero.fiskheroes.the_tick.name");
    hero.setTier(5);
    
    hero.setHelmet("item.superhero_armor.piece.cowl");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:superhuman_durability", "fiskheroes:explosion_immunity", "fiskheroes:fire_immunity", "fiskheroes:leaping");
    hero.addAttribute("ATTACK_DAMAGE", 0.85, 1);
    hero.addAttribute("FALL_RESISTANCE", 1.0, 1);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 14.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.2, 1);
    hero.addAttribute("STEP_HEIGHT", 0.5, 0);
    hero.addAttribute("SWORD_DAMAGE", -0.55, 1);
    
    hero.setDefaultScale(1.1);
}
