//package com.fiskmods.heroes.client.hud; // TODO: 1.4 Combat
//
//import java.util.Map;
//
//import org.lwjgl.opengl.GL11;
//
//import com.fiskmods.heroes.SHConstants;
//import com.fiskmods.heroes.client.sound.SoundSH;
//import com.fiskmods.heroes.common.data.SHData;
//import com.fiskmods.heroes.common.hero.Hero;
//import com.fiskmods.heroes.common.move.MoveCommonHandler;
//import com.fiskmods.heroes.common.move.MoveEntry;
//import com.fiskmods.heroes.common.move.MoveSet;
//import com.fiskmods.heroes.helper.SHHelper;
//import com.fiskmods.heroes.helper.SHRenderHelper;
//import com.google.common.collect.ImmutableMap;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.resources.I18n;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.Vec3;
//import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
//
//public class HUDElementFocus extends HUDElement
//{
//    private Hero hero;
//    private MoveEntry entry;
//
//    private double maxMarker;
//    private int pitchIncr;
//
//    private Map<Double, Float> markerAnimation;
//
//    public HUDElementFocus(Minecraft mc)
//    {
//        super(mc);
//    }
//
//    @Override
//    public boolean isVisible(ElementType type)
//    {
//        return type == ElementType.HOTBAR && entry != null;
//    }
//
//    @Override
//    public void postRender(ElementType type, ScreenInfo screen, int mouseX, int mouseY, float partialTicks)
//    {
//        float focusTime = SHData.FOCUS_TIME.interpolate(mc.thePlayer);
//        float alpha = Math.min(0.4F + 0.6F * focusTime / SHConstants.TICKS_FOCUS_START, 1);
//
//        setupAlpha();
//
//        if (alpha > 0)
//        {
//            MoveSet set = entry.getParent();
//            Vec3 color = SHRenderHelper.getColorFromHex(set.color);
//
//            short barWidth = 75;
//            float focus = SHData.FOCUS.interpolate(mc.thePlayer);
//            float max = MoveCommonHandler.getMaxFocus(mc.thePlayer);
//            float f = focusTime / SHConstants.TICKS_FOCUS_HOLD;
//            float f1 = focus / max * barWidth;
//
//            GL11.glColor4f(1, 1, 1, alpha);
//            mc.getTextureManager().bindTexture(ICONS);
//            drawTexturedModalRect(x, y, 24, 140, width, height);
//
//            for (double d : set.getMarkers(entry.move))
//            {
//                if (d <= max)
//                {
//                    drawTexturedModalRect(x + d / max * barWidth, y - 1, 118, 140, 5, 13);
//                }
//            }
//
//            GL11.glColor4f((float) color.xCoord, (float) color.yCoord, (float) color.zCoord, alpha);
//            drawTexturedModalRect(x + 2, y + 2, 26, 153, f1, 9);
//
//            if (f > 0 && (f1 = f * 9) > 0)
//            {
//                GL11.glColor4f(f >= 0.5F ? 1 - (f - 0.5F) * 2 : 1, f * 2, 0, alpha);
//                drawTexturedModalRect(x + 79, y + 2 + 9 - f1, 103, 153 + 9 - f1, 3, f1);
//            }
//
//            GL11.glColor4f(1, 1, 1, alpha);
//
//            for (double d : set.getMarkers(entry.move))
//            {
//                if (d <= max && focus >= d)
//                {
//                    drawTexturedModalRect(x + d / max * barWidth, y - 1, 113, 140, 5, 13);
//                }
//            }
//
//            mc.getTextureManager().bindTexture(entry.icon);
//            Tessellator tessellator = Tessellator.instance;
//
////            for (int j = 0; j < 1; ++j)
//            {
//                tessellator.startDrawingQuads();
//                tessellator.addVertexWithUV(-8, 8, 8, 0, 1);
//                tessellator.addVertexWithUV(8, 8, 8, 1, 1);
//                tessellator.addVertexWithUV(8, -8, 8, 1, 0);
//                tessellator.addVertexWithUV(-8, -8, 8, 0, 0);
//
//                GL11.glPushMatrix();
//                GL11.glTranslatef(x - 14, y + height / 2, zLevel);
////                GL11.glRotatef(Minecraft.getSystemTime() / 30F - 90 * j, 0, 1, 0);
//                tessellator.draw();
//                GL11.glPopMatrix();
//            }
//
//            ImmutableMap<String, Number> modifiers = set.getModifiers(entry.move, focus);
//
//            if (modifiers != null)
//            {
//                for (f1 = 0; f1 < 2; ++f1)
//                {
//                    int opacity = Math.round((255 - 127 * f1) * alpha) << 24;
//                    int outline = opacity | set.color & 0x404040;
//                    int y1 = y - 12;
//
//                    for (Map.Entry<String, Number> e : modifiers.entrySet())
//                    {
//                        String s = I18n.format(entry.move.localizeModifier(e.getKey()));
//                        int x1 = x + width / 4 * 3;
//
//                        if (f1 == 0)
//                        {
//                            s = entry.move.formatModifier(e.getKey(), e.getValue()) + "x ";
//                            x1 -= mc.fontRenderer.getStringWidth(s);
//                        }
//
//                        mc.fontRenderer.drawString(s, x1 - 1, y1, outline);
//                        mc.fontRenderer.drawString(s, x1 + 1, y1, outline);
//                        mc.fontRenderer.drawString(s, x1, y1 - 1, outline);
//                        mc.fontRenderer.drawString(s, x1, y1 + 1, outline);
//                        mc.fontRenderer.drawString(s, x1, y1, set.color);
//                        y1 -= mc.fontRenderer.FONT_HEIGHT;
//                    }
//                }
//            }
//        }
//
//        String s = I18n.format("gui.combat_mode");
//        int w = mc.fontRenderer.getStringWidth(s);
//
//        GL11.glPushMatrix();
//        GL11.glTranslatef(0, 0, 90);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        GL11.glColor4f(0.5F, 0, 0, 0.3F);
//        drawTexturedModalRect(screen.w / 2 - 91, screen.h - 22, 0, 0, 182, 22);
//        GL11.glColor4f(0, 0, 0, 0.6F);
//        drawTexturedModalRect((screen.w - w) / 2 - 1, screen.h - 1 - 16, 0, 0, w + 2, mc.fontRenderer.FONT_HEIGHT + 1);
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//
//        mc.fontRenderer.drawString(s, (screen.w - w) / 2, screen.h - 16, -1);
//        GL11.glPopMatrix();
//        finishAlpha();
//    }
//
//    @Override
//    public void updateTick()
//    {
//        if (!mc.isGamePaused())
//        {
//            hero = SHHelper.getHero(mc.thePlayer);
//            entry = MoveCommonHandler.getMove(mc.thePlayer, hero);
//
//            if (entry != null)
//            {
//                float focus = SHData.FOCUS.get(mc.thePlayer);
//                float prev = SHData.FOCUS.getPrev(mc.thePlayer);
//                boolean flag = false;
//                int i = 0;
//
//                for (double d : entry.getParent().getMarkers(entry.move))
//                {
//                    if (focus >= d && prev < d)
//                    {
//                        ++i;
//                    }
//                    else if (prev >= d && focus < d)
//                    {
//                        flag = true;
//                    }
//                }
//
//                for (; i > 0; --i)
//                {
//                    mc.getSoundHandler().playSound(SoundSH.makeSound(new ResourceLocation("note.pling"), false, 1, 0.5F + pitchIncr / 105F));
//                    pitchIncr += 15;
//                }
//
//                if (flag)
//                {
//                    if (focus == 0)
//                    {
//                        mc.getSoundHandler().playSound(SoundSH.makeSound(new ResourceLocation("random.fizz"), false, 1, 2));
//                    }
//                    else
//                    {
//                        mc.getSoundHandler().playSound(SoundSH.makeSound(new ResourceLocation("random.burp"), false, 1, 1));
//                    }
//
//                    pitchIncr = 0;
//                }
//
//                if (pitchIncr > 0)
//                {
//                    --pitchIncr;
//                }
//            }
//            else
//            {
//                maxMarker = 0;
//                pitchIncr = 0;
//            }
//        }
//    }
//
//    public void drawTexturedModalRect(double x, double y, double u, double v, double w, double h)
//    {
//        Tessellator tessellator = Tessellator.instance;
//        float f = 0.00390625F;
//
//        tessellator.startDrawingQuads();
//        tessellator.addVertexWithUV(x + 0, y + h, zLevel, (u + 0) * f, (v + h) * f);
//        tessellator.addVertexWithUV(x + w, y + h, zLevel, (u + w) * f, (v + h) * f);
//        tessellator.addVertexWithUV(x + w, y + 0, zLevel, (u + w) * f, (v + 0) * f);
//        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, (u + 0) * f, (v + 0) * f);
//        tessellator.draw();
//    }
//}
