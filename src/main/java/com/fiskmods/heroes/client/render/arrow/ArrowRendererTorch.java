package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;

public class ArrowRendererTorch extends ArrowRenderer
{
    private final Block blockType;

    public ArrowRendererTorch(Block block)
    {
        model.arrowHeadBase.showModel = false;
        blockType = block;
    }

    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        float f = 0.6F;
        GL11.glScalef(f, f, f);
        GL11.glRotatef(180, 0, 0, 1);
        GL11.glTranslatef(0, 1, 0);
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(false);
        RenderBlocks.getInstance().renderBlockAsItem(blockType, 1, 1);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
