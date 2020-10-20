package com.fiskmods.heroes.common.interaction;

import com.fiskmods.heroes.common.interaction.key.KeyPressAccelerate;
import com.fiskmods.heroes.common.interaction.key.KeyPressArrowSelect;
import com.fiskmods.heroes.common.interaction.key.KeyPressDecelerate;
import com.fiskmods.heroes.common.interaction.key.KeyPressEquipmentReset;
import com.fiskmods.heroes.common.interaction.key.KeyPressEquipmentSwitch;
import com.fiskmods.heroes.common.interaction.key.KeyPressIntangibility;
import com.fiskmods.heroes.common.interaction.key.KeyPressInvisibility;
import com.fiskmods.heroes.common.interaction.key.KeyPressMaskOpen;
import com.fiskmods.heroes.common.interaction.key.KeyPressMiniaturizeSuit;
import com.fiskmods.heroes.common.interaction.key.KeyPressOpenQuiver;
import com.fiskmods.heroes.common.interaction.key.KeyPressSentryMode;
import com.fiskmods.heroes.common.interaction.key.KeyPressShadowform;
import com.fiskmods.heroes.common.interaction.key.KeyPressShapeShift;
import com.fiskmods.heroes.common.interaction.key.KeyPressShapeShiftReset;
import com.fiskmods.heroes.common.interaction.key.KeyPressSpellMenu;
import com.fiskmods.heroes.common.interaction.key.KeyPressSpikeRing;
import com.fiskmods.heroes.common.interaction.key.KeyPressSpodermen;
import com.fiskmods.heroes.common.interaction.key.KeyPressSteelTransform;
import com.fiskmods.heroes.common.interaction.key.KeyPressTeleport;
import com.fiskmods.heroes.common.interaction.key.KeyPressToggleBlade;
import com.fiskmods.heroes.common.interaction.key.KeyPressToggleHover;
import com.fiskmods.heroes.common.interaction.key.KeyPressToggleShield;
import com.fiskmods.heroes.common.interaction.key.KeyPressToggleSlow;
import com.fiskmods.heroes.common.interaction.key.KeyPressToggleSpeed;
import com.fiskmods.heroes.common.interaction.key.KeyPressTransform;

public class InteractionHandler
{
    public static void register()
    {
        Interaction.register("BATFISH", new InteractionBatfish());
        Interaction.register("REPULSOR_BLAST", new InteractionRepulsor());
        Interaction.register("FIREBALL", new InteractionFireball());
        Interaction.register("ICICLE_SHOOT", new InteractionIcicleShoot());
        Interaction.register("EARTHQUAKE", new InteractionEarthquake());
        Interaction.register("GROUND_SMASH", new InteractionGroundSmash());
        Interaction.register("CACTUS_SUMMON", new InteractionCactusSummon());
        Interaction.register("SPIKE_BURST", new InteractionSpikeBurst());
        Interaction.register("EQUIPMENT", new InteractionEquipment());
        Interaction.register("LIGHTNING", new InteractionLightning());
        Interaction.register("ENERGY_BLAST", new InteractionEnergyBlast());
//        Interaction.register("TELEKINESIS", new InteractionTelekinesis());

        Interaction.register("KEY_SELECT_ARROW", new KeyPressArrowSelect());
        Interaction.register("KEY_OPEN_QUIVER", new KeyPressOpenQuiver());
        Interaction.register("KEY_OPEN_MASK", new KeyPressMaskOpen());
        Interaction.register("KEY_TOGGLE_SPEED", new KeyPressToggleSpeed());
        Interaction.register("KEY_ACCELERATE", new KeyPressAccelerate());
        Interaction.register("KEY_DECELERATE", new KeyPressDecelerate());
        Interaction.register("KEY_TOGGLE_SLOW", new KeyPressToggleSlow());
        Interaction.register("KEY_SHAPE_SHIFT", new KeyPressShapeShift());
        Interaction.register("KEY_SHAPE_SHIFT_RESET", new KeyPressShapeShiftReset());
        Interaction.register("KEY_INTANGIBILITY", new KeyPressIntangibility());
        Interaction.register("KEY_INVISIBILITY", new KeyPressInvisibility());
        Interaction.register("KEY_EQUIPMENT_SWITCH", new KeyPressEquipmentSwitch());
        Interaction.register("KEY_EQUIPMENT_RESET", new KeyPressEquipmentReset());
        Interaction.register("KEY_MINIATURIZE_SUIT", new KeyPressMiniaturizeSuit());
        Interaction.register("KEY_TOGGLE_HOVER", new KeyPressToggleHover());
        Interaction.register("KEY_SPIKE_RING", new KeyPressSpikeRing());
        Interaction.register("KEY_SPODERMEN", new KeyPressSpodermen());
        Interaction.register("KEY_SENTRY_MODE", new KeyPressSentryMode());
        Interaction.register("KEY_STEEL_TRANSFORM", new KeyPressSteelTransform());
        Interaction.register("KEY_TOGGLE_SHIELD", new KeyPressToggleShield());
        Interaction.register("KEY_SHADOWFORM", new KeyPressShadowform());
        Interaction.register("KEY_TELEPORT", new KeyPressTeleport());
        Interaction.register("KEY_TRANSFORM", new KeyPressTransform());
        Interaction.register("KEY_BLADE", new KeyPressToggleBlade());
        Interaction.register("KEY_SPELL_MENU", new KeyPressSpellMenu());
//        Interaction.register("KEY_COMBAT_MODE", new KeyPressCombatMode()); // TODO: 1.4 Combat
    }
}
