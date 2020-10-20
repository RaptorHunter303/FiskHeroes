function init(hero) {
    hero.setName("hero.fiskheroes.spectre.name");
    hero.setCosmic();
    hero.setTier(6);
    
    hero.setHelmet("item.superhero_armor.piece.cloak");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:flight", "fiskheroes:enhanced_reflexes", "fiskheroes:teleportation", "fiskheroes:energy_projection", "fiskheroes:archery");
    hero.addAttribute("ARROW_DAMAGE", 0.3, 1);
    hero.addAttribute("ATTACK_DAMAGE", 2.45, 1);
    hero.addAttribute("BOW_DRAWBACK", 0.8, 1);
    hero.addAttribute("PUNCH_DAMAGE", 14.0, 0);
    hero.addAttribute("SWORD_DAMAGE", -0.75, 1);
    
    hero.addKeyBind("ENERGY_PROJECTION", "key.energyProjection", 1);
    hero.addKeyBind("HORIZONTAL_BOW", "key.horizontalBow", 2);
    hero.addKeyBind("TELEPORT", "key.teleport", 3);
    
    hero.setHasProperty(hasProperty);
}

function hasProperty(entity, property) {
    return property == "BREATHE_UNDERWATER" || property == "BREATHE_SPACE";
}
