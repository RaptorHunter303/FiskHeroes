function init(hero) {
    hero.setName("hero.fiskheroes.deadpool_xmen.name");
    hero.setVersion("item.superhero_armor.version.xmen");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.mask");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:deadpools_swords");
    
    hero.addAbilities("fiskheroes:healing_factor");
    hero.addAttribute("FALL_RESISTANCE", 5.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 2.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 6.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.4, 1);
    hero.addAttribute("SWORD_DAMAGE", 9.0, 0);
}
