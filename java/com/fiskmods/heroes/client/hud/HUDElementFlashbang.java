package com.fiskmods.heroes.client.hud;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class HUDElementFlashbang extends HUDElement
{
    private StatusEffect effect;

    public HUDElementFlashbang(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public boolean isVisible(ElementType type)
    {
        return type == ElementType.HELMET && effect != null;
    }

    @Override
    public void preRender(ElementType type, ScreenInfo screen, int mouseX, int mouseY, float partialTicks)
    {
        Tessellator tessellator = Tessellator.instance;
        float f = effect.duration - partialTicks;

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, Math.min(f / 20, 0.9F + MathHelper.sin(f / 8) * 0.1F));
        setupAlpha();

        tessellator.startDrawingQuads();
        tessellator.addVertex(x, y + height, -90);
        tessellator.addVertex(x + width, y + height, -90);
        tessellator.addVertex(x + width, y, -90);
        tessellator.addVertex(x, y, -90);
        tessellator.draw();

        finishAlpha();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    @Override
    public void updateTick()
    {
        effect = StatusEffect.get(mc.thePlayer, StatEffect.FLASHBANG);
    }
}
