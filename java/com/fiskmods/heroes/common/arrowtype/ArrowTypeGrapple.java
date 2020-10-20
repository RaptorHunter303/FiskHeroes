package com.fiskmods.heroes.common.arrowtype;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityVineArrow;

import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class ArrowTypeGrapple<T extends EntityTrickArrow> extends ArrowType<T>
{
    public ArrowTypeGrapple(Class<T> clazz)
    {
        super(clazz);
    }

    @Override
    public void onShoot(EntityLivingBase shooter, IBlockSource source, T entity, ItemStack bow, ItemStack arrow, float f)
    {
        super.onShoot(shooter, source, entity, bow, arrow, f);

        if (entity instanceof EntityVineArrow && arrow.hasDisplayName() && arrow.getDisplayName().equals("Snake Arrow"))
        {
            ((EntityVineArrow) entity).setIsSnake(true);
        }
    }

    @Override
    public boolean canDispense(IBlockSource blockSource, ItemStack itemstack)
    {
        return false;
    }
}
