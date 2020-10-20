package com.fiskmods.heroes.common.world.biome;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenQuantumRealm extends BiomeGenBase
{
    public BiomeGenQuantumRealm(int id)
    {
        super(id);
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        topBlock = Blocks.stone;
        fillerBlock = Blocks.stone;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float temp)
    {
        return 0;
    }

    @Override
    public BiomeDecorator createBiomeDecorator()
    {
        return new BiomeDecoratorQuantumRealm();
    }
}
