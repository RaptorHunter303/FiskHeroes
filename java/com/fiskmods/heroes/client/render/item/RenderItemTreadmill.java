package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.tileentity.TileEntityTreadmill;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemTreadmill implements IItemRenderer
{
    INSTANCE;

    private final TileEntityTreadmill tile = new TileEntityTreadmill();

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
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        float scale = 0.6F;
        GL11.glScalef(scale, scale, scale);

        if (type == ItemRenderType.INVENTORY)
        {
            GL11.glRotatef(180, 0, 1, 0);
            TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, 0, -1, -0.5F, 0);
        }
        else if (type == ItemRenderType.ENTITY)
        {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, -0.5F, -0.5F, -1, 0);
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, 0.5F, 0.5F, -0.5F, 0);
        }
        else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.FIRST_PERSON_MAP)
        {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, 0.5F, 0.75F, 0.5F, 0);
        }
    }
}
