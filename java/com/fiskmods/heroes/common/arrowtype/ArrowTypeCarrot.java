package com.fiskmods.heroes.common.arrowtype;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ArrowTypeCarrot<T extends EntityTrickArrow> extends ArrowType<T>
{
    public ArrowTypeCarrot(Class<T> clazz)
    {
        super(clazz);
    }

    @Override
    public ItemStack[] onEaten(ItemStack itemstack, World world, EntityPlayer player)
    {
        return new ItemStack[] {ArrowTypeManager.NORMAL.makeItem()};
    }

    @Override
    public boolean isEdible(ItemStack itemstack, EntityPlayer player)
    {
        return player.canEat(false);
    }

    @Override
    public int getHealAmount(ItemStack itemstack)
    {
        return 4;
    }

    @Override
    public float getSaturationModifier(ItemStack itemstack)
    {
        return 0.6F;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.eat;
    }
}
