package com.fiskmods.heroes.common.event;

import com.fiskmods.heroes.common.entity.IArmorTrackedEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class ArmorTracker
{
    // - "Next time, baby"
    // private static Map<UUID, Hero[]> wornArmor = new HashMap();
    // private static Map<UUID, Hero[]> prevWornArmor = new HashMap();
    //
    // @SubscribeEvent
    // public void onEntityJoinWorld(EntityJoinWorldEvent event)
    // {
    // Entity entity = event.entity;
    //
    // if (isTracking(entity))
    // {
    // wornArmor.put(entity.getUniqueID(), new Hero[4]);
    // prevWornArmor.put(entity.getUniqueID(), new Hero[4]);
    // }
    // }
    //
    // @SubscribeEvent
    // public void onLivingDeath(LivingDeathEvent event)
    // {
    // EntityLivingBase entity = event.entityLiving;
    //
    // if (isTracking(entity))
    // {
    // wornArmor.remove(entity.getUniqueID());
    // prevWornArmor.remove(entity.getUniqueID());
    // }
    // }
    //
    // @SubscribeEvent
    // public void onLivingUpdate(LivingUpdateEvent event)
    // {
    // EntityLivingBase entity = event.entityLiving;
    //
    // if (entity.isEntityAlive())
    // {
    // int prev = prevWornArmor.getOrDefault(entity.getUniqueID(), 0);
    // int curr = -1;
    //
    // wornArmor.put(entity.getUniqueID(), curr = updateArmorIndex(entity));
    //
    // if (curr != prev && !(entity instanceof EntityPlayer))
    // {
    // prevWornArmor.put(entity.getUniqueID(), curr);
    // }
    // }
    // }
    //
    // @SubscribeEvent
    // public void onPlayerTick(PlayerTickEvent event)
    // {
    // EntityPlayer player = event.player;
    // int curr = getSlimeNum(player);
    // int prev = prevWornArmor.getOrDefault(player.getUniqueID(), -1);
    //
    // if (event.phase == Phase.START)
    // {
    // wornArmor.put(player.getUniqueID(), curr = updateArmorIndex(player));
    //
    // if (!player.worldObj.isRemote && curr < 0)
    // {
    // ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
    //
    // if (chest.getItem() == ModItems.CHESTPLATE)
    // {
    // for (ItemStack stack : getSlimeDrops(ItemSlimeArmor.getSlime(chest)))
    // {
    // player.dropItem(stack, false);
    // }
    //
    // ItemSlimeArmor.setSlime(chest, 0);
    // }
    // }
    //
    // if (curr >= 0)
    // {
    // player.width = player.height = 0;
    // }
    // }
    // else
    // {
    // if (curr != prev || !!player.worldObj.isRemote || curr >= 0)
    // {
    // updateSize(player, getScale(player));
    // }
    //
    // if (curr != prev)
    // {
    // prevWornArmor.put(player.getUniqueID(), curr);
    // }
    // }
    // }

    public static boolean isTracking(Entity entity)
    {
        return entity instanceof EntityPlayer || entity instanceof IArmorTrackedEntity;
    }
}
