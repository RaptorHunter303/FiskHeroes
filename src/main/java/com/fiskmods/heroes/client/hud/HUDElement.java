package com.fiskmods.heroes.client.hud;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public abstract class HUDElement extends Gui
{
    public static final ResourceLocation BARS = new ResourceLocation(FiskHeroes.MODID, "textures/gui/bars.png");
    public static final ResourceLocation ICONS = new ResourceLocation(FiskHeroes.MODID, "textures/gui/icons.png");
    public static final ResourceLocation WIDGETS = new ResourceLocation(FiskHeroes.MODID, "textures/gui/widgets.png");

    protected final Minecraft mc;

    private static boolean alphaEnabled = false;

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public HUDElement(Minecraft minecraft)
    {
        mc = minecraft;
    }

    public final void setBounds(HUDBounds bounds, ScreenInfo i)
    {
        x = bounds.xPos.apply(i);
        y = bounds.yPos.apply(i);
        width = bounds.width != null ? bounds.width.apply(i) : 0;
        height = bounds.height != null ? bounds.height.apply(i) : 0;
    }

    public abstract boolean isVisible(ElementType type);

    public void preRender(ElementType type, ScreenInfo screen, int mouseX, int mouseY, float partialTicks)
    {
    }

    public void postRender(ElementType type, ScreenInfo screen, int mouseX, int mouseY, float partialTicks)
    {
    }

    public void updateTick()
    {
    }

    public void keyPress()
    {
    }

    protected void setupAlpha()
    {
        if (alphaEnabled)
        {
            return;
        }

        alphaEnabled = true;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    }

    protected void finishAlpha()
    {
        if (!alphaEnabled)
        {
            return;
        }

        alphaEnabled = false;

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
