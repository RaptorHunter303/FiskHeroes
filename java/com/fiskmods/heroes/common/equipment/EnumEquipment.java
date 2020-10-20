package com.fiskmods.heroes.common.equipment;

import java.util.function.BiFunction;

import com.fiskmods.heroes.common.entity.gadget.EntityBatarang;
import com.fiskmods.heroes.common.entity.gadget.EntityFreezeGrenade;
import com.fiskmods.heroes.common.entity.gadget.EntitySmokePellet;
import com.fiskmods.heroes.common.entity.gadget.EntityThrowingStar;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public enum EnumEquipment
{
    FISTS,
    BATARANG(EntityBatarang::new, 2, 3),
    FREEZE_GRENADE(EntityFreezeGrenade::new, 20, 1),
    SMOKE_PELLET(EntitySmokePellet::new, 20, 2),
    THROWING_STAR(EntityThrowingStar::new, 3, 2);

    public static final EnumEquipment[] EQUIPMENT_BATMAN = {BATARANG, FREEZE_GRENADE, SMOKE_PELLET};

    private final BiFunction<World, EntityLivingBase, Entity> constructor;

    public final Type type;
    public final int useCooldown;
    public final int maxUses;

    EnumEquipment(BiFunction<World, EntityLivingBase, Entity> func, float cooldown, int uses)
    {
        type = Type.PROJECTILE;
        constructor = func;
        useCooldown = MathHelper.floor_float(cooldown * 20);
        maxUses = uses;
    }

    EnumEquipment()
    {
        type = Type.NONE;
        constructor = (t, u) -> null;
        useCooldown = 0;
        maxUses = 0;
    }

    public Entity createEntity(World world, EntityLivingBase entity)
    {
        return constructor.apply(world, entity);
    }

    public enum Type
    {
        NONE,
        PROJECTILE
    }
}
