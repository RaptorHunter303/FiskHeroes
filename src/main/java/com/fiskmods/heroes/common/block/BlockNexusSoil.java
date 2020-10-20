package com.fiskmods.heroes.common.block;

import java.util.Random;

import com.fiskmods.heroes.client.sound.SHSounds;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockNexusSoil extends BlockSH
{
    public BlockNexusSoil()
    {
        super(Material.grass);
        setStepSound(Block.soundTypeGravel);
        setHarvestLevel("shovel", 2);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        float f = 0.75F;
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1 - f, z + 1);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
        double d = 0.75;
        entity.motionX += (world.rand.nextFloat() * 2 - 1) * d;
        entity.motionZ += (world.rand.nextFloat() * 2 - 1) * d;
        entity.motionY -= 100;
        entity.setInWeb();

        if (!world.isRemote && world.isAirBlock(x, y + 1, z))
        {
            world.setBlock(x, y + 1, z, Blocks.fire);
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, SHSounds.BLOCK_NEXUSSOIL_IGNITE.toString(), 1, 1);
        }
    }

    @Override
    public int quantityDropped(Random rand)
    {
        return 0;
    }
}
