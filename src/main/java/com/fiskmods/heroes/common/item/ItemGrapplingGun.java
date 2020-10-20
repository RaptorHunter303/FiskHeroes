package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.entity.gadget.EntityGrapplingHook;
import com.fiskmods.heroes.common.hero.Hero.Permission;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemGrapplingGun extends ItemUntextured
{
    public ItemGrapplingGun()
    {
        setMaxStackSize(1);
        setMaxDamage(SHConstants.MAX_DMG_GRAPPLING_GUN);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        if (SHHelper.hasPermission(player, Permission.USE_GRAPPLING_GUN))
        {
            EntityGrapplingHook entity = new EntityGrapplingHook(world, player);

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(entity);
            }

            itemstack.damageItem(1, player);
        }

        return itemstack;
    }
}
