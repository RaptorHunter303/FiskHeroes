function init(hero) {
    hero.setName("hero.fiskheroes.black_canary_dinah.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.mask");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:bo_staff");
    
    hero.addAbilities("fiskheroes:canary_cry");
    hero.addAttribute("FALL_RESISTANCE", 5.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 4.5, 0);
    hero.addAttribute("SPRINT_SPEED", 0.2, 1);
    hero.addAttribute("SWORD_DAMAGE", 0.3, 1);
    
    hero.addKeyBind("CANARY_CRY", "key.canaryCry", 1);
}
