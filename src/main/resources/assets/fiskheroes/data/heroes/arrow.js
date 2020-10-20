function init(hero) {
    hero.setName("hero.fiskheroes.arrow.name");
    hero.setTier(2);
    
    hero.setHelmet("item.superhero_armor.piece.hood");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:compound_bow");
    hero.addEquipment("fiskheroes:quiver");
    
    hero.addAbilities("fiskheroes:archery");
    hero.addAttribute("ARROW_DAMAGE", 0.25, 1);
    hero.addAttribute("BOW_DRAWBACK", 0.7, 1);
    hero.addAttribute("FALL_RESISTANCE", 4.5, 0);
    hero.addAttribute("PUNCH_DAMAGE", 5.0, 0);
    
    hero.addKeyBind("HORIZONTAL_BOW", "key.horizontalBow", 1);
}
