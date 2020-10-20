package com.fiskmods.heroes.client.render.block;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.common.BlockStack;
import com.fiskmods.heroes.common.block.BlockDisplayStand;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;

public enum RenderBlockDisplayStand implements ISimpleBlockRenderingHandler
{
    INSTANCE;

    public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        boolean flag = false;
        boolean ao = renderer.enableAO;
        int metadata = world.getBlockMetadata(x, y, z);

        Map<List<AxisAlignedBB>, BlockStack> map = BlockDisplayStand.getExtraBounds(world, x, y, z);
        renderer.setRenderAllFaces(true);
        renderer.enableAO = false;

        for (Map.Entry<List<AxisAlignedBB>, BlockStack> e : map.entrySet())
        {
            BlockStack stack = e.getValue();

            if (stack != null && stack.block.getMaterial() != Material.air && stack.block.canRenderInPass(block.getRenderBlockPass()))
            {
                renderer.setOverrideBlockTexture(stack.block.getIcon(1, stack.metadata));
                BlockDisplayStand.renderingBlock = stack;

                for (AxisAlignedBB aabb : e.getKey())
                {
                    boolean flag1 = false;
                    int renderY = y;

                    if (metadata < 8)
                    {
                        if (aabb.minY >= 1)
                        {
                            ++renderY;
                        }
                    }
                    else
                    {
                        if (aabb.minY <= 1)
                        {
                            --renderY;
                        }
                    }

                    if (aabb.minY - 1 >= 0)
                    {
                        aabb.minY -= 1;
                        aabb.maxY -= 1;
                        Tessellator.instance.addTranslation(0, 1, 0);
                        flag1 = true;
                    }

                    int translation = y - renderY;

                    if (translation != 0)
                    {
                        Tessellator.instance.addTranslation(0, translation, 0);
                    }

                    renderer.setRenderBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
                    flag |= renderer.renderStandardBlock(block, x, renderY, z);

                    if (translation != 0)
                    {
                        Tessellator.instance.addTranslation(0, -translation, 0);
                    }

                    if (flag1)
                    {
                        Tessellator.instance.addTranslation(0, -1, 0);
                    }
                }
            }
        }

        BlockDisplayStand.renderingBlock = null;
        renderer.clearOverrideBlockTexture();
        renderer.setRenderAllFaces(false);
        renderer.enableAO = ao;

        return flag;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawingQuads();
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        renderer.setOverrideBlockTexture(Blocks.stone_slab.getIcon(0, 0));
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
