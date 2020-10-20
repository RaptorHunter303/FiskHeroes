package com.fiskmods.heroes.client.gui;

import java.util.List;
import java.util.function.Function;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.client.GuiScrollingList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

public class GuiStringList<T> extends GuiScrollingList
{
    protected final Minecraft mc;
    protected final int right;

    protected int scrollUpActionId;
    protected int scrollDownActionId;
    protected float initialMouseClickY = -2;
    protected float scrollFactor;
    protected float scrollDistance;
    protected int selectedIndex = -1;
    protected long lastClickTime = 0L;
    protected boolean field_25123_p = true;
    protected boolean field_27262_q;
    protected int field_27261_r;

    protected final IGuiStringList listener;
    protected final List<T> entries;
    public int hoveredIndex = -1;

    protected Function<T, String> stringFormat = Object::toString;
    protected Function<T, Integer> stringColor = t -> -1;

    private GuiStringList(Minecraft minecraft, IGuiStringList parent, List<T> list, int listWidth, int height, int top, int bottom, int left)
    {
        super(minecraft, listWidth, height, top, bottom, left, 11);
        right = listWidth + left;
        entries = list;
        listener = parent;
        mc = minecraft;
    }

    public GuiStringList(IGuiStringList parent, List<T> list, int x, int y, int listWidth, int listHeight)
    {
        this(Minecraft.getMinecraft(), parent, list, listWidth, listHeight, y, y + listHeight, x);
    }

    public GuiStringList setFormat(Function<T, String> text, Function<T, Integer> color)
    {
        stringFormat = text;
        stringColor = color;
        return this;
    }

    @Override
    public void func_27258_a(boolean p_27258_1_)
    {
        field_25123_p = p_27258_1_;
    }

    @Override
    protected void func_27259_a(boolean p_27259_1_, int p_27259_2_)
    {
        field_27262_q = p_27259_1_;
        field_27261_r = p_27259_2_;

        if (!p_27259_1_)
        {
            field_27261_r = 0;
        }
    }

    @Override
    protected int getSize()
    {
        return entries.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick)
    {
        listener.elementClicked(index, doubleClick);
    }

    @Override
    protected boolean isSelected(int index)
    {
        return listener.isSelected(index);
    }

    @Override
    protected void drawBackground()
    {
    }

    @Override
    protected int getContentHeight()
    {
        return getSize() * 11;
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int y, int var4, Tessellator tessellator)
    {
        T entry = entries.get(listIndex);

        if (entry != null)
        {
            boolean unicode = mc.fontRenderer.getUnicodeFlag();
            mc.fontRenderer.setUnicodeFlag(true);
            String s = stringFormat.apply(entry);
            String s1 = mc.fontRenderer.trimStringToWidth(s, listWidth - 10 - mc.fontRenderer.getStringWidth("..."));

            if (mc.fontRenderer.getStringWidth(s) > mc.fontRenderer.getStringWidth(s1))
            {
                s1 += "...";
            }

            mc.fontRenderer.drawString(s1, left + 3, y - 1, stringColor.apply(entry));
            mc.fontRenderer.setUnicodeFlag(unicode);
        }
    }

    @Override
    public int func_27256_c(int p_27256_1_, int p_27256_2_)
    {
        int var3 = left + 1;
        int var4 = left + listWidth - 7;
        int var5 = p_27256_2_ - top - field_27261_r + (int) scrollDistance - 4;
        int var6 = var5 / slotHeight;
        return p_27256_1_ >= var3 && p_27256_1_ <= var4 && var6 >= 0 && var5 >= 0 && var6 < getSize() ? var6 : -1;
    }

    @Override
    public void registerScrollButtons(List p_22240_1_, int p_22240_2_, int p_22240_3_)
    {
        scrollUpActionId = p_22240_2_;
        scrollDownActionId = p_22240_3_;
    }

    private void applyScrollLimits()
    {
        int i = getContentHeight() - (bottom - top - 4);

        if (i < 0)
        {
            i /= 2;
        }

        if (scrollDistance < 0)
        {
            scrollDistance = 0;
        }

        if (scrollDistance > i)
        {
            scrollDistance = i;
        }
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == scrollUpActionId)
            {
                scrollDistance -= slotHeight * 2 / 3;
                initialMouseClickY = -2;
                applyScrollLimits();
            }
            else if (button.id == scrollDownActionId)
            {
                scrollDistance += slotHeight * 2 / 3;
                initialMouseClickY = -2;
                applyScrollLimits();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        SHRenderHelper.startGlScissor(left, top, listWidth, bottom - top);
        drawBackground();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        int listLength = getSize();
        int scrollBarXStart = left + listWidth - 6;
        int scrollBarXEnd = scrollBarXStart + 6;
        int boxLeft = left;
        int boxRight = scrollBarXStart - 1;
        int i;
        int j;
        int k;
        int l;

        if (getContentHeight() <= listHeight)
        {
            boxRight = right - 1;
        }

        if (mouseY >= top && mouseY <= bottom)
        {
            i = mouseY - top - field_27261_r + (int) scrollDistance - 4;
            j = i / slotHeight;

            if (mouseX >= boxLeft && mouseX <= boxRight && j >= 0 && i >= 0 && j < listLength)
            {
                hoveredIndex = j;
            }
            else
            {
                hoveredIndex = -1;
            }
        }
        else
        {
            hoveredIndex = -1;
        }

        if (Mouse.isButtonDown(0))
        {
            if (initialMouseClickY == -1)
            {
                boolean flag = true;

                if (mouseY >= top && mouseY <= bottom)
                {
                    i = mouseY - top - field_27261_r + (int) scrollDistance - 4;
                    j = i / slotHeight;

                    if (mouseX >= boxLeft && mouseX <= boxRight && j >= 0 && i >= 0 && j < listLength)
                    {
                        boolean flag1 = j == selectedIndex && System.currentTimeMillis() - lastClickTime < 250L;
                        elementClicked(j, flag1);
                        selectedIndex = j;
                        lastClickTime = System.currentTimeMillis();
                    }
                    else if (mouseX >= boxLeft && mouseX <= boxRight && i < 0)
                    {
                        func_27255_a(mouseX - boxLeft, mouseY - top + (int) scrollDistance - 4);
                        flag = false;
                    }

                    if (mouseX >= scrollBarXStart && mouseX <= scrollBarXEnd)
                    {
                        scrollFactor = -1;
                        l = getContentHeight() - (bottom - top - 4);

                        if (l < 1)
                        {
                            l = 1;
                        }

                        k = (int) ((float) ((bottom - top) * (bottom - top)) / (float) getContentHeight());

                        if (k < 32)
                        {
                            k = 32;
                        }

                        if (k > bottom - top - 8)
                        {
                            k = bottom - top - 8;
                        }

                        scrollFactor /= (float) (bottom - top - k) / (float) l;
                    }
                    else
                    {
                        scrollFactor = 1;
                    }

                    if (flag)
                    {
                        initialMouseClickY = mouseY;
                    }
                    else
                    {
                        initialMouseClickY = -2;
                    }
                }
                else
                {
                    initialMouseClickY = -2;
                }
            }
            else if (initialMouseClickY >= 0)
            {
                scrollDistance -= (mouseY - initialMouseClickY) * scrollFactor;
                initialMouseClickY = mouseY;
            }
        }
        else
        {
            int wheel = Mouse.getDWheel();

            if (wheel != 0)
            {
                if (wheel > 0)
                {
                    wheel = -1;
                }
                else if (wheel < 0)
                {
                    wheel = 1;
                }

                scrollDistance += wheel * slotHeight / 2;
            }

            initialMouseClickY = -1;
        }

        applyScrollLimits();
        Tessellator tessellator = Tessellator.instance;

        if (mc.theWorld != null)
        {
            drawGradientRect(left, top, right, bottom, -1072689136, -804253680);
        }
        else
        {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_FOG);
            mc.renderEngine.bindTexture(Gui.optionsBackground);
            GL11.glColor4f(1, 1, 1, 1);
            float var17 = 32;
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(2105376);
            tessellator.addVertexWithUV(left, bottom, 0, left / var17, (bottom + (int) scrollDistance) / var17);
            tessellator.addVertexWithUV(right, bottom, 0, right / var17, (bottom + (int) scrollDistance) / var17);
            tessellator.addVertexWithUV(right, top, 0, right / var17, (top + (int) scrollDistance) / var17);
            tessellator.addVertexWithUV(left, top, 0, left / var17, (top + (int) scrollDistance) / var17);
            tessellator.draw();
        }
        // boxRight = listWidth / 2 - 92 - 16;
        i = top + 4 - (int) scrollDistance;

        if (field_27262_q)
        {
            func_27260_a(boxRight, i, tessellator);
        }

        int var14;

        for (j = 0; j < listLength; ++j)
        {
            l = i + j * slotHeight + field_27261_r;
            k = slotHeight - 4;

            if (l <= bottom && l + k >= top)
            {
                if (field_25123_p && isSelected(j))
                {
                    var14 = boxLeft;
                    GL11.glColor4f(1, 1, 1, 1);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    tessellator.setColorOpaque_I(8421504);
                    tessellator.addVertexWithUV(var14, l + k + 2, 0, 0, 1);
                    tessellator.addVertexWithUV(boxRight, l + k + 2, 0, 1, 1);
                    tessellator.addVertexWithUV(boxRight, l - 2, 0, 1, 0);
                    tessellator.addVertexWithUV(var14, l - 2, 0, 0, 0);
                    tessellator.setColorOpaque_I(0);
                    tessellator.addVertexWithUV(var14 + 1, l + k + 1, 0, 0, 1);
                    tessellator.addVertexWithUV(boxRight - 1, l + k + 1, 0, 1, 1);
                    tessellator.addVertexWithUV(boxRight - 1, l - 1, 0, 1, 0);
                    tessellator.addVertexWithUV(var14 + 1, l - 1, 0, 0, 0);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                drawSlot(j, boxRight, l, k, tessellator);
            }
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        byte var20 = 4;

        if (mc.theWorld == null)
        {
            overlayBackground(0, top, 255, 255);
            overlayBackground(bottom, listHeight, 255, 255);
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0, 0);
        tessellator.addVertexWithUV(left, top + var20, 0, 0, 1);
        tessellator.addVertexWithUV(right, top + var20, 0, 1, 1);
        tessellator.setColorRGBA_I(0, 255);
        tessellator.addVertexWithUV(right, top, 0, 1, 0);
        tessellator.addVertexWithUV(left, top, 0, 0, 0);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0, 255);
        tessellator.addVertexWithUV(left, bottom, 0, 0, 1);
        tessellator.addVertexWithUV(right, bottom, 0, 1, 1);
        tessellator.setColorRGBA_I(0, 0);
        tessellator.addVertexWithUV(right, bottom - var20, 0, 1, 0);
        tessellator.addVertexWithUV(left, bottom - var20, 0, 0, 0);
        tessellator.draw();
        l = getContentHeight() - (bottom - top - 4);

        if (l > 0)
        {
            k = (bottom - top) * (bottom - top) / getContentHeight();

            if (k < 32)
            {
                k = 32;
            }

            if (k > bottom - top - 8)
            {
                k = bottom - top - 8;
            }

            var14 = (int) scrollDistance * (bottom - top - k) / l + top;

            if (var14 < top)
            {
                var14 = top;
            }

            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(0, 255);
            tessellator.addVertexWithUV(scrollBarXStart, bottom, 0, 0, 1);
            tessellator.addVertexWithUV(scrollBarXEnd, bottom, 0, 1, 1);
            tessellator.addVertexWithUV(scrollBarXEnd, top, 0, 1, 0);
            tessellator.addVertexWithUV(scrollBarXStart, top, 0, 0, 0);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(8421504, 255);
            tessellator.addVertexWithUV(scrollBarXStart, var14 + k, 0, 0, 1);
            tessellator.addVertexWithUV(scrollBarXEnd, var14 + k, 0, 1, 1);
            tessellator.addVertexWithUV(scrollBarXEnd, var14, 0, 1, 0);
            tessellator.addVertexWithUV(scrollBarXStart, var14, 0, 0, 0);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(12632256, 255);
            tessellator.addVertexWithUV(scrollBarXStart, var14 + k - 1, 0, 0, 1);
            tessellator.addVertexWithUV(scrollBarXEnd - 1, var14 + k - 1, 0, 1, 1);
            tessellator.addVertexWithUV(scrollBarXEnd - 1, var14, 0, 1, 0);
            tessellator.addVertexWithUV(scrollBarXStart, var14, 0, 0, 0);
            tessellator.draw();
        }

        func_27257_b(mouseX, mouseY);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        SHRenderHelper.endGlScissor();
    }

    private void overlayBackground(int i, int j, int k, int l)
    {
        Tessellator tessellator = Tessellator.instance;
        mc.renderEngine.bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1, 1, 1, 1);
        float f = 32;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(4210752, l);
        tessellator.addVertexWithUV(0, j, 0, 0, j / f);
        tessellator.addVertexWithUV((double) listWidth + 30, j, 0, (listWidth + 30) / f, j / f);
        tessellator.setColorRGBA_I(4210752, k);
        tessellator.addVertexWithUV((double) listWidth + 30, i, 0, (listWidth + 30) / f, i / f);
        tessellator.addVertexWithUV(0, i, 0, 0, i / f);
        tessellator.draw();
    }

    public static interface IGuiStringList
    {
        void elementClicked(int index, boolean doubleClick);

        boolean isSelected(int index);
    }
}
