function init(hero) {
    hero.setName("hero.fiskheroes.green_arrow.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.hood");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:compound_bow");
    hero.addEquipment("fiskheroes:quiver");
    
    hero.addAbilities("fiskheroes:archery");
    hero.addAttribute("ARROW_DAMAGE", 0.25, 1);
    hero.addAttribute("BOW_DRAWBACK", 0.7, 1);
    hero.addAttribute("FALL_RESISTANCE", 6.0, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 5.0, 0);
    hero.addAttribute("SPRINT_SPEED", 0.15, 1);
    hero.addAttribute("SWORD_DAMAGE", 4.0, 0);
    
    hero.addKeyBind("HORIZONTAL_BOW", "key.horizontalBow", 1);
}
