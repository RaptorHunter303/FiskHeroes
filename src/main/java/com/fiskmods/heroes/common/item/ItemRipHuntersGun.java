package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityLaserBolt;
import com.fiskmods.heroes.common.entity.EntityLaserBolt.Type;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Hero.Permission;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemRipHuntersGun extends ItemUntextured implements IReloadWeapon, IScopeWeapon
{
    public ItemRipHuntersGun()
    {
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        HeroIteration iter;

        if (SHData.AIMING.get(player) && SHData.RELOAD_TIMER.get(player) == 0 && (iter = SHHelper.getHeroIter(player)) != null && iter.hero.hasPermission(player, Permission.USE_RIPS_GUN))
        {
            if (!world.isRemote)
            {
                world.spawnEntityInWorld(new EntityLaserBolt(world, player, Type.REVOLVER, iter, true));
            }

            player.playSound(SHSounds.ITEM_RIPSGUN_SHOOT.toString(), 1, 1.0F / (itemRand.nextFloat() * 0.3F + 0.7F));
            SHData.RELOAD_TIMER.setWithoutNotify(player, 1.0F);
        }

        return stack;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getReloadTime(ItemStack stack, EntityPlayer player, Hero hero)
    {
        return Rule.COOLDOWN_RIPSGUN.get(player, hero);
    }

    @Override
    public boolean isProperScope()
    {
        return false;
    }
}
