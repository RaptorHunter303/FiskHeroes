function init(hero) {
    hero.setName("hero.fiskheroes.spodermen.name");
    hero.setTier(1);
    hero.hide();
    
    hero.setHelmet("item.superhero_armor.piece.mask");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAttribute("FALL_RESISTANCE", 12.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 4.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 3.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.75, 1);
}
