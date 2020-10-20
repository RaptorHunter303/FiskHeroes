package com.fiskmods.heroes.client.hud;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.item.IScopeWeapon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class HUDElementScope extends HUDElement
{
    public static final ResourceLocation SCOPE = new ResourceLocation(FiskHeroes.MODID, "textures/misc/scope.png");

    public HUDElementScope(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public boolean isVisible(ElementType type)
    {
        ItemStack stack = mc.thePlayer.getHeldItem();
        return type == ElementType.HELMET && mc.gameSettings.thirdPersonView == 0 && stack != null && stack.getItem() instanceof IScopeWeapon && ((IScopeWeapon) stack.getItem()).canUseScope(stack) && ((IScopeWeapon) stack.getItem()).isProperScope();
    }

    @Override
    public void preRender(ElementType type, ScreenInfo screen, int mouseX, int mouseY, float partialTicks)
    {
        float scope = Math.max(SHData.SCOPE_TIMER.interpolate(mc.thePlayer) * (1 - Math.abs(MathHelper.sin(mc.thePlayer.getSwingProgress(partialTicks) * (float) Math.PI))) * 2 - 1, 0);

        if (scope > 0)
        {
            Dimension dim = new Dimension(16, 9);
            Tessellator tessellator = Tessellator.instance;

            double scaleX = (double) dim.width / (width < 0 ? dim.width : width);
            double scaleY = (double) dim.height / (height < 0 ? dim.height : height);
            double scale = 1.0 / Math.min(scaleX, scaleY);

            dim.width *= scale;
            dim.height *= scale;

            Rectangle rect = new Rectangle(new Point(x + (width - dim.width) / 2, y + (height - dim.height) / 2), dim);
            double minX = rect.getMinX();
            double minY = rect.getMinY();
            double maxX = rect.getMaxX();
            double maxY = rect.getMaxY();
            int transparency = Math.round(255 * scope) & 0xFF;

            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(1, 1, 1, scope);
            mc.getTextureManager().bindTexture(SCOPE);
            setupAlpha();

            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(minX, maxY, -90, 0, 1);
            tessellator.addVertexWithUV(maxX, maxY, -90, 1, 1);
            tessellator.addVertexWithUV(maxX, minY, -90, 1, 0);
            tessellator.addVertexWithUV(minX, minY, -90, 0, 0);
            tessellator.draw();

            if (transparency > 4)
            {
                String[] astring = String.format("%.2f", MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw)).split("\\.");
                String[] astring1 = String.format("%.2f", mc.thePlayer.rotationPitch).split("\\.");

                if (astring.length > 1 && astring1.length > 1)
                {
                    int color = 0x9BFFD5 | transparency << 24;
                    int left = width / 2 + (int) Math.round(1.6 * scale) + 20;
                    int top = height / 2 + (int) Math.round(0.25 * scale);

                    mc.fontRenderer.drawString(astring[0], left - mc.fontRenderer.getStringWidth(astring[0]), top, color);
                    mc.fontRenderer.drawString("." + astring[1], left, top, color);
                    top += mc.fontRenderer.FONT_HEIGHT;
                    mc.fontRenderer.drawString(astring1[0], left - mc.fontRenderer.getStringWidth(astring1[0]), top, color);
                    mc.fontRenderer.drawString("." + astring1[1], left, top, color);
                }
            }

            finishAlpha();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
        }
    }
}
