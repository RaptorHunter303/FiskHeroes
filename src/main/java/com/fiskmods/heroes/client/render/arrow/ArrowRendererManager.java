package com.fiskmods.heroes.client.render.arrow;

import com.fiskmods.heroes.common.arrowtype.ArrowTypeManager;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArrowRendererManager
{
    public static void register()
    {
        ArrowRenderer.register(ArrowTypeManager.NORMAL, new ArrowRenderer());
        ArrowRenderer.register(ArrowTypeManager.EXPLOSIVE, new ArrowRendererExplosive());
        ArrowRenderer.register(ArrowTypeManager.GRAPPLE, new ArrowRendererGrapplingHook());
        ArrowRenderer.register(ArrowTypeManager.CARROT, new ArrowRendererCarrot());
        ArrowRenderer.register(ArrowTypeManager.FIREWORK, new ArrowRendererFirework());
        ArrowRenderer.register(ArrowTypeManager.CACTUS, new ArrowRendererCactus());
        ArrowRenderer.register(ArrowTypeManager.BOXING_GLOVE, new ArrowRendererBoxingGlove());
        ArrowRenderer.register(ArrowTypeManager.VIAL, new ArrowRendererVial());
        ArrowRenderer.register(ArrowTypeManager.PHANTOM, new ArrowRendererPhantom());
        ArrowRenderer.register(ArrowTypeManager.PUFFERFISH, new ArrowRendererPufferfish(false));
        ArrowRenderer.register(ArrowTypeManager.EXPL_PUFFERF, new ArrowRendererPufferfish(true));
        ArrowRenderer.register(ArrowTypeManager.VINE, new ArrowRendererVine());
        ArrowRenderer.register(ArrowTypeManager.SMOKE_BOMB, new ArrowRendererSmokeBomb());
        ArrowRenderer.register(ArrowTypeManager.EXCESSIVE, new ArrowRendererExcessive());
        ArrowRenderer.register(ArrowTypeManager.GROSS, new ArrowRendererGross());
        ArrowRenderer.register(ArrowTypeManager.GLITCH, new ArrowRendererGlitch());
        ArrowRenderer.register(ArrowTypeManager.DETONATOR, new ArrowRendererDetonator());
        ArrowRenderer.register(ArrowTypeManager.FIREBALL, new ArrowRendererFireball());

        ArrowRenderer.register(ArrowTypeManager.TRIPLE, new ArrowRendererTriple(ArrowTypeManager.NORMAL));
        ArrowRenderer.register(ArrowTypeManager.TRIPLE_EXPL, new ArrowRendererTriple(ArrowTypeManager.EXPLOSIVE));

        ArrowRenderer.register(ArrowTypeManager.FIRE_CHARGE, new ArrowRendererItemBall(new ItemStack(Items.fire_charge)));
        ArrowRenderer.register(ArrowTypeManager.SLIME, new ArrowRendererItemBall(new ItemStack(Items.slime_ball)));
        ArrowRenderer.register(ArrowTypeManager.ENDER_PEARL, new ArrowRendererItemBall(new ItemStack(Items.ender_pearl)));

        ArrowRenderer.register(ArrowTypeManager.TORCH, new ArrowRendererTorch(Blocks.torch));
        ArrowRenderer.register(ArrowTypeManager.PULSE, new ArrowRendererTorch(Blocks.redstone_torch));

        ArrowRenderer.register(ArrowTypeManager.VIBRANIUM, new ArrowRendererTipped("vibranium", false));
        ArrowRenderer.register(ArrowTypeManager.TUTRIDIUM, new ArrowRendererTipped("tutridium", false));
        ArrowRenderer.register(ArrowTypeManager.BLAZE, new ArrowRendererTipped("blaze", true));
    }
}
