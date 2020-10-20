package com.fiskmods.heroes.client.render.item;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.common.BlockStack;
import com.fiskmods.heroes.common.item.ItemDisplayCase;
import com.fiskmods.heroes.common.item.ItemDisplayCase.DisplayCase;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemDisplayCase implements IItemRenderer
{
    INSTANCE;

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

        if (type == ItemRenderType.ENTITY || type == ItemRenderType.INVENTORY)
        {
            if (type == ItemRenderType.ENTITY)
            {
                scale *= 0.5F;
            }

            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0, -0.5F, 0);
        }
        else
        {
            GL11.glScalef(scale, scale, scale);

            if (type == ItemRenderType.EQUIPPED)
            {
                GL11.glTranslatef(1, 0.5F, 1);
            }
            else
            {
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
        }

        render(item);
    }

    public void render(ItemStack item)
    {
        DisplayCase casing = ItemDisplayCase.getCasing(item);
        RenderBlocks renderBlocks = RenderBlocks.getInstance();
        Map<List<AxisAlignedBB>, BlockStack> map = casing.getBounds(0);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        boolean flag = false;

        for (int i = 0; i < 2; ++i)
        {
            for (Map.Entry<List<AxisAlignedBB>, BlockStack> e : map.entrySet())
            {
                BlockStack stack = e.getValue();

                if (stack != null && stack.block.getMaterial() != Material.air && stack.block.canRenderInPass(i))
                {
                    int color = stack.block.getRenderColor(stack.metadata);

                    if (renderBlocks.useInventoryTint)
                    {
                        tessellator.setColorOpaque_I(color);
                        flag = true;
                    }
                    else if (flag)
                    {
                        tessellator.setColorOpaque_I(-1);
                    }

                    for (AxisAlignedBB aabb : e.getKey())
                    {
                        boolean flag1 = false;

                        if (aabb.minY - 1 >= 0)
                        {
                            aabb.minY -= 1;
                            aabb.maxY -= 1;
                            Tessellator.instance.addTranslation(0, 1, 0);
                            flag1 = true;
                        }

                        renderBlocks.setRenderBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
                        SHRenderHooks.renderBlock(stack.block, stack.block.getIcon(1, stack.metadata), renderBlocks);

                        if (flag1)
                        {
                            Tessellator.instance.addTranslation(0, -1, 0);
                        }
                    }
                }
            }
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glRotatef(90, 0, 1, 0);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
}
