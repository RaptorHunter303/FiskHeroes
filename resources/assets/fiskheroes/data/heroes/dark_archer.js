function init(hero) {
    hero.setName("hero.fiskheroes.dark_archer.name");
    hero.setTier(2);
    
    hero.setHelmet("item.superhero_armor.piece.hood");
    hero.setChestplate("item.superhero_armor.piece.robes");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    hero.addEquipment("fiskheroes:compound_bow");
    hero.addEquipment("fiskheroes:quiver");
    
    hero.addAbilities("fiskheroes:archery");
    hero.addAttribute("BOW_DRAWBACK", 0.8, 1);
    hero.addAttribute("FALL_RESISTANCE", 4.0, 0);
    hero.addAttribute("SWORD_DAMAGE", 6.0, 0);
    
    hero.addKeyBind("HORIZONTAL_BOW", "key.horizontalBow", 1);
}
