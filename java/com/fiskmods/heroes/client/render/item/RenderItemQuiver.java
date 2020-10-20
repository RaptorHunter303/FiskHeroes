package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.QuiverHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemQuiver implements IItemRenderer
{
    INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        EntityPlayer player = mc.thePlayer;
        return type == ItemRenderType.INVENTORY && player.getHeldItem() != null && player.getHeldItem().getItem() == ModItems.compoundBow && item == QuiverHelper.getEquippedQuiver(player);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        if (type == ItemRenderType.INVENTORY)
        {
            Tessellator tessellator = Tessellator.instance;
            RenderItem renderItem = RenderItem.getInstance();

            tessellator.startDrawingQuads();
            tessellator.addVertex(0, 16, renderItem.zLevel);
            tessellator.addVertex(16, 16, renderItem.zLevel);
            tessellator.addVertex(16, 0, renderItem.zLevel);
            tessellator.addVertex(0, 0, renderItem.zLevel);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(0.2F, 0.2F, 1, 0.4F);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);

            renderItem.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), item, 0, 0, true);
        }
    }
}
