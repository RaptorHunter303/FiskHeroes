package com.fiskmods.heroes.client.render.tile;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.SHRenderHooks;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class RenderPymChest extends TileEntitySpecialRenderer
{
    public void render(TileEntity tile, double x, double y, double z, float partialTicks)
    {
        int metadata = 0;

        if (tile.getWorldObj() != null)
        {
            metadata = tile.getBlockMetadata();
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GL11.glScalef(1F, -1F, -1F);
        SHRenderHooks.renderBlockAllFaces(RenderBlocks.getInstance(), tile.getBlockType(), tile.xCoord, tile.yCoord, tile.zCoord, tile.blockMetadata);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks)
    {
        render(tile, x, y, z, partialTicks);
    }
}
