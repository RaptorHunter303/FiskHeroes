package com.fiskmods.heroes.common.arrowtype;

import com.fiskmods.heroes.common.data.arrow.DefaultArrowData;
import com.fiskmods.heroes.common.data.arrow.GrappleArrowData;
import com.fiskmods.heroes.common.data.arrow.IArrowData;
import com.fiskmods.heroes.common.entity.arrow.EntityBlazeArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityBoxingGloveArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityCactusArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityCarrotArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityDetonatorArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityEnderPearlArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityExcessiveArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityExplPufferfArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityExplosiveArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityFireChargeArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityFireballArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityFireworkArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityGlitchArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityGrappleArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityGrossArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityPhantomArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityPufferfishArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityPulseArrow;
import com.fiskmods.heroes.common.entity.arrow.EntitySlimeArrow;
import com.fiskmods.heroes.common.entity.arrow.EntitySmokeBombArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityTorchArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityTutridiumArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityVialArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityVibraniumArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityVineArrow;

public class ArrowTypeManager
{
    public static final ArrowType NORMAL = new ArrowType<>(EntityTrickArrow.class);
    public static final ArrowType EXPLOSIVE = new ArrowType<>(EntityExplosiveArrow.class).setDataFactory(null).setVelocity(1);
    public static final ArrowType GRAPPLE = new ArrowTypeGrapple<>(EntityGrappleArrow.class).setDataFactory(GrappleArrowData::new);
    public static final ArrowType TRIPLE = new ArrowTypeTriple<>(NORMAL, 0.6).setDefaultDataFactory(NORMAL);
    public static final ArrowType CARROT = new ArrowTypeCarrot(EntityCarrotArrow.class).setDamage(0.2F);
    public static final ArrowType FIREWORK = new ArrowTypeFirework(EntityFireworkArrow.class).setDataFactory(null);
    public static final ArrowType FIRE_CHARGE = new ArrowType<>(EntityFireChargeArrow.class).setDefaultDataFactory(NORMAL).setDamage(0.75F);
    public static final ArrowType CACTUS = new ArrowType<>(EntityCactusArrow.class).setDataFactory(null).setVelocity(0.75F);
    public static final ArrowType BOXING_GLOVE = new ArrowType<>(EntityBoxingGloveArrow.class).setDataFactory(ArrowTypeManager::boxingGloveData).setDamage(0.5F).setVelocity(1);
    public static final ArrowType VIAL = new ArrowTypeVial<>(EntityVialArrow.class).setDefaultDataFactory(NORMAL).setVelocity(1).setGravity(0.075F);
    public static final ArrowType TORCH = new ArrowType<>(EntityTorchArrow.class).setDataFactory(null);
    public static final ArrowType VIBRANIUM = new ArrowType<>(EntityVibraniumArrow.class).setGravity(0.075F);
    public static final ArrowType PHANTOM = new ArrowType<>(EntityPhantomArrow.class).setDamage(0.5F).setVelocity(1);
    public static final ArrowType PUFFERFISH = new ArrowType<>(EntityPufferfishArrow.class).setDamage(0.6F).setVelocity(0.75F);
    public static final ArrowType EXPL_PUFFERF = new ArrowType<>(EntityExplPufferfArrow.class).setDamage(0.6F).setVelocity(0.75F);
    public static final ArrowType VINE = new ArrowTypeGrapple<>(EntityVineArrow.class).setDataFactory(GrappleArrowData::new);
    public static final ArrowType SMOKE_BOMB = new ArrowType<>(EntitySmokeBombArrow.class).setDefaultDataFactory(NORMAL).setVelocity(1);
    public static final ArrowType SLIME = new ArrowType<>(EntitySlimeArrow.class).setDamage(0.3F).setVelocity(0.75F);
    public static final ArrowType TRIPLE_EXPL = new ArrowTypeTriple<>(EXPLOSIVE, 0.9).setDataFactory(null);
    public static final ArrowType ENDER_PEARL = new ArrowType<>(EntityEnderPearlArrow.class).setDefaultDataFactory(NORMAL).setDamage(0.5F).setVelocity(0.25F).setGravity(0.02F);
    public static final ArrowType TUTRIDIUM = new ArrowType<>(EntityTutridiumArrow.class).setDamage(0.8F).setGravity(0.075F);
    public static final ArrowType EXCESSIVE = new ArrowType<>(EntityExcessiveArrow.class).setDamage(1.2F).setVelocity(0.4F);
    public static final ArrowType GROSS = new ArrowType<>(EntityGrossArrow.class).setDamage(0.75F);
    public static final ArrowType GLITCH = new ArrowType<>(EntityGlitchArrow.class);
    public static final ArrowType BLAZE = new ArrowType<>(EntityBlazeArrow.class);
    public static final ArrowType PULSE = new ArrowType<>(EntityPulseArrow.class).setDataFactory(null);
    public static final ArrowType DETONATOR = new ArrowType<>(EntityDetonatorArrow.class).setDataFactory(null).setVelocity(1);
    public static final ArrowType FIREBALL = new ArrowType<>(EntityFireballArrow.class).setDefaultDataFactory(NORMAL).setVelocity(1);

    private static IArrowData boxingGloveData(ArrowType type, EntityTrickArrow entity)
    {
        return type == NORMAL ? new DefaultArrowData(type, entity) : null;
    }

    public static void register()
    {
        ArrowType.register(0, "normal", NORMAL);
        ArrowType.register(1, "explosive", EXPLOSIVE);
        ArrowType.register(2, "grappling_hook", GRAPPLE);
        ArrowType.register(3, "triple", TRIPLE);
        ArrowType.register(4, "carrot", CARROT);
        ArrowType.register(5, "firework", FIREWORK);
        ArrowType.register(6, "fire_charge", FIRE_CHARGE);
        ArrowType.register(7, "cactus", CACTUS);
        ArrowType.register(8, "boxing_glove", BOXING_GLOVE);
        ArrowType.register(9, "vial", VIAL);
        ArrowType.register(10, "torch", TORCH);
        ArrowType.register(11, "vibranium", VIBRANIUM);
        ArrowType.register(12, "phantom", PHANTOM);
        ArrowType.register(13, "pufferfish", PUFFERFISH);
        ArrowType.register(14, "explosive_pufferfish", EXPL_PUFFERF);
        ArrowType.register(15, "vine", VINE);
        ArrowType.register(16, "smoke_bomb", SMOKE_BOMB);
        ArrowType.register(17, "slime", SLIME);
        ArrowType.register(18, "triple_explosive", TRIPLE_EXPL);
        ArrowType.register(19, "ender_pearl", ENDER_PEARL);
        ArrowType.register(20, "tutridium", TUTRIDIUM);
        ArrowType.register(21, "excessive", EXCESSIVE);
        ArrowType.register(22, "gross", GROSS);
        ArrowType.register(23, "glitch", GLITCH);
        ArrowType.register(24, "blaze", BLAZE);
        ArrowType.register(25, "pulse", PULSE);
        ArrowType.register(26, "detonator", DETONATOR);
        ArrowType.register(27, "fireball", FIREBALL);
    }
}
