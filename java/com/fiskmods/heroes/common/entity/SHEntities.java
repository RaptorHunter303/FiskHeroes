package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.entity.gadget.EntityBatarang;
import com.fiskmods.heroes.common.entity.gadget.EntityFreezeGrenade;
import com.fiskmods.heroes.common.entity.gadget.EntityGrapplingHook;
import com.fiskmods.heroes.common.entity.gadget.EntitySmokePellet;
import com.fiskmods.heroes.common.entity.gadget.EntityThrowingStar;
import com.fiskmods.heroes.util.SHFormatHelper;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;

public class SHEntities
{
    private static int nextID;

    public static void register()
    {
        nextID = -1;

        for (ArrowType type : ArrowType.REGISTRY)
        {
            String s = type.getDomain() + ":Arrow" + SHFormatHelper.getUnconventionalName(type.getRegistryName().getResourcePath());
            registerEntity(type.entity, s, 64, 20, true);
        }

        registerEntity(EntityThrownShield.class, "ThrownShield", 160, 20, true);
        registerEntity(EntityFireBlast.class, "FireBlast", 64, 10, true);
        registerEntity(EntityEarthquake.class, "GroundShockwave", 64, 10, false);
        registerEntity(EntityShadowDome.class, "ShadowDome", 64, 10, false);
        registerEntity(EntityIcicle.class, "Icicle", 64, 10, true);
        registerEntity(EntityCactusSpike.class, "CactusSpike", 64, 10, true);
        registerEntity(EntityBatarang.class, "Batarang", 64, 10, true);
        registerEntity(EntityThrowingStar.class, "ThrowingStar", 64, 10, true);
        registerEntity(EntityFreezeGrenade.class, "FreezeGrenade", 64, 10, true);
        registerEntity(EntitySmokePellet.class, "SmokePellet", 64, 10, true);
        registerEntity(EntityGrapplingHook.class, "GrapplingHook", 64, 20, true);
        registerEntity(EntityCanaryCry.class, "CanaryCry", 64, 10, true);
        registerEntity(EntityLaserBolt.class, "LaserBolt", 64, 20, true);
        registerEntity(EntityLightningCast.class, "LightningShot", 64, 10, false);
        registerEntity(EntityEarthCrack.class, "EarthCrack", 64, 10, false);
        registerEntity(EntitySpellWhip.class, "SpellWhip", 64, 10, true);
        registerEntity(EntityRepulsorBlast.class, "RepulsorBlast", 64, 10, false);
        registerEntity(EntityIronMan.class, "IronMan", 80, 1, true);
        registerEntity(EntitySpellDuplicate.class, "SpellDuplicate", 80, 1, false);

        EntityRegistry.registerGlobalEntityID(EntityCactus.class, String.format("%s.Cactus", FiskHeroes.MODID), EntityRegistry.findGlobalUniqueEntityId(), 0x149424, 0xD4D8A0);
        registerEntity(EntityCactus.class, "Cactus", 80, 1, true);
    }

    private static void registerEntity(Class<? extends Entity> entityClass, String name, int trackingRange, int updateFrequency, boolean sendVelocityUpdates)
    {
        EntityRegistry.registerModEntity(entityClass, name, ++nextID, FiskHeroes.MODID, trackingRange, updateFrequency, sendVelocityUpdates);
    }
}
