package com.fiskmods.heroes.client.gui;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.container.ContainerQuiver;
import com.fiskmods.heroes.common.container.InventoryQuiver;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiQuiver extends GuiContainer
{
    private static final ResourceLocation guiTextures = new ResourceLocation(FiskHeroes.MODID, "textures/gui/container/quiver.png");
    public ItemStack quiverItem;

    public GuiQuiver(InventoryPlayer inventoryPlayer, InventoryQuiver inventoryQuiver)
    {
        super(new ContainerQuiver(inventoryPlayer, inventoryQuiver));
        ySize = 133;
        quiverItem = inventoryQuiver.quiverItem;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        String s = StatCollector.translateToLocal("gui.quiver");
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(guiTextures);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
    }
}
