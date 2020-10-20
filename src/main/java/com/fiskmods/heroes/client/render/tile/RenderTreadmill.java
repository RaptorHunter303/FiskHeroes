package com.fiskmods.heroes.client.render.tile;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.tile.ModelTreadmill;
import com.fiskmods.heroes.common.tileentity.TileEntityTreadmill;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderTreadmill extends TileEntitySpecialRenderer
{
    private final ResourceLocation texture = new ResourceLocation(FiskHeroes.MODID, "textures/models/tiles/treadmill.png");
    private final ModelTreadmill model = new ModelTreadmill();

    public void render(TileEntityTreadmill tile, double x, double y, double z, float partialTicks)
    {
        int metadata = 0;

        if (tile.getWorldObj() != null)
        {
            metadata = tile.getBlockMetadata();
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        GL11.glRotatef(metadata * 90 + 180, 0.0F, 1.0F, 0.0F);

        if (metadata < 8)
        {
            float scale = 1.225F;
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0, -0.275F, 0.1F);

            bindTexture(texture);
            model.render(tile);
        }

        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks)
    {
        render((TileEntityTreadmill) tile, x, y, z, partialTicks);
    }
}
