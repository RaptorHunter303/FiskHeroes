function init(hero) {
    hero.setName("hero.fiskheroes.black_canary.name");
    hero.setTier(2);
    
    hero.setHelmet("item.superhero_armor.piece.mask");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:black_canarys_tonfas");
    
    hero.addAbilities("fiskheroes:canary_cry");
    hero.addAttribute("FALL_RESISTANCE", 5.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 4.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.25, 1);
    hero.addAttribute("SWORD_DAMAGE", 0.2, 1);
    
    hero.addKeyBind("CANARY_CRY", "key.canaryCry", 1);
}
