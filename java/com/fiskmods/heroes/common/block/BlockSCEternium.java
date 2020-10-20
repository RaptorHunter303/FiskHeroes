package com.fiskmods.heroes.common.block;

import java.util.Random;

import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.client.render.block.RenderBlockSCEternium;
import com.fiskmods.heroes.util.FabricatorHelper.IMaterialEnergy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSCEternium extends Block implements IMaterialEnergy
{
    public BlockSCEternium()
    {
        super(Material.rock);
        setHarvestLevel("pickaxe", 3);
        setLightLevel(1.0F);
    }

    @Override
    public int getEnergyValue()
    {
        return 16384;
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return true;
    }

    @Override
    public int getRenderType()
    {
        return RenderBlockSCEternium.RENDER_ID;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {
        for (int i = 0; i < 10; ++i)
        {
            SHParticleType.ETERNIUM_AURA.spawn(x + 0.5 + (rand.nextFloat() * 2 - 1) * 0.1, y + 0.5 + (rand.nextFloat() * 2 - 1) * 0.1, z + 0.5 + (rand.nextFloat() * 2 - 1) * 0.1, 0, 0, 0);
        }
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        return ModBlocks.eterniumBlock.getIcon(side, metadata);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
    }
}
