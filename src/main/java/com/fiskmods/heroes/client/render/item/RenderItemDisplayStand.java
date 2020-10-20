package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemDisplayStand implements IItemRenderer
{
    INSTANCE;

    private final TileEntityDisplayStand tile = new TileEntityDisplayStand();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack itemstack, Object... data)
    {
        float scale = 0.6F;
        GL11.glScalef(scale, scale, scale);

        if (type == ItemRenderType.ENTITY || type == ItemRenderType.INVENTORY)
        {
            GL11.glTranslatef(0, -0.5F, 0);
        }
        else
        {
            if (type == ItemRenderType.EQUIPPED)
            {
                GL11.glTranslatef(1, 0.5F, 1);
            }
            else
            {
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }

            GL11.glRotatef(180, 0, 1, 0);
        }

        if (data.length > 0 && data[0] instanceof RenderBlocks)
        {
            ((RenderBlocks) data[0]).renderBlockAsItem(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage(), 1);
        }

        tile.setColor(itemstack.getItemDamage());
        TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, -0.5F, -0.5F, -0.5F, 0);
    }
}
