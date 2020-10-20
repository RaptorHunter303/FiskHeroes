package com.fiskmods.heroes.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.TextureHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public abstract class GuiPickName extends GuiScreen
{
    public static List<String> recentlyUsed = new ArrayList<>();

    private final String title;
    private String name = "";

    public GuiPickName(String title)
    {
        this.title = title;
    }

    @Override
    public void initGui()
    {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 155, height / 2 + 25, 150, 20, I18n.format("gui.disguise.done")));
        buttonList.add(new GuiButton(1, width / 2 + 5, height / 2 + 25, 150, 20, I18n.format("gui.disguise.cancel")));
        buttonList.add(new GuiButton(2, width / 2 - 132, height / 2 - 12, 32, 20, I18n.format("gui.disguise.clear")));
        buttonList.add(new GuiButton(3, width / 2 + 100, height / 2 - 12, 32, 20, I18n.format("gui.disguise.sort")));
    }

    public abstract void onDone(String finalName);

    @Override
    protected void actionPerformed(GuiButton button)
    {
        int id = button.id;

        if (id == 0)
        {
            select();
            mc.thePlayer.closeScreen();
        }
        else if (id == 1)
        {
            mc.thePlayer.closeScreen();
        }
        else if (id == 2)
        {
            recentlyUsed.clear();
        }
        else if (id == 3)
        {
            Collections.sort(recentlyUsed);
        }

        super.actionPerformed(button);
    }

    private void select()
    {
        onDone(name);
    }

    @Override
    protected void keyTyped(char character, int key)
    {
        super.keyTyped(character, key);
        Keyboard.enableRepeatEvents(true);

        if (ChatAllowedCharacters.isAllowedCharacter(character) && fontRendererObj.getStringWidth(name + character + "_") < 170)
        {
            name += character;
        }

        if (name.length() > 0 && Keyboard.isKeyDown(Keyboard.KEY_BACK))
        {
            if (isCtrlKeyDown())
            {
                name = name.substring(0, name.contains(" ") ? name.lastIndexOf(" ") : 0);
            }
            else
            {
                name = name.substring(0, name.length() - 1);
            }
        }

        if (key == Keyboard.KEY_RETURN)
        {
            select();
            mc.thePlayer.closeScreen();
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
        drawDefaultBackground();
        int x = width / 2 - 45;
        int y = height / 2 - 30;
        int additionalWidth = 40;
        drawCenteredString(fontRendererObj, title, width / 2, y - 15, 16777215);

        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png"));
        drawTexturedModalRect(x, y, 80, 4, 90, 12);
        drawTexturedModalRect(x - additionalWidth, y, 80, 4, 89, 12);
        drawTexturedModalRect(x + additionalWidth, y, 81, 4, 89, 12);
        drawString(fontRendererObj, name + (mc.thePlayer.ticksExisted % 20 >= 10 ? "" : "_"), x + 2 - additionalWidth, y + 2, 0xffffff);

        int displayY = y + 20;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0, 0, 0, 0.4F);
        drawTexturedModalRect(x - 10 - additionalWidth, displayY - 2, 0, 0, 191, 20);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);

        for (int i = 0; i < recentlyUsed.size(); ++i)
        {
            String s = recentlyUsed.get(i);
            int displayX = x - 8 - additionalWidth + i * 19;

            if (mouseX >= displayX && mouseX < displayX + 16 && mouseY >= displayY && mouseY < displayY + 16)
            {
                Color color = new Color(0x00FF00);

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                SHRenderHelper.setGlColor(color);
                drawTexturedModalRect(displayX - 2, displayY - 2, 0, 0, 20, 20);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                drawCenteredString(fontRendererObj, EnumChatFormatting.ITALIC + s, displayX + 8, displayY + 20, Color.LIGHT_GRAY.getRGB());
                GL11.glColor4f(1, 1, 1, 1);

                if (Mouse.isButtonDown(0))
                {
                    onDone(name = s);
                    mc.thePlayer.closeScreen();
                }
            }

            mc.renderEngine.bindTexture(TextureHelper.getSkin(s));

            float scale = 0.5F;
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            GL11.glTranslatef(displayX, displayY, 0);
            GL11.glScalef(scale, 0.5F * scale, scale);
            GL11.glTranslatef(-displayX, -displayY, 0);
            drawTexturedModalRect(displayX, displayY, 32, 64, 32, 64);
            GL11.glPopMatrix();
            scale = 0.5F + 0.0625F;
            GL11.glPushMatrix();
            GL11.glTranslatef(displayX - 1, displayY - 1, 0);
            GL11.glScalef(scale, 0.5F * scale, scale);
            GL11.glTranslatef(-displayX + 1, -displayY + 1, 0);
            drawTexturedModalRect(displayX - 1, displayY - 1, 160, 64, 32, 64);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
