function init(hero) {
    hero.setName("hero.fiskheroes.black_panther.name");
    hero.setTier(4);
    
    hero.setHelmet("item.superhero_armor.piece.helmet");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:superhuman_durability", "fiskheroes:leaping");
    hero.addAttribute("ATTACK_DAMAGE", 0.3, 1);
    hero.addAttribute("FALL_RESISTANCE", 1.0, 1);
    hero.addAttribute("JUMP_HEIGHT", 2.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 11.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.6, 1);
    hero.addAttribute("STEP_HEIGHT", 0.5, 0);
}
