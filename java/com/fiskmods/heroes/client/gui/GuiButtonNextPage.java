package com.fiskmods.heroes.client.gui;

import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
public class GuiButtonNextPage extends GuiButton
{
    private final GuiSuperheroesBook parent;
    private final boolean backButton;

    public GuiButtonNextPage(int id, int x, int y, boolean flag, GuiSuperheroesBook gui)
    {
        super(id, x, y, 23, 13, "");
        backButton = flag;
        parent = gui;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (visible)
        {
            boolean flag = new Rectangle(xPosition, yPosition, width, height).contains(mouseX, mouseY);

            GL11.glColor4f(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(parent.book.getBackground());
            drawTexturedModalRect(xPosition, yPosition, flag ? 23 : 0, backButton ? 180 : 193, 23, 13, 512, 256);
        }
    }

    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int texWidth, int texHeight)
    {
        float f = 1F / texWidth;
        float f1 = 1F / texHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, zLevel, u * f, (v + height) * f1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, (u + width) * f, (v + height) * f1);
        tessellator.addVertexWithUV(x + width, y, zLevel, (u + width) * f, v * f1);
        tessellator.addVertexWithUV(x, y, zLevel, u * f, v * f1);
        tessellator.draw();
    }
}
