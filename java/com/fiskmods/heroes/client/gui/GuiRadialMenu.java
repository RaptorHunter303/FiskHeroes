package com.fiskmods.heroes.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import com.fiskmods.heroes.util.SHRenderHelper;
import com.google.common.util.concurrent.Runnables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public abstract class GuiRadialMenu<T> extends GuiScreen
{
    public final List<RadialEntry> slots = new ArrayList<>();

    private RadialEntry slotSelected;
    public int ticks;

    public GuiRadialMenu(Iterable<T> iter)
    {
        int index = -1;

        for (T t : iter)
        {
            slots.add(new RadialEntry(++index, t));
        }
    }

    public abstract KeyBinding getKeyBinding();

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        KeyBinding key = getKeyBinding();

        if (key == null)
        {
            mc.thePlayer.closeScreen();
            return;
        }

        if (!GameSettings.isKeyDown(key))
        {
            mc.displayGuiScreen(null);

            if (slotSelected != null)
            {
                onSelected(slotSelected.index, slotSelected.value, false);
            }
        }

        ++ticks;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0 && slotSelected != null)
        {
            onSelected(slotSelected.index, slotSelected.value, true);
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        List<Runnable> postRender = new ArrayList<>();
        List<Runnable> render = new ArrayList<>();
        Runnable tooltip = Runnables.doNothing();
        int x = width / 2;
        int y = height / 2;

        float angle = mouseAngle(x, y, mouseX, mouseY);
        float angleIncr = 360F / slots.size();
        float totalAngle = 0;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        slotSelected = null;

        for (int i = 1; i < slots.size() + 1; ++i)
        {
            int index = i == slots.size() ? 0 : i;
            RadialEntry entry = slots.get(index);

            boolean selected = index == 0 && slotSelected == null || angle >= totalAngle && angle < totalAngle + angleIncr;
            float radius = getRadius(index, entry.value, selected, partialTicks);
            int color = getColor(index, entry.value, selected);

            if (selected)
            {
                slotSelected = entry;
            }

            SHRenderHelper.setGlColor(SHRenderHelper.hexToRGB(color), 0.4F);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2i(x, y);

            for (float f = angleIncr; f >= 0; --f)
            {
                float radians = (float) Math.toRadians(f + totalAngle);
                double xPos = x + MathHelper.cos(radians) * radius;
                double yPos = y + MathHelper.sin(radians) * radius;

                if (f == (int) (angleIncr / 2))
                {
                    render.add(() -> render(index, entry.value, (int) xPos, (int) yPos, selected, mouseX, mouseY, partialTicks));
                    postRender.add(() -> postRender(index, entry.value, (int) xPos, (int) yPos, selected, mouseX, mouseY, partialTicks));

                    if (selected)
                    {
                        tooltip = () ->
                        {
                            List<String> list = getTooltip(index, entry.value);

                            if (list != null)
                            {
                                drawHoveringText(list, mouseX, mouseY, fontRendererObj);
                            }
                        };
                    }
                }

                GL11.glVertex2d(xPos, yPos);
            }

            totalAngle += angleIncr;
            GL11.glVertex2i(x, y);
            GL11.glEnd();
        }

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        render.forEach(Runnable::run);
        postRender.forEach(Runnable::run);

        if (tooltip != null)
        {
            tooltip.run();
        }

        GL11.glPopMatrix();
    }

    public float mouseAngle(int x, int y, int mouseX, int mouseY)
    {
        Vector2f baseVec = new Vector2f(1, 0);
        Vector2f mouseVec = new Vector2f(mouseX - x, mouseY - y);

        float angle = (float) (Math.acos(Vector2f.dot(baseVec, mouseVec) / (baseVec.length() * mouseVec.length())) * (180F / Math.PI));
        return mouseY < y ? 360 - angle : angle;
    }

    public abstract void render(int index, T value, int xPos, int yPos, boolean selected, int mouseX, int mouseY, float partialTicks);

    public abstract void onSelected(int index, T value, boolean mouseClicked);

    public void postRender(int index, T value, int xPos, int yPos, boolean selected, int mouseX, int mouseY, float partialTicks)
    {
    }

    public float getRadius(int index, T value, boolean selected, float partialTicks)
    {
        return Math.max(0, Math.min((ticks + partialTicks - index * 4F / slots.size()) * 40, 80));
    }

    public int getColor(int index, T value, boolean selected)
    {
        return 0x202020;
    }

    public List<String> getTooltip(int index, T value)
    {
        return null;
    }

    private class RadialEntry
    {
        private final int index;
        private final T value;

        public RadialEntry(int index, T value)
        {
            this.index = index;
            this.value = value;
        }
    }
}
