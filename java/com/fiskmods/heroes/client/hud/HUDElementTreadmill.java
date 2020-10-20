package com.fiskmods.heroes.client.hud;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.data.DataManager;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.SHPlayerData;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class HUDElementTreadmill extends HUDElement
{
    private KeyBinding nextKey;

    private boolean keyLeftPressed = false;
    private boolean keyRightPressed = false;

    public float timer;
    public float prevTimer;
    public int successCount;

    private boolean increase;
    private boolean success;

    public HUDElementTreadmill(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public boolean isVisible(ElementType type)
    {
        return type == ElementType.BOSSHEALTH && SpeedsterHelper.isOnTreadmill(mc.thePlayer);
    }

    @Override
    public void postRender(ElementType type, ScreenInfo screen, int mouseX, int mouseY, float partialTicks)
    {
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_BLEND);
        mc.getTextureManager().bindTexture(BARS);

        int cap = SHPlayerData.getData(mc.thePlayer).speedXpBarCap();
        int left = x - 91;

        if (cap > 0)
        {
            short barWidth = 182;
            int filled = (int) (SHData.SPEED_EXPERIENCE_BAR.get(mc.thePlayer) * (barWidth + 1));
            drawTexturedModalRect(left, y, 0, 128, barWidth, 5);

            if (filled > 0)
            {
                drawTexturedModalRect(left, y, 0, 133, filled, 5);
            }
        }

        if (SHData.SPEED_EXPERIENCE_LEVEL.get(mc.thePlayer) > 0)
        {
            String s = SHData.SPEED_EXPERIENCE_LEVEL.get(mc.thePlayer) + "";
            int x1 = x - (mc.fontRenderer.getStringWidth(s) + 8) / 2;
            int y1 = y - 1;

            mc.fontRenderer.drawString(s, x1 + 1, y1, 0);
            mc.fontRenderer.drawString(s, x1 - 1, y1, 0);
            mc.fontRenderer.drawString(s, x1, y1 + 1, 0);
            mc.fontRenderer.drawString(s, x1, y1 - 1, 0);
            mc.fontRenderer.drawString(s, x1, y1, 0xFCC705);

            GL11.glColor4f(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(ICONS);
            drawTexturedModalRect(x - 5 + (mc.fontRenderer.getStringWidth(s) - 8) / 2, y - 6, Ability.SUPER_SPEED.getX(), Ability.SUPER_SPEED.getY(), 18, 18);
        }

        left = x - 45;
        mc.getTextureManager().bindTexture(BARS);
        drawTexturedModalRect(left, y + 12, 0, 0, 91, 5);

        if (timer > 0)
        {
            drawTexturedModalRect(left, y + 12, 0, 5, (int) (SHRenderHelper.interpolate(timer, prevTimer) * 91), 5);
        }

        if (nextKey != null)
        {
            String s = GameSettings.getKeyDisplayString(nextKey.getKeyCode());
            int x1 = x - 48 - mc.fontRenderer.getStringWidth(s);
            int y1 = y + 11;

            mc.fontRenderer.drawString(s, x1 + 1, y1, 0);
            mc.fontRenderer.drawString(s, x1 - 1, y1, 0);
            mc.fontRenderer.drawString(s, x1, y1 + 1, 0);
            mc.fontRenderer.drawString(s, x1, y1 - 1, 0);
            mc.fontRenderer.drawString(s, x1, y1, -1);
        }

        String s = I18n.format("gui.treadmill.streak", successCount, 2 + SHData.SPEED_EXPERIENCE_LEVEL.get(mc.thePlayer));
        int x1 = x + 50;
        int y1 = y + 11;

        mc.fontRenderer.drawString(s, x1 + 1, y1, 0);
        mc.fontRenderer.drawString(s, x1 - 1, y1, 0);
        mc.fontRenderer.drawString(s, x1, y1 + 1, 0);
        mc.fontRenderer.drawString(s, x1, y1 - 1, 0);
        mc.fontRenderer.drawString(s, x1, y1, -1);

        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void updateTick()
    {
        if (!mc.isGamePaused())
        {
            prevTimer = timer;

            if (nextKey == null)
            {
                nextKey = mc.gameSettings.keyBindLeft;
            }

            if (SpeedsterHelper.isOnTreadmill(mc.thePlayer))
            {
                float increment = 0.15F * (1 + (float) SHData.SPEED_EXPERIENCE_LEVEL.get(mc.thePlayer) / 20);
                float prevTimer = timer;

                if (increase)
                {
                    if (timer < 1)
                    {
                        timer += increment;
                    }
                }
                else
                {
                    if (timer > 0)
                    {
                        timer -= increment;
                    }
                }

                if (timer >= 1)
                {
                    increase = false;
                }
                else if (timer <= 0)
                {
                    increase = true;
                }

                timer = MathHelper.clamp_float(timer, 0, 1);

                if (timer == 0)
                {
                    success = false;
                }

                if (!increase && timer < 0.66F && prevTimer >= 0.66F && !success)
                {
                    successCount = 0;
                    nextKey = mc.gameSettings.keyBindLeft;
                }

                boolean flag = successCount == 0;

                if (timer == 0 && successCount >= 2 + SHData.SPEED_EXPERIENCE_LEVEL.get(mc.thePlayer))
                {
                    successCount = 0;
                    DataManager.addSpeedExperience(mc.thePlayer, 1);
                }

                if (timer == 0 && flag && !SHData.TREADMILL_DECREASING.get(mc.thePlayer))
                {
                    SHData.TREADMILL_DECREASING.set(mc.thePlayer, flag);
                }
            }
        }
    }

    @Override
    public void keyPress()
    {
        if (SpeedsterHelper.isOnTreadmill(mc.thePlayer))
        {
            if (mc.gameSettings.keyBindLeft.getIsKeyPressed())
            {
                if (!keyLeftPressed)
                {
                    keyLeftPressed = true;
                    keyPress(mc.gameSettings.keyBindLeft);
                }
            }
            else
            {
                keyLeftPressed = false;
            }

            if (mc.gameSettings.keyBindRight.getIsKeyPressed())
            {
                if (!keyRightPressed)
                {
                    keyRightPressed = true;
                    keyPress(mc.gameSettings.keyBindRight);
                }
            }
            else
            {
                keyRightPressed = false;
            }
        }
    }

    private void keyPress(KeyBinding key)
    {
        if (key == nextKey && timer >= 0.66F && !success)
        {
            ++successCount;
            nextKey = key == mc.gameSettings.keyBindLeft ? mc.gameSettings.keyBindRight : mc.gameSettings.keyBindLeft;
            success = true;
            SHData.TREADMILL_DECREASING.set(mc.thePlayer, false);
        }
        else
        {
            successCount = 0;
            nextKey = mc.gameSettings.keyBindLeft;
        }
    }
}
