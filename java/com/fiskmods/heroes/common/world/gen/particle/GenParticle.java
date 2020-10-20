package com.fiskmods.heroes.common.world.gen.particle;

import java.util.Random;

import com.fiskmods.heroes.common.block.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public enum GenParticle
{
    //// ICE(10, 1, 4, 8, ModBlocks.subatomicParticleCore, new ParticleShellVaried(new Block[] {Blocks.ice, Blocks.packed_ice}, Blocks.packed_ice, 5)),
    // NETHER(10, 1, 4, 8, ModBlocks.subatomicParticleCore, new ParticleShellVaried(new Block[] {Blocks.netherrack, Blocks.quartz_ore}, Blocks.glowstone, 5)),
    // QUARTZ(10, 1, 3, 6, ModBlocks.subatomicParticleCore, new ParticleShell(Blocks.stained_glass, Blocks.quartz_block)),
    // OBSIDIAN_SMALL(5, 1, 4, 8, ModBlocks.subatomicParticleCore, new ParticleShell(Blocks.obsidian, Blocks.end_stone)),
    // OBSIDIAN_LARGE(2, 1, 16, 32, ModBlocks.subatomicParticleCore, new ParticleShellObsidian(Blocks.obsidian, Blocks.end_stone)),
    // STONE(2, 1, 4, 16, ModBlocks.subatomicParticleCore, new ParticleShellOres(Blocks.stone)),
    // SUBATOMIC(2, 1, 8, 16, ModBlocks.subatomicParticleCore, new ParticleShell(ModBlocks.subatomicParticleShell)),
    // TACHYONIC(1, 1, 4, 8, ModBlocks.tachyonicParticleCore, new ParticleShell(ModBlocks.tachyonicParticleShell));

    OBSIDIAN_SMALL(20, 1, 4, 8, ModBlocks.subatomicParticleCore, new ParticleShell(Blocks.obsidian, Blocks.end_stone)),
    OBSIDIAN_LARGE(8, 1, 16, 32, ModBlocks.subatomicParticleCore, new ParticleShellObsidian(Blocks.obsidian, Blocks.end_stone)),
    NETHER(4, 2, 4, 8, ModBlocks.subatomicParticleCore, new ParticleShellOres(Blocks.netherrack)),
    SUBATOMIC(2, 1, 8, 16, ModBlocks.subatomicParticleCore, new ParticleShell(ModBlocks.subatomicParticleShell)),
    TACHYONIC(1, 1, 4, 8, ModBlocks.tachyonicParticleCore, new ParticleShell(ModBlocks.tachyonicParticleShell)),
    STONE(1, 1, 4, 16, ModBlocks.subatomicParticleCore, new ParticleShellOres(Blocks.stone));

    public float weight;
    public float gravity;
    public int minSize;
    public int maxSize;

    public Block core;
    public ParticleShell shell;

    private GenParticle(float weight, float gravity, int minSize, int maxSize, Block core, ParticleShell shell)
    {
        this.weight = weight;
        this.gravity = gravity;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.core = core;
        this.shell = shell;
    }

    public static GenParticle getRandom(Random rand)
    {
        double totalWeight = 0;

        for (GenParticle particle : values())
        {
            totalWeight += particle.weight;
        }

        double random = rand.nextDouble() * totalWeight;

        for (GenParticle particle : values())
        {
            random -= particle.weight;

            if (random <= 0)
            {
                return particle;
            }
        }

        return values()[0];
    }

    public static GenParticle get(int index)
    {
        if (index >= 0 && index < values().length)
        {
            return values()[index];
        }

        return values()[0];
    }
}
