function init(hero) {
    hero.setName("hero.fiskheroes.vision.name");
    hero.setTier(5);
    
    hero.setHelmet("item.superhero_armor.piece.head");
    hero.setChestplate("item.superhero_armor.piece.torso");
    hero.setLeggings("item.superhero_armor.piece.legs");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:heat_vision", "fiskheroes:flight", "fiskheroes:superhuman_durability", "fiskheroes:absolute_intangibility", "fiskheroes:fire_immunity");
    hero.addAttribute("ATTACK_DAMAGE", 2.0, 1);
    hero.addAttribute("PUNCH_DAMAGE", 11.5, 0);
    hero.addAttribute("SWORD_DAMAGE", -0.35, 1);
    
    hero.addKeyBind("HEAT_VISION", "key.mindStoneBlast", 1);
    hero.addKeyBind("INTANGIBILITY", "key.intangibility", 2);
}
