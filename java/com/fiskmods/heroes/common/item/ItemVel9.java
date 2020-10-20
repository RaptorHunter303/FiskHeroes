package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.common.achievement.SHAchievements;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemVel9 extends Item
{
    public ItemVel9()
    {
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer player)
    {
        if (!player.capabilities.isCreativeMode)
        {
            --itemstack.stackSize;
        }

        if (!world.isRemote)
        {
            StatusEffect effectVel9 = StatusEffect.get(player, StatEffect.VELOCITY_9);
            StatusEffect effectSpeedSickness = StatusEffect.get(player, StatEffect.SPEED_SICKNESS);
            int duration = 6000;
            int amplifier = 0;

            if (effectVel9 != null)
            {
                amplifier = effectVel9.amplifier + 1;
            }
            else if (effectSpeedSickness != null)
            {
                amplifier = effectSpeedSickness.amplifier + 1;
                player.triggerAchievement(SHAchievements.SPEED_CURE);
            }

            StatusEffect.add(player, StatEffect.VELOCITY_9, duration / (amplifier + 1), amplifier);
        }

        if (!player.capabilities.isCreativeMode)
        {
            if (itemstack.stackSize <= 0)
            {
                return new ItemStack(Items.glass_bottle);
            }

            player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
        }

        return itemstack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.drink;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
        return itemstack;
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int damage, int pass)
    {
        return pass == 0 ? ItemPotion.func_94589_d("overlay") : ItemPotion.func_94589_d("bottle_drinkable");
    }

    @Override
    public int getColorFromItemStack(ItemStack itemstack, int pass)
    {
        if (pass == 0)
        {
            return 0x610000;
        }
        else
        {
            return super.getColorFromItemStack(itemstack, pass);
        }
    }

    @Override
    public boolean hasEffect(ItemStack itemstack, int pass)
    {
        return pass == 0;
    }

    @Override
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
    }
}
