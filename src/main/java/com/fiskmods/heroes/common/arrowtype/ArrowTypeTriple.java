package com.fiskmods.heroes.common.arrowtype;

import java.util.Random;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ArrowTypeTriple<T extends EntityTrickArrow> extends ArrowType<T>
{
    private final ArrowType arrowType;
    private final double spread;

    public ArrowTypeTriple(ArrowType<T> type, double d)
    {
        super(type.entity);
        arrowType = type;
        spread = d;
    }

    @Override
    public T shoot(World world, EntityLivingBase shooter, IBlockSource source, ItemStack bow, ItemStack arrow, float f)
    {
        if (!world.isRemote)
        {
            boolean horizontal = shooter != null && SHData.HORIZONTAL_BOW.get(shooter);
            Random rand = new Random();
            T entity = null;

            arrow = arrow.copy();
            arrow.setItemDamage(ArrowType.getIdFromArrow(arrowType));

            for (int i = 0; i < 3; ++i)
            {
                entity = super.shoot(world, shooter, source, bow, arrow, f);

                if (entity != null && i > 0)
                {
                    entity.motionX += (rand.nextDouble() - 0.5) * spread;
                    entity.motionZ += (rand.nextDouble() - 0.5) * spread;

                    if (!horizontal)
                    {
                        entity.motionY += (rand.nextDouble() - 0.5) * spread;
                    }
                }
            }

            return entity;
        }

        return null;
    }

    @Override
    public void onShoot(EntityLivingBase shooter, IBlockSource source, T entity, ItemStack bow, ItemStack arrow, float f)
    {
        arrow = arrow.copy();
        arrow.setItemDamage(ArrowType.getIdFromArrow(arrowType));
        arrowType.onShoot(shooter, source, entity, bow, arrow, f);
    }
}
