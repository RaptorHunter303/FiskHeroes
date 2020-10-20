package com.fiskmods.heroes.common.block;

import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEterniumOre extends BlockOreSH
{
    public BlockEterniumOre()
    {
        super(ModItems.eterniumShard);
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
    {
        return super.canEntityDestroy(world, x, y, z, entity);
    }

    @Override
    public void onBlockPreDestroy(World p_149725_1_, int p_149725_2_, int p_149725_3_, int p_149725_4_, int p_149725_5_)
    {
        super.onBlockPreDestroy(p_149725_1_, p_149725_2_, p_149725_3_, p_149725_4_, p_149725_5_);
    }

    @Override
    public void onBlockDestroyedByPlayer(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_, int p_149664_5_)
    {
        super.onBlockDestroyedByPlayer(p_149664_1_, p_149664_2_, p_149664_3_, p_149664_4_, p_149664_5_);
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
    {
        super.onBlockClicked(world, x, y, z, player);
    }
}
