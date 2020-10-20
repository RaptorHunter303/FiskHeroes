package com.fiskmods.heroes.common.damagesource;

import static com.fiskmods.heroes.common.damagesource.IExtendedDamage.DamageType.*;

import java.util.function.Function;

import com.fiskmods.heroes.common.damagesource.IExtendedDamage.DamageType;
import com.fiskmods.heroes.common.entity.EntityLaserBolt;
import com.fiskmods.heroes.common.entity.EntityThrownShield;
import com.fiskmods.heroes.common.entity.arrow.EntityCarrotArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityPufferfishArrow;
import com.fiskmods.heroes.common.entity.gadget.EntityBatarang;
import com.fiskmods.heroes.common.entity.gadget.EntityThrowingStar;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public enum ModDamageSources implements Function<Entity, DamageSource>
{
    BURN("burn", t -> t.with(INDIRECT).setFireDamage().setProjectile()),
    COLLISION("flyIntoPlayer", t -> t),
    ENERGY("energy", t -> t.with(DamageType.ENERGY, INDIRECT).setProjectile()),
    FREEZE("freeze.player", t -> t.with(COLD, INDIRECT).setDamageIsAbsolute().setProjectile()),
    ICICLE("icicle", t -> t.with(COLD).setProjectile()),
    LIGHTNING("lightning", t -> t.with(DamageType.ENERGY, INDIRECT).setProjectile()),
    REPULSOR("repulsor", t -> t.with(DamageType.ENERGY, INDIRECT).setProjectile()),
    SOUND("sound", t -> t.with(DamageType.SOUND, INDIRECT).setProjectile().setDamageIsAbsolute()),
    SPIKE("spike", t -> t.with(CACTUS)),
    THORNS("thorns", t -> t.with(CACTUS)),
    TREMOR("tremor", t -> t.with(INDIRECT).setExplosion()),
    EARTH_CRACK("earthCrack", t -> t.with(INDIRECT).setMagicDamage()),
    ELDRITCH_WHIP("magic", t -> t.with(INDIRECT).setMagicDamage().setFireDamage()),
    MAGIC("magic", t -> t.with(INDIRECT).setMagicDamage());

    public static final DamageSource SUFFOCATE = new DamageSourceSH("suffocate").setDamageBypassesArmor();
//    public static final DamageSource FREEZE = new DamageSourceSH("freeze").with(COLD).setDamageIsAbsolute();
    public static final DamageSource FLY_INTO_WALL = new DamageSourceSH("flyInto");
    public static final DamageSource BLACK_HOLE = new DamageSourceSH("blackHole").setDamageBypassesArmor().setDamageAllowedInCreativeMode();
//    public static final DamageSource VELOCITY_NINE = new DamageSourceSH("velocityNine").setDamageBypassesArmor().setDamageAllowedInCreativeMode();
    public static final DamageSource SNAKE = new DamageSourceSH("snake").setDamageIsAbsolute();

    private final Function<Entity, DamageSource> func;

    <T extends DamageSource & IExtendedDamage> ModDamageSources(String s, Function<T, DamageSource> f)
    {
        func = entity -> f.apply((T) new EntityDamageSourceSH(s, entity));
    }

    @Override
    public DamageSource apply(Entity entity)
    {
        return func.apply(entity);
    }

    public static DamageSource causeBatarangDamage(EntityBatarang batarang, Entity entity)
    {
        return new EntityDamageSourceIndirectSH("batarang", batarang, entity).with(SHURIKEN).setProjectile();
    }

    public static DamageSource causeThrowingStarDamage(EntityThrowingStar throwingStar, Entity entity)
    {
        return new EntityDamageSourceIndirectSH("throwingStar", throwingStar, entity).with(SHURIKEN).setProjectile();
    }

    public static DamageSource causeFireballDamage(Entity fireball, Entity entity)
    {
        return new EntityDamageSourceIndirectSH("fireball", fireball, entity).setFireDamage().setProjectile();
    }

    public static DamageSource causeShieldDamage(EntityThrownShield shield, Entity entity)
    {
        return new EntityDamageSourceIndirectSH("thrownShield", shield, entity).setProjectile();
    }

    public static DamageSource causeCarrowDamage(EntityCarrotArrow arrow, Entity entity)
    {
        return new EntityDamageSourceIndirectSH("carrow", arrow, entity).setProjectile();
    }

    public static DamageSource causePufferfishDamage(EntityPufferfishArrow arrow, Entity entity)
    {
        return new EntityDamageSourceIndirectSH("pufferfish", arrow, entity).setProjectile();
    }

    public static DamageSource causeLaserDamage(EntityLaserBolt laser, Entity entity)
    {
        return new EntityDamageSourceIndirectSH("laser", laser, entity).with(DamageType.ENERGY).setProjectile().setDamageIsAbsolute();
    }
}
