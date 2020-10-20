package com.fiskmods.heroes.client.gui;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.container.ContainerDisplayStand;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiDisplayStand extends GuiContainer
{
    private static final ResourceLocation guiTextures = new ResourceLocation(FiskHeroes.MODID, "textures/gui/container/display_stand.png");

    public GuiDisplayStand(InventoryPlayer inventoryPlayer, TileEntityDisplayStand tile)
    {
        super(new ContainerDisplayStand(inventoryPlayer, tile));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 94, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(guiTextures);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        GuiInventory.func_147046_a(guiLeft + 124, guiTop + 75, 30, guiLeft + 124 - mouseX, guiTop + 75 - 50 - mouseY, mc.thePlayer);
    }
}
