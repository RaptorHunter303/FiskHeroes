package com.fiskmods.heroes.client.render.block;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.common.block.ModBlocks;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public enum RenderBlockSCEternium implements ISimpleBlockRenderingHandler
{
    INSTANCE;

    public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        boolean hasNoOverride = !renderer.hasOverrideBlockTexture();
        boolean flag = false;
        float f = 0.0625F;
        float f1 = f * 3;

        renderer.setRenderAllFaces(true);

        if (hasNoOverride)
        {
            renderer.setOverrideBlockTexture(ModBlocks.eterniumBlock.getIcon(0, 0));
        }

        renderer.setRenderBounds(f1, f1, f1, 1 - f1, 1 - f1, 1 - f1);
        flag |= renderer.renderStandardBlock(block, x, y, z);

        if (hasNoOverride)
        {
            renderer.setOverrideBlockTexture(Blocks.iron_bars.getIcon(0, 0));
        }

        renderer.setRenderBounds(f, f, f, 1 - f, 1 - f, 1 - f);
        flag |= renderer.renderStandardBlock(block, x, y, z);

//        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
//        {
//            if (world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == block)
//            {
//                switch (dir)
//                {
//                case DOWN:
//                    renderer.setRenderBounds(f1, 0, f1, 1 - f1, f1, 1 - f1);
//                    break;
//                case UP:
//                    renderer.setRenderBounds(f1, 1 - f1, f1, 1 - f1, 1, 1 - f1);
//                    break;
//                case NORTH:
//                    renderer.setRenderBounds(f1, f1, 0, 1 - f1, 1 - f1, f1);
//                    break;
//                case SOUTH:
//                    renderer.setRenderBounds(f1, f1, 1 - f1, 1 - f1, 1 - f1, 1);
//                    break;
//                case WEST:
//                    renderer.setRenderBounds(0, f1, f1, f1, 1 - f1, 1 - f1);
//                    break;
//                case EAST:
//                    renderer.setRenderBounds(1 - f1, f1, f1, 1, 1 - f1, 1 - f1);
//                    break;
//                default:
//                    break;
//                }
//
//                flag |= renderer.renderStandardBlock(block, x, y, z);
//            }
//        }

        if (hasNoOverride)
        {
            renderer.setOverrideBlockTexture(ModBlocks.nexusBricks.getIcon(0, 0));
        }

        float minY = 0;
        float maxY = 1;
        float w = f * 1;
        float h = f * 2;

        if (world.getBlock(x, y - 1, z) != block)
        {
            minY = h;
            renderer.setRenderBounds(0, 0, 0, 1, minY, 1);
            flag |= renderer.renderStandardBlock(block, x, y, z);
        }

        if (world.getBlock(x, y + 1, z) != block)
        {
            maxY = 1 - h;
            renderer.setRenderBounds(0, maxY, 0, 1, 1, 1);
            flag |= renderer.renderStandardBlock(block, x, y, z);
        }

        renderer.setRenderBounds(0, minY, 0, w, maxY, w);
        flag |= renderer.renderStandardBlock(block, x, y, z);
        renderer.setRenderBounds(1 - w, minY, 1 - w, 1, maxY, 1);
        flag |= renderer.renderStandardBlock(block, x, y, z);
        renderer.setRenderBounds(1 - w, minY, 0, 1, maxY, w);
        flag |= renderer.renderStandardBlock(block, x, y, z);
        renderer.setRenderBounds(0, minY, 1 - w, w, maxY, 1);
        flag |= renderer.renderStandardBlock(block, x, y, z);

        if (hasNoOverride)
        {
            renderer.clearOverrideBlockTexture();
        }

        return flag;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;
        float f = 0.0625F;
        float f1 = f * 3;
        float w = f * 1;
        float h = f * 2;

        tessellator.startDrawingQuads();
        renderer.setRenderAllFaces(true);

        renderer.setOverrideBlockTexture(ModBlocks.eterniumBlock.getIcon(0, 0));
        renderer.setRenderBounds(f1, f1, f1, 1 - f1, 1 - f1, 1 - f1);
        SHRenderHooks.renderBlock(block, metadata, renderer);
        renderer.setOverrideBlockTexture(Blocks.iron_bars.getIcon(0, 0));
        renderer.setRenderBounds(f, f, f, 1 - f, 1 - f, 1 - f);
        SHRenderHooks.renderBlock(block, metadata, renderer);

        renderer.setOverrideBlockTexture(ModBlocks.nexusBricks.getIcon(0, 0));
        renderer.setRenderBounds(0, 0, 0, 1, h, 1);
        SHRenderHooks.renderBlock(block, metadata, renderer);
        renderer.setRenderBounds(0, 1 - h, 0, 1, 1, 1);
        SHRenderHooks.renderBlock(block, metadata, renderer);

        renderer.setRenderBounds(0, h, 0, w, 1 - h, w);
        SHRenderHooks.renderBlock(block, metadata, renderer);
        renderer.setRenderBounds(1 - w, h, 1 - w, 1, 1 - h, 1);
        SHRenderHooks.renderBlock(block, metadata, renderer);
        renderer.setRenderBounds(1 - w, h, 0, 1, 1 - h, w);
        SHRenderHooks.renderBlock(block, metadata, renderer);
        renderer.setRenderBounds(0, h, 1 - w, w, 1 - h, 1);
        SHRenderHooks.renderBlock(block, metadata, renderer);
        renderer.clearOverrideBlockTexture();

        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return RENDER_ID;
    }
}
