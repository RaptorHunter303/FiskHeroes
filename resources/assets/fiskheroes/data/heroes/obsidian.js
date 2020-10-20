function init(hero) {
    hero.setName("hero.fiskheroes.obsidian.name");
    hero.setTier(3);
    
    hero.setHelmet("item.superhero_armor.piece.hood");
    hero.setChestplate("item.superhero_armor.piece.chestpiece");
    hero.setLeggings("item.superhero_armor.piece.pants");
    hero.setBoots("item.superhero_armor.piece.boots");
    
    hero.addAbilities("fiskheroes:umbrakinesis", "fiskheroes:flight");
    hero.addAttribute("ATTACK_DAMAGE", 0.6, 1);
    hero.addAttribute("FALL_RESISTANCE", 4.5, 0);
    hero.addAttribute("JUMP_HEIGHT", 1.0, 0);
    hero.addAttribute("PUNCH_DAMAGE", 4.5, 0);
    
    hero.addKeyBind("SHADOWFORM", "key.shadowForm", 1);
    hero.addKeyBind("SHADOWDOME", "key.shadowDome", 2);
    
    hero.setModifierEnabled(isModifierEnabled);
}

function isModifierEnabled(entity, modifier) {
    return entity.getData("fiskheroes:shadowform") || modifier != "fiskheroes:flight";
}
