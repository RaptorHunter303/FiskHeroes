function init(hero) {
    hero.setName("hero.fiskheroes.prometheus.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.hood");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:prometheus_sword");
    hero.addEquipment("fiskheroes:compound_bow");
    hero.addEquipment("fiskheroes:quiver");
    
    hero.addAbilities("fiskheroes:archery", "fiskheroes:throwing_stars");
    hero.addAttribute("ARROW_DAMAGE", 0.25, 1);
    hero.addAttribute("BOW_DRAWBACK", 0.7, 1);
    hero.addAttribute("FALL_RESISTANCE", 6.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.5, 0);
    hero.addAttribute("PUNCH_DAMAGE", 4.5, 0);
    hero.addAttribute("SPRINT_SPEED", 0.15, 1);
    hero.addAttribute("SWORD_DAMAGE", 6.0, 0);
    
    hero.addKeyBind("HORIZONTAL_BOW", "key.horizontalBow", 1);
    hero.addKeyBind("UTILITY_RESET", "key.utilityReset", 2);
    hero.addKeyBind("UTILITY_SWITCH", "key.utilitySwitch", 3);
}
