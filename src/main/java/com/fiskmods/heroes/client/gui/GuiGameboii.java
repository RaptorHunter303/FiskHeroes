package com.fiskmods.heroes.client.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.gameboii.GameboiiSaveType;
import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.GameboiiCartridge;
import com.fiskmods.heroes.gameboii.IGameboiiGame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class GuiGameboii extends GuiScreen
{
    public final GameboiiCartridge cartridge;
    public final IGameboiiGame game;

    public boolean fullscreen;

    public GuiGameboii(GameboiiCartridge gameCartridge)
    {
        cartridge = gameCartridge;
        game = Gameboii.get(cartridge);
    }

    @Override
    public void initGui()
    {
        Gameboii.start(cartridge, GameboiiSaveType.FILE, 144 * 4, 144 * 3);
        mc.gameSettings.thirdPersonView = 0;
    }

    @Override
    protected void keyTyped(char character, int key)
    {
        if (isShiftKeyDown())
        {
            super.keyTyped(character, key);

//            if (key == Keyboard.KEY_F)
//            {
//                if (!(fullscreen = !fullscreen))
//                {
//                    //                    mc.skipRenderWorld = false;
//                }
//
//                return;
//            }
        }

        game.keyTyped(character, key);
    }

    @Override
    public void updateScreen()
    {
        if (mc.thePlayer.isDead)
        {
            mc.thePlayer.closeScreen();
            return;
        }

        //        if (fullscreen)
        //        {
        //            mc.skipRenderWorld = true;
        //        }

        try
        {
            game.tick();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            mc.thePlayer.closeScreen();
        }
    }

    @Override
    public void onGuiClosed()
    {
        Gameboii.quit(game);
//        mc.skipRenderWorld = false;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (fullscreen)
        {
            float scale = height * 4 / 3F;

            GL11.glColor4f(0, 0, 0, 1);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            drawTexturedModalRect(0, 0, 0, 0, width, height);
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            GL11.glPushMatrix();
            GL11.glTranslatef((width - scale) / 2, 0, zLevel);
            GL11.glScalef(scale, scale, scale);
            game.draw(partialTicks);
            GL11.glPopMatrix();
        }
    }
}
