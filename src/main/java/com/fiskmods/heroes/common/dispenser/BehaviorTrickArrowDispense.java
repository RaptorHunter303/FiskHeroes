package com.fiskmods.heroes.common.dispenser;

import com.fiskmods.heroes.common.arrowtype.ArrowType;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class BehaviorTrickArrowDispense implements IBehaviorDispenseItem
{
    @Override
    public final ItemStack dispense(IBlockSource source, ItemStack itemstack)
    {
        ItemStack itemstack1 = dispenseStack(source, itemstack);
        playDispenseSound(source, itemstack);
        spawnDispenseParticles(source, BlockDispenser.func_149937_b(source.getBlockMetadata()));

        return itemstack1;
    }

    protected void spawnDispenseParticles(IBlockSource source, EnumFacing facing)
    {
        source.getWorld().playAuxSFX(2000, source.getXInt(), source.getYInt(), source.getZInt(), func_82488_a(facing));
    }

    private int func_82488_a(EnumFacing facing)
    {
        return facing.getFrontOffsetX() + 1 + (facing.getFrontOffsetZ() + 1) * 3;
    }

    public ItemStack dispenseStack(IBlockSource source, ItemStack itemstack)
    {
        ArrowType type = ArrowType.getArrowById(itemstack.getItemDamage());
        IPosition pos = BlockDispenser.func_149939_a(source);

        if (type.canDispense(source, itemstack))
        {
            type.shoot(source.getWorld(), null, source, null, itemstack.splitStack(1), 0);
            return itemstack;
        }
        else
        {
            EnumFacing facing = BlockDispenser.func_149937_b(source.getBlockMetadata());
            BehaviorDefaultDispenseItem.doDispense(source.getWorld(), itemstack.splitStack(1), 6, facing, pos);
            return itemstack;
        }
    }

    protected void playDispenseSound(IBlockSource source, ItemStack itemstack)
    {
        ArrowType type = ArrowType.getArrowById(itemstack.getItemDamage());

        if (type.canDispense(source, itemstack))
        {
            source.getWorld().playAuxSFX(1002, source.getXInt(), source.getYInt(), source.getZInt(), 0);
        }
        else
        {
            source.getWorld().playAuxSFX(1000, source.getXInt(), source.getYInt(), source.getZInt(), 0);
        }
    }
}
